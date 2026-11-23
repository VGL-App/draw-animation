package com.vgl.draw.animation.feature.projects

import com.vgl.draw.animation.core.project.model.ProjectId

/** Every state of the project list screen. */
sealed interface ProjectsUiState {
    /** The first frame, before the projects and the flags are known. */
    data object Loading : ProjectsUiState

    /** The projects flag is off: the app replaces this screen with the sample screen (spec projects, KB-13). */
    data object Disabled : ProjectsUiState

    /** No project yet (KB-1). */
    data class Empty(val showLessonButton: Boolean, val showSettingsButton: Boolean) : ProjectsUiState

    /** At least one project, in the order of the repository: most recently updated first (KB-2). */
    data class List(val cards: kotlin.collections.List<ProjectCardUi>, val showSettingsButton: Boolean) :
        ProjectsUiState
}

/**
 * One card of the grid. [name] is null only for an unreadable project whose name cannot be read either; the card then
 * shows "Untitled project". An unreadable card ([readable] false) has no thumbnail and cannot be opened.
 */
data class ProjectCardUi(
    val id: ProjectId,
    val name: String?,
    val frameCount: Int,
    val fps: Int,
    val updatedAtMillis: Long,
    val readable: Boolean,
)

/** One-off events of the screen, each received once. */
sealed interface ProjectsEffect {
    data class OpenProject(val id: ProjectId) : ProjectsEffect

    /** The tapped project cannot be read. */
    data object ShowCannotOpen : ProjectsEffect

    /** The drawing screen is not available yet (editor flag off). */
    data object ShowComingSoon : ProjectsEffect
}
