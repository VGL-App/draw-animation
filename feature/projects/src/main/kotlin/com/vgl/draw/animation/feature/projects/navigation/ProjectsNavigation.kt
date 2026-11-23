package com.vgl.draw.animation.feature.projects.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.feature.projects.ProjectsRoute
import kotlinx.serialization.Serializable

/** Navigation destination of the project list, the start screen after loading (spec projects, KB-1). */
@Serializable
data object ProjectsDestination

/**
 * A null callback means its destination does not exist yet: the screen shows "Coming soon" instead.
 * [onFeatureDisabled] is called once when the projects flag is off; the app then opens the sample screen (KB-13).
 * [onBackUpProject] receives the project chosen with "Back up to a file" in the project menu (T-013).
 */
fun NavGraphBuilder.projectsScreen(
    onNewProject: (() -> Unit)?,
    onOpenProject: ((ProjectId) -> Unit)?,
    onOpenLessons: (() -> Unit)?,
    onOpenSettings: (() -> Unit)?,
    onImport: (() -> Unit)?,
    onFeatureDisabled: () -> Unit,
    onBackUpProject: ((ProjectId, String) -> Unit)? = null,
) {
    composable<ProjectsDestination> {
        ProjectsRoute(
            onNewProject = onNewProject,
            onOpenProject = onOpenProject,
            onOpenLessons = onOpenLessons,
            onOpenSettings = onOpenSettings,
            onImport = onImport,
            onFeatureDisabled = onFeatureDisabled,
            onBackUpProject = onBackUpProject,
        )
    }
}
