package com.vgl.draw.animation.feature.projects.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vgl.draw.animation.core.analytics.Analytics
import com.vgl.draw.animation.core.analytics.AnalyticsEvent
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.config.FeatureFlags
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.repository.NewBackground
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectNames
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.data.NewProjectRepository
import com.vgl.draw.animation.feature.projects.data.PreparedPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The Create screen (spec projects, KB-3, KB-2, KB-12, KB-13). The preset choices are computed once when the screen
 * opens, with the first value of the Settings flag (off: 9:16 and 12, settings question 15), so a flag changing later
 * never replaces what the user chose. The editor and projects flags are observed for as long as the view model lives
 * (ADR-0003). One view model creates at most one project.
 *
 * The prepared photo stays here, never in [uiState] (it holds the bytes); choosing a color keeps it, so the preview
 * stays on the "Photo" button.
 */
@HiltViewModel
class NewProjectViewModel @Inject constructor(
    private val repository: NewProjectRepository,
    private val featureFlags: FeatureFlags,
    private val analytics: Analytics,
    private val logger: AppLogger,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewProjectUiState>(NewProjectUiState.Loading)
    val uiState: StateFlow<NewProjectUiState> = _uiState.asStateFlow()

    private val effectChannel = Channel<NewProjectEffect>(Channel.BUFFERED)
    val effects: Flow<NewProjectEffect> = effectChannel.receiveAsFlow()

    // The editor flag at this moment, read when "Create" is tapped.
    @Volatile
    private var editorEnabled = false

    private var presets: Choices? = null
    private var firstLayerName = ""
    private var photo: PreparedPhoto? = null
    private var photoJob: Job? = null
    private var created = false
    private var pendingOpen: ProjectId? = null

    /** A created project that has not been opened yet (the interstitial may still cover the screen). */
    val hasPendingOpen: Boolean get() = pendingOpen != null

    init {
        viewModelScope.launch { featureFlags.enabled(FeatureFlag.EDITOR).collect { editorEnabled = it } }
        viewModelScope.launch {
            featureFlags.enabled(FeatureFlag.PROJECTS).first { !it }
            logger.info(LOG_TAG, "Closed: projects off")
            effectChannel.trySend(NewProjectEffect.Close)
        }
        viewModelScope.launch {
            val defaults = repository.defaults()
            val settingsOn = featureFlags.enabled(FeatureFlag.SETTINGS).first()
            val choices = Choices(
                aspectRatio = if (settingsOn) defaults.aspectRatio else AspectRatio.PORTRAIT_9_16,
                resolution = Resolution.P1080,
                frameRate = if (settingsOn) defaults.frameRate else FrameRate.FPS_12,
                background = BackgroundChoice.Solid(BackgroundColor.WHITE),
            )
            presets = choices
            firstLayerName = defaults.firstLayerName
            _uiState.value = NewProjectUiState.Ready(
                name = defaults.defaultName,
                defaultName = defaults.defaultName,
                aspectRatio = choices.aspectRatio,
                resolution = choices.resolution,
                frameRate = choices.frameRate,
                background = choices.background,
                photoPreview = null,
                photoLoading = false,
                creating = false,
                error = null,
            )
            logger.info(LOG_TAG, "State: Ready")
        }
    }

    /** Keeps at most [ProjectNames.MAX_NAME_LENGTH] code points (a long paste is cut); not trimmed while typing. */
    fun onNameChange(text: String) {
        val max = ProjectNames.MAX_NAME_LENGTH
        val limited = if (text.codePointCount(0, text.length) > max) {
            text.substring(0, text.offsetByCodePoints(0, max))
        } else {
            text
        }
        updateReady { it.copy(name = limited) }
    }

    fun onAspectRatio(aspectRatio: AspectRatio) = updateReady { it.copy(aspectRatio = aspectRatio, error = null) }

    fun onResolution(resolution: Resolution) = updateReady { it.copy(resolution = resolution, error = null) }

    fun onFrameRate(frameRate: FrameRate) = updateReady { it.copy(frameRate = frameRate, error = null) }

    /** A color; a photo still being prepared is dropped, a photo already prepared is kept for its preview. */
    fun onSolidBackground(color: BackgroundColor) {
        if (_uiState.value !is NewProjectUiState.Ready) return
        photoJob?.cancel()
        photoJob = null
        updateReady {
            it.copy(background = BackgroundChoice.Solid(color), photoLoading = false, error = null)
        }
    }

    /** The photo picker's result; null when it was closed without a photo (the background stays as it was). */
    fun onPhotoPicked(uri: String?) {
        if (_uiState.value !is NewProjectUiState.Ready) return
        if (uri == null) {
            logger.info(LOG_TAG, "Photo picker closed")
            return
        }
        logger.info(LOG_TAG, "Photo picked")
        photoJob?.cancel()
        updateReady { it.copy(photoLoading = true, error = null) }
        photoJob = viewModelScope.launch {
            val prepared = repository.preparePhoto(uri)
            if (prepared == null) {
                updateReady { it.copy(photoLoading = false) }
                effectChannel.trySend(NewProjectEffect.ShowPhotoFailed)
                return@launch
            }
            photo = prepared
            updateReady {
                it.copy(background = BackgroundChoice.Photo, photoPreview = prepared.preview, photoLoading = false)
            }
        }
    }

    /** The photo picker could not be opened (no app answers the request). */
    fun onPhotoPickerFailed() {
        logger.warn(LOG_TAG, "Photo picker unavailable")
        effectChannel.trySend(NewProjectEffect.ShowPhotoFailed)
    }

    /**
     * Creates the project once. Ignored while creating, while a photo is prepared, and after a project was created:
     * the screen then leaves through [NewProjectEffect.ProjectCreated], and `creating` stays true until it does.
     */
    fun onCreate() {
        val state = _uiState.value as? NewProjectUiState.Ready ?: return
        if (state.creating || state.photoLoading || created) return
        if (!editorEnabled) {
            logger.info(LOG_TAG, "Create blocked: editor off")
            effectChannel.trySend(NewProjectEffect.ShowComingSoon)
            return
        }
        val background = when (val choice = state.background) {
            is BackgroundChoice.Solid -> NewBackground.Solid(choice.color)
            BackgroundChoice.Photo -> photo?.let { NewBackground.Photo(it.bytes, it.extension) } ?: return
        }
        val request = NewProject(
            name = state.name.trim().ifEmpty { state.defaultName },
            aspectRatio = state.aspectRatio,
            resolution = state.resolution,
            frameRate = state.frameRate,
            background = background,
            firstLayerName = firstLayerName,
        )
        val choices = Choices(state.aspectRatio, state.resolution, state.frameRate, state.background)
        updateReady { it.copy(creating = true, error = null) }
        viewModelScope.launch {
            val countBefore = repository.createdCount()
            when (val result = repository.create(request)) {
                is ProjectResult.Success -> {
                    created = true
                    pendingOpen = result.value.id
                    analytics.log(createEvent(choices))
                    logger.info(LOG_TAG, "Project created")
                    effectChannel.trySend(
                        NewProjectEffect.ProjectCreated(result.value.id, interstitialAllowed = countBefore >= 1),
                    )
                }
                is ProjectResult.Failure -> {
                    logger.warn(LOG_TAG, "Create failed: ${result.reason}")
                    val error = if (result.reason == ProjectFailure.NO_SPACE) {
                        NewProjectError.NO_SPACE
                    } else {
                        NewProjectError.WRITE_FAILED
                    }
                    updateReady { it.copy(creating = false, error = error) }
                }
            }
        }
    }

    /** The created project to open, once: later calls return null. */
    fun takePendingOpen(): ProjectId? = pendingOpen.also { pendingOpen = null }

    /** The error message was shown. */
    fun onErrorShown() = updateReady { it.copy(error = null) }

    private fun createEvent(choices: Choices) = AnalyticsEvent(
        EVENT_PROJECT_CREATE,
        mapOf(
            PARAM_RATIO to when (choices.aspectRatio) {
                AspectRatio.PORTRAIT_9_16 -> "9:16"
                AspectRatio.LANDSCAPE_16_9 -> "16:9"
                AspectRatio.SQUARE_1_1 -> "1:1"
            },
            PARAM_SIZE to when (choices.resolution) {
                Resolution.P720 -> "720p"
                Resolution.P1080 -> "1080p"
            },
            PARAM_FPS to choices.frameRate.fps.toString(),
            PARAM_BACKGROUND to when (val background = choices.background) {
                BackgroundChoice.Photo -> "photo"
                is BackgroundChoice.Solid -> when (background.color) {
                    BackgroundColor.WHITE -> "white"
                    BackgroundColor.DARK -> "dark"
                    BackgroundColor.BLUE -> "blue"
                }
            },
            // The name is not compared (question 3): only the four groups against the presets.
            PARAM_IS_DEFAULT to (choices == presets).toString(),
        ),
    )

    private fun updateReady(change: (NewProjectUiState.Ready) -> NewProjectUiState.Ready) {
        _uiState.update { if (it is NewProjectUiState.Ready) change(it) else it }
    }

    /** The four groups of choices, compared with the presets for `is_default`. */
    private data class Choices(
        val aspectRatio: AspectRatio,
        val resolution: Resolution,
        val frameRate: FrameRate,
        val background: BackgroundChoice,
    )

    internal companion object {
        const val LOG_TAG = "NewProject"

        /** The analytics event written when a project is created (spec section 6). */
        const val EVENT_PROJECT_CREATE = "project_create"
        const val PARAM_RATIO = "ratio"
        const val PARAM_SIZE = "size"
        const val PARAM_FPS = "fps"
        const val PARAM_BACKGROUND = "background"
        const val PARAM_IS_DEFAULT = "is_default"
    }
}
