package com.vgl.draw.animation.feature.projects

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.vgl.draw.animation.core.analytics.Analytics
import com.vgl.draw.animation.core.config.FeatureFlags
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.feature.projects.data.ProjectsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repository: ProjectsRepository,
    private val featureFlags: FeatureFlags,
    private val analytics: Analytics,
    private val logger: AppLogger,
) : ViewModel() {
    // Skeleton of step 3 (tests first): replaced by the real implementation in step 4.
    val uiState: StateFlow<ProjectsUiState> = MutableStateFlow(ProjectsUiState.Loading)

    val effects: Flow<ProjectsEffect> = emptyFlow()

    val thumbnails: StateFlow<Map<ProjectId, ImageBitmap?>> = MutableStateFlow(emptyMap())

    fun onProjectClick(card: ProjectCardUi) = Unit

    fun onThumbnailNeeded(card: ProjectCardUi, maxWidthPx: Int) = Unit

    internal companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val THUMBNAIL_CACHE_SIZE = 40
        const val LOG_TAG = "Projects"
    }
}
