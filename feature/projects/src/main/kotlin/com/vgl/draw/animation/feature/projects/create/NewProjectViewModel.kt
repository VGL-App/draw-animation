package com.vgl.draw.animation.feature.projects.create

import androidx.lifecycle.ViewModel
import com.vgl.draw.animation.core.analytics.Analytics
import com.vgl.draw.animation.core.config.FeatureFlags
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.feature.projects.data.NewProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

/** Test-first skeleton (T-011): filled in by the code commit. */
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

    fun onNameChange(text: String) = Unit

    fun onAspectRatio(aspectRatio: AspectRatio) = Unit

    fun onResolution(resolution: Resolution) = Unit

    fun onFrameRate(frameRate: FrameRate) = Unit

    fun onSolidBackground(color: BackgroundColor) = Unit

    fun onPhotoPicked(uri: String?) = Unit

    fun onPhotoPickerFailed() = Unit

    fun onCreate() = Unit

    fun onErrorShown() = Unit

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
