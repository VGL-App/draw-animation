package com.vgl.draw.animation.feature.projects.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.feature.projects.create.NewProjectRoute
import kotlinx.serialization.Serializable

/** Navigation destination of the Create screen (spec projects, KB-3), opened from the project list. */
@Serializable
data object NewProjectDestination

/**
 * [onClose] leaves the screen without creating (close button, Back) and is also called after creating while
 * [onOpenProject] is null (no drawing screen yet, T-018). [onOpenProject] receives the project just created.
 */
fun NavGraphBuilder.newProjectScreen(onClose: () -> Unit, onOpenProject: ((ProjectId) -> Unit)?) {
    composable<NewProjectDestination> {
        NewProjectRoute(onClose = onClose, onOpenProject = onOpenProject)
    }
}
