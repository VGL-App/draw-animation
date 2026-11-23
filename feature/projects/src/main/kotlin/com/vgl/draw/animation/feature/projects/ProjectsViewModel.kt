package com.vgl.draw.animation.feature.projects

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vgl.draw.animation.core.analytics.Analytics
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.config.FeatureFlags
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.core.project.repository.ProjectNames
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.data.ProjectsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * The project list (spec projects, KB-1, KB-2, KB-11, KB-13). Flags are observed, never read once (ADR-0003).
 * No analytics event: spec section 6 has none for this screen; [analytics] is taken as in the sample pattern.
 *
 * Thumbnails are kept here, not in the repository: at most [THUMBNAIL_CACHE_SIZE] images, least recently used dropped
 * first, keyed by (id, updatedAtMillis) so an edited project loads its new image. Images of projects no longer in the
 * list are dropped each time the list changes, on the same collection as [uiState] (no second collector).
 *
 * The project menu (spec projects, KB-4 to KB-7) lives here too, so it survives a configuration change (KB-12). The
 * list is read once for [uiState] and [menuState] through a shared flow.
 */
@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repository: ProjectsRepository,
    private val featureFlags: FeatureFlags,
    @Suppress("unused") private val analytics: Analytics,
    private val logger: AppLogger,
) : ViewModel() {
    // The editor flag at this moment, read when a card is tapped. Set inside the combine of uiState.
    @Volatile
    private var editorEnabled = false

    private val lock = Any()
    private val cache = LinkedHashMap<ThumbnailKey, ImageBitmap?>(16, 0.75f, true)
    private val loading = mutableMapOf<ThumbnailKey, Job>()
    private val _thumbnails = MutableStateFlow<Map<ProjectId, ImageBitmap?>>(emptyMap())
    val thumbnails: StateFlow<Map<ProjectId, ImageBitmap?>> = _thumbnails.asStateFlow()

    private val effectChannel = Channel<ProjectsEffect>(Channel.BUFFERED)
    val effects: Flow<ProjectsEffect> = effectChannel.receiveAsFlow()

    private var lastLogged: String? = null

    // One read of the repository for the list and the menu; stops as soon as nobody collects (the 5 s wait is in the
    // stateIn of uiState and menuState).
    private val items = repository.items
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    // The source of truth of the menu: the functions below read and write it, also when nobody collects menuState.
    private val _menuState = MutableStateFlow(ProjectsMenuState())

    /** The menu and its dialogs. Collecting it also closes a menu whose project left the list. */
    val menuState: StateFlow<ProjectsMenuState> = _menuState
        .combine(items.onEach(::closeOverlayIfCardMissing)) { menu, _ -> menu }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ProjectsMenuState(),
        )

    val uiState: StateFlow<ProjectsUiState> = combine(
        items.onEach(::retainThumbnails),
        featureFlags.enabled(FeatureFlag.PROJECTS),
        featureFlags.enabled(FeatureFlag.LESSONS),
        featureFlags.enabled(FeatureFlag.SETTINGS),
        featureFlags.enabled(FeatureFlag.EDITOR),
    ) { items, projectsOn, lessonsOn, settingsOn, editorOn ->
        editorEnabled = editorOn
        when {
            !projectsOn -> ProjectsUiState.Disabled
            items.isEmpty() -> ProjectsUiState.Empty(showLessonButton = lessonsOn, showSettingsButton = settingsOn)
            else -> ProjectsUiState.List(items.map(::toCard), showSettingsButton = settingsOn)
        }.also(::logState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ProjectsUiState.Loading,
    )

    fun onProjectClick(card: ProjectCardUi) {
        val effect = when {
            !card.readable -> {
                logger.info(LOG_TAG, "Unreadable project tapped")
                ProjectsEffect.ShowCannotOpen
            }
            editorEnabled -> ProjectsEffect.OpenProject(card.id)
            else -> ProjectsEffect.ShowComingSoon
        }
        effectChannel.trySend(effect)
    }

    /** Opens the project menu (KB-4). Ignored while a menu or dialog is open. */
    fun onProjectLongClick(card: ProjectCardUi) {
        if (_menuState.value.overlay != ProjectsOverlay.None) return
        _menuState.update { it.copy(overlay = ProjectsOverlay.Menu(card)) }
        logger.info(LOG_TAG, "Menu opened")
    }

    /** "Open": the same way as a tap on the card, with the editor flag of this moment. */
    fun onMenuOpen() {
        val card = menuCard() ?: return
        _menuState.update { it.copy(overlay = ProjectsOverlay.None) }
        onProjectClick(card)
    }

    fun onMenuRename() {
        val card = menuCard() ?: return
        if (!card.readable) return
        _menuState.update { it.copy(overlay = ProjectsOverlay.Rename(card)) }
    }

    /** Saves the new name (KB-5). A blank name is ignored; the same name closes the dialog without writing. */
    fun onRenameConfirm(name: String) {
        val state = _menuState.value
        val card = (state.overlay as? ProjectsOverlay.Rename)?.card ?: return
        if (name.isBlank() || state.busy) return
        if (ProjectNames.normalize(name) == card.name) {
            _menuState.update { it.copy(overlay = ProjectsOverlay.None) }
            return
        }
        _menuState.update { it.copy(busy = true) }
        viewModelScope.launch {
            try {
                val result = repository.rename(card.id, name)
                _menuState.update { it.copy(overlay = ProjectsOverlay.None) }
                when (result) {
                    is ProjectResult.Success -> logger.info(LOG_TAG, "Rename done")
                    is ProjectResult.Failure -> {
                        logger.warn(LOG_TAG, "Rename failed: ${result.reason}")
                        val message = if (result.reason == ProjectFailure.NO_SPACE) {
                            ProjectsMessage.NO_SPACE
                        } else {
                            ProjectsMessage.RENAME_FAILED
                        }
                        effectChannel.trySend(ProjectsEffect.ShowMessage(message))
                    }
                }
            } finally {
                _menuState.update { it.copy(busy = false) }
            }
        }
    }

    /**
     * Copies the project of the open menu (KB-6). One copy at a time: while one runs, another is ignored. The copy
     * comes first in the list; once the list shows it, the grid scrolls to the top.
     */
    fun onMenuDuplicate() {
        val card = menuCard() ?: return
        _menuState.update { it.copy(overlay = ProjectsOverlay.None) }
        if (_menuState.value.duplicatingName != null || !card.readable) return
        _menuState.update { it.copy(duplicatingName = card.name.orEmpty()) }
        logger.info(LOG_TAG, "Duplicate started")
        viewModelScope.launch {
            try {
                val result = repository.duplicate(card.id)
                // The "Duplicating…" message goes away before any result message shows.
                _menuState.update { it.copy(duplicatingName = null) }
                when (result) {
                    is ProjectResult.Success -> {
                        logger.info(LOG_TAG, "Duplicate done")
                        // The repository sends the new list after duplicate returns: wait for the copy on top.
                        val shown = withTimeoutOrNull(SCROLL_WAIT_MILLIS) {
                            items.first { it.firstOrNull()?.id == result.value.id }
                        }
                        if (shown != null) effectChannel.trySend(ProjectsEffect.ScrollToTop)
                    }
                    is ProjectResult.Failure -> {
                        logger.warn(LOG_TAG, "Duplicate failed: ${result.reason}")
                        val message = if (result.reason == ProjectFailure.NO_SPACE) {
                            ProjectsMessage.NO_SPACE
                        } else {
                            ProjectsMessage.DUPLICATE_FAILED
                        }
                        effectChannel.trySend(ProjectsEffect.ShowMessage(message))
                    }
                }
            } finally {
                _menuState.update { it.copy(duplicatingName = null) }
            }
        }
    }

    /** Asks before deleting (KB-7), also for an unreadable project. */
    fun onMenuDelete() {
        val card = menuCard() ?: return
        _menuState.update { it.copy(overlay = ProjectsOverlay.ConfirmDelete(card)) }
    }

    fun onDeleteConfirm() {
        val state = _menuState.value
        val card = (state.overlay as? ProjectsOverlay.ConfirmDelete)?.card ?: return
        if (state.busy) return
        _menuState.update { it.copy(busy = true) }
        viewModelScope.launch {
            try {
                val result = repository.delete(card.id)
                _menuState.update { it.copy(overlay = ProjectsOverlay.None) }
                when (result) {
                    is ProjectResult.Success -> logger.info(LOG_TAG, "Delete done")
                    is ProjectResult.Failure -> {
                        logger.warn(LOG_TAG, "Delete failed: ${result.reason}")
                        effectChannel.trySend(ProjectsEffect.ShowMessage(ProjectsMessage.DELETE_FAILED))
                    }
                }
            } finally {
                _menuState.update { it.copy(busy = false) }
            }
        }
    }

    /** "Back up to a file": the route hands the project to the backup screen (T-013), or shows "Coming soon". */
    fun onMenuBackUp() {
        val card = menuCard() ?: return
        val name = card.name ?: return
        _menuState.update { it.copy(overlay = ProjectsOverlay.None) }
        effectChannel.trySend(ProjectsEffect.BackUpProject(card.id, name))
    }

    /** Back, a tap outside, a swipe down or "Cancel". Ignored while a rename or delete is written. */
    fun dismissOverlay() {
        if (_menuState.value.busy) return
        _menuState.update { it.copy(overlay = ProjectsOverlay.None) }
    }

    private fun menuCard(): ProjectCardUi? = (_menuState.value.overlay as? ProjectsOverlay.Menu)?.card

    /** The project of the open menu or dialog left the list (deleted elsewhere): close it. */
    private fun closeOverlayIfCardMissing(items: List<ProjectListItem>) {
        _menuState.update { state ->
            val card = when (val overlay = state.overlay) {
                ProjectsOverlay.None -> return@update state
                is ProjectsOverlay.Menu -> overlay.card
                is ProjectsOverlay.Rename -> overlay.card
                is ProjectsOverlay.ConfirmDelete -> overlay.card
            }
            if (state.busy || items.any { it.id == card.id }) state else state.copy(overlay = ProjectsOverlay.None)
        }
    }

    /** The card is on screen: loads its thumbnail unless it is in memory or loading. Unreadable cards get none. */
    fun onThumbnailNeeded(card: ProjectCardUi, maxWidthPx: Int) {
        if (!card.readable) return
        val key = ThumbnailKey(card.id, card.updatedAtMillis)
        synchronized(lock) {
            if (cache.containsKey(key)) {
                cache[key] // Marks the image as recently used.
                return
            }
            if (loading.containsKey(key)) return
            // A load of an older version of this project must not finish later and replace the newer image.
            loading.entries.removeAll { (other, job) ->
                (other.id == key.id && other != key).also { older -> if (older) job.cancel() }
            }
            // Lazy: on an immediate dispatcher the body could finish before the job is stored, leaving a stale entry.
            val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
                val image = repository.thumbnail(card.id, maxWidthPx)
                if (image == null) logger.warn(LOG_TAG, "Thumbnail not loaded")
                synchronized(lock) {
                    // Removed from loading: replaced by a newer version or the project left the list.
                    if (loading.remove(key) == null) return@launch
                    cache.keys.removeAll { it.id == key.id && it != key }
                    cache[key] = image
                    while (cache.size > THUMBNAIL_CACHE_SIZE) cache.remove(cache.keys.first())
                    publishThumbnails()
                }
            }
            loading[key] = job
            job.start()
        }
    }

    /** Drops images and loads of projects that left the list (deleted projects keep no image). */
    private fun retainThumbnails(items: List<ProjectListItem>) {
        val ids = items.mapTo(HashSet()) { it.id }
        synchronized(lock) {
            cache.keys.removeAll { it.id !in ids }
            loading.entries.removeAll { (key, job) ->
                (key.id !in ids).also { gone -> if (gone) job.cancel() }
            }
            publishThumbnails()
        }
    }

    // Called under [lock]. Iterating does not change the access order.
    private fun publishThumbnails() {
        _thumbnails.value = cache.entries.associate { it.key.id to it.value }
    }

    private fun toCard(item: ProjectListItem): ProjectCardUi = when (item) {
        is ProjectListItem.Readable -> ProjectCardUi(
            id = item.id,
            name = item.project.name,
            frameCount = item.project.frames.size,
            fps = item.project.frameRate.fps,
            updatedAtMillis = item.project.updatedAtMillis,
            readable = true,
        )
        is ProjectListItem.Unreadable -> ProjectCardUi(
            id = item.id,
            name = item.name,
            frameCount = 0,
            fps = 0,
            updatedAtMillis = 0L,
            readable = false,
        )
    }

    private fun logState(state: ProjectsUiState) {
        val description = when (state) {
            ProjectsUiState.Loading -> return
            ProjectsUiState.Disabled -> "Disabled"
            is ProjectsUiState.Empty -> "Empty"
            is ProjectsUiState.List -> "List with ${state.cards.size} cards"
        }
        if (description == lastLogged) return
        lastLogged = description
        logger.info(LOG_TAG, "State: $description")
    }

    private data class ThumbnailKey(val id: ProjectId, val updatedAtMillis: Long)

    internal companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val THUMBNAIL_CACHE_SIZE = 40
        const val LOG_TAG = "Projects"
        const val SCROLL_WAIT_MILLIS = 2_000L
    }
}
