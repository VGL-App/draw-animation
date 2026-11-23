package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.vgl.draw.animation.core.project.model.ProjectId

// Skeleton of step 3 (tests first): replaced by the real implementation in step 4.
@Composable
internal fun ProjectsRoute(
    viewModel: ProjectsViewModel = hiltViewModel(),
    onNewProject: (() -> Unit)?,
    onOpenProject: ((ProjectId) -> Unit)?,
    onOpenLessons: (() -> Unit)?,
    onOpenSettings: (() -> Unit)?,
    onImport: (() -> Unit)?,
    onFeatureDisabled: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = Unit

@Composable
fun ProjectsScreen(
    uiState: ProjectsUiState,
    thumbnails: Map<ProjectId, ImageBitmap?>,
    snackbarHostState: SnackbarHostState,
    onNewProject: () -> Unit,
    onProjectClick: (ProjectCardUi) -> Unit,
    onOpenLessons: () -> Unit,
    onOpenSettings: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    onThumbnailNeeded: (ProjectCardUi, Int) -> Unit = { _, _ -> },
) = Unit

/** Tags for UI tests and for the scenario files of the plans (uiautomator resource-id). */
object ProjectsTestTags {
    const val SCREEN = "projects_screen"
    const val EMPTY = "projects_empty"
    const val GRID = "projects_grid"
    const val NEW = "projects_new"
    const val LESSON = "projects_lesson"
    const val SETTINGS = "projects_settings"
    const val IMPORT = "projects_import"

    // ProjectId is a value class: its toString() is "ProjectId(value=…)", so the tag uses the raw value.
    fun card(id: ProjectId): String = "project_card_${id.value}"

    fun thumbnail(id: ProjectId): String = "project_thumbnail_${id.value}"
}
