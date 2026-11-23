package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vgl.draw.animation.core.designsystem.component.PrimaryButton
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import com.vgl.draw.animation.core.project.model.ProjectId
import java.time.ZoneId
import kotlinx.coroutines.launch

// Numbers of design/context/ProjectsScreen_*.md that have no Spacing token.
private val TopBarHeight = 64.dp
private val TopBarStartPadding = 20.dp
private val WordmarkDot = 14.dp
private val EmptyTopPadding = 28.dp
private val EmptyGap = 12.dp
private val ButtonMinHeight = 52.dp
private val ButtonCorner = 16.dp
private val ButtonIcon = 18.dp

/**
 * Stateful entry point used by navigation. A null callback means its destination does not exist yet: the screen
 * shows "Coming soon" instead. [onFeatureDisabled] is called once, also across recreations, when the flag is off.
 */
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
    onBackUpProject: ((ProjectId, String) -> Unit)? = null,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val thumbnails by viewModel.thumbnails.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val comingSoon = stringResource(R.string.projects_coming_soon)
    val cannotOpen = stringResource(R.string.projects_cannot_open)
    val show: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
    val currentOpenProject by rememberUpdatedState(onOpenProject)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProjectsEffect.OpenProject -> currentOpenProject?.invoke(effect.id) ?: show(comingSoon)
                ProjectsEffect.ShowCannotOpen -> show(cannotOpen)
                ProjectsEffect.ShowComingSoon -> show(comingSoon)
                is ProjectsEffect.ShowMessage, is ProjectsEffect.BackUpProject, ProjectsEffect.ScrollToTop -> Unit
            }
        }
    }
    var disabledHandled by rememberSaveable { mutableStateOf(false) }
    val currentOnFeatureDisabled by rememberUpdatedState(onFeatureDisabled)
    if (uiState is ProjectsUiState.Disabled && !disabledHandled) {
        LaunchedEffect(Unit) {
            disabledHandled = true
            currentOnFeatureDisabled()
        }
    }
    ProjectsScreen(
        uiState = uiState,
        thumbnails = thumbnails,
        snackbarHostState = snackbarHostState,
        onNewProject = { onNewProject?.invoke() ?: show(comingSoon) },
        onProjectClick = viewModel::onProjectClick,
        onOpenLessons = { onOpenLessons?.invoke() ?: show(comingSoon) },
        onOpenSettings = { onOpenSettings?.invoke() ?: show(comingSoon) },
        onImport = { onImport?.invoke() ?: show(comingSoon) },
        onThumbnailNeeded = viewModel::onThumbnailNeeded,
        gridState = gridState,
    )
}

