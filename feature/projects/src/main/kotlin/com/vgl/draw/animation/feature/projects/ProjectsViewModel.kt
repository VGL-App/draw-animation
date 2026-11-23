package com.vgl.draw.animation.feature.projects

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vgl.draw.animation.core.analytics.Analytics
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.config.FeatureFlags
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectListItem
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * The project list (spec projects, KB-1, KB-2, KB-11, KB-13). Flags are observed, never read once (ADR-0003).
 * No analytics event: spec section 6 has none for this screen; [analytics] is taken as in the sample pattern.
 *
 * Thumbnails are kept here, not in the repository: at most [THUMBNAIL_CACHE_SIZE] images, least recently used dropped
 * first, keyed by (id, updatedAtMillis) so an edited project loads its new image. Images of projects no longer in the
 * list are dropped each time the list changes, on the same collection as [uiState] (no second collector).
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

    val uiState: StateFlow<ProjectsUiState> = combine(
        repository.items.onEach(::retainThumbnails),
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
            // Lazy: on an immediate dispatcher the body could finish before the job is stored, leaving a stale entry.
            val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
                val image = repository.thumbnail(card.id, maxWidthPx)
                if (image == null) logger.warn(LOG_TAG, "Thumbnail not loaded")
                synchronized(lock) {
                    loading.remove(key)
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
    }
}