/**
 * Stateless screen. The top bar (wordmark, import, settings) is outside the scrolling content, so it stays in place.
 * [gridState] is remembered above the state switch, so the scroll position is saved with the back stack entry even
 * when the state passes through Loading (KB-11).
 */
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
    menuState: ProjectsMenuState = ProjectsMenuState(),
    nowMillis: Long = 0L,
    zone: ZoneId = ZoneId.systemDefault(),
    onProjectLongClick: (ProjectCardUi) -> Unit = {},
    onMenuOpen: () -> Unit = {},
    onMenuRename: () -> Unit = {},
    onMenuDuplicate: () -> Unit = {},
    onMenuBackUp: () -> Unit = {},
    onMenuDelete: () -> Unit = {},
    onRenameConfirm: (String) -> Unit = {},
    onDeleteConfirm: () -> Unit = {},
    onDismissOverlay: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag(ProjectsTestTags.SCREEN),
    ) {
        when (uiState) {
            ProjectsUiState.Loading, ProjectsUiState.Disabled -> Unit
            is ProjectsUiState.Empty -> Column(Modifier.fillMaxSize()) {
                ProjectsTopBar(uiState.showSettingsButton, onImport = onImport, onOpenSettings = onOpenSettings)
                EmptyContent(
                    showLessonButton = uiState.showLessonButton,
                    onNewProject = onNewProject,
                    onOpenLessons = onOpenLessons,
                    modifier = Modifier.weight(1f),
                )
            }
            is ProjectsUiState.List -> Column(Modifier.fillMaxSize()) {
                ProjectsTopBar(uiState.showSettingsButton, onImport = onImport, onOpenSettings = onOpenSettings)
                ProjectGrid(
                    cards = uiState.cards,
                    thumbnails = thumbnails,
                    gridState = gridState,
                    onNewProject = onNewProject,
                    onProjectClick = onProjectClick,
                    onThumbnailNeeded = onThumbnailNeeded,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun ProjectsTopBar(showSettingsButton: Boolean, onImport: () -> Unit, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TopBarHeight)
            .padding(start = TopBarStartPadding, end = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(WordmarkDot).background(MaterialTheme.colorScheme.primary, CircleShape))
        Spacer(Modifier.width(Spacing.sm))
        Text(
            text = stringResource(R.string.projects_wordmark),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onImport, modifier = Modifier.testTag(ProjectsTestTags.IMPORT)) {
            Icon(
                painter = painterResource(R.drawable.ic_upload_file),
                contentDescription = stringResource(R.string.projects_import),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        if (showSettingsButton) {
            IconButton(onClick = onOpenSettings, modifier = Modifier.testTag(ProjectsTestTags.SETTINGS)) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.projects_settings),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

/** No project yet: picture, title, text, "New animation", "Try a free lesson". Scrolls on short screens. */
@Composable
private fun EmptyContent(
    showLessonButton: Boolean,
    onNewProject: () -> Unit,
    onOpenLessons: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .testTag(ProjectsTestTags.EMPTY)
            .padding(start = Spacing.lg, end = Spacing.lg, top = EmptyTopPadding, bottom = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EmptyGap),
    ) {
        EmptyIllustration()
        Text(
            text = stringResource(R.string.projects_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.projects_empty_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        NewAnimationButton(onClick = onNewProject, modifier = Modifier.fillMaxWidth())
        if (showLessonButton) {
            FilledTonalButton(
                onClick = onOpenLessons,
                shape = RoundedCornerShape(ButtonCorner),
                // Explicit colors: the design's tonal button is surfaceContainerHigh with onSurface text.
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.fillMaxWidth().heightIn(min = ButtonMinHeight).testTag(ProjectsTestTags.LESSON),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_school),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonIcon),
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = stringResource(R.string.projects_try_lesson),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
internal fun NewAnimationButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    PrimaryButton(
        text = stringResource(R.string.projects_new),
        onClick = onClick,
        icon = rememberVectorPainter(Icons.Rounded.Add),
        modifier = modifier.testTag(ProjectsTestTags.NEW),
    )
}

/** Tags for UI tests and for the scenario files of the plans (uiautomator resource-id). */
object ProjectsTestTags {
    const val SCREEN = "projects_screen"
    const val EMPTY = "projects_empty"
    const val GRID = "projects_grid"
    const val NEW = "projects_new"
    const val LESSON = "projects_lesson"
    const val SETTINGS = "projects_settings"
    const val IMPORT = "projects_import"
    const val MENU = "project_menu"
    const val MENU_OPEN = "project_menu_open"
    const val MENU_RENAME = "project_menu_rename"
    const val MENU_DUPLICATE = "project_menu_duplicate"
    const val MENU_BACKUP = "project_menu_backup"
    const val MENU_DELETE = "project_menu_delete"
    const val RENAME_DIALOG = "rename_dialog"
    const val RENAME_FIELD = "rename_field"
    const val RENAME_SAVE = "rename_save"
    const val RENAME_CANCEL = "rename_cancel"
    const val DELETE_DIALOG = "delete_dialog"
    const val DELETE_CONFIRM = "delete_confirm"
    const val DELETE_CANCEL = "delete_cancel"

    // ProjectId is a value class: its toString() is "ProjectId(value=…)", so the tag uses the raw value.
    fun card(id: ProjectId): String = "project_card_${id.value}"

    fun thumbnail(id: ProjectId): String = "project_thumbnail_${id.value}"
}
