package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material3.SnackbarDuration
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
import com.vgl.draw.animation.core.ads.InContentAds
import com.vgl.draw.animation.core.designsystem.component.PrimaryButton
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import com.vgl.draw.animation.core.project.model.ProjectId
import java.time.ZoneId
import kotlinx.coroutines.isActive
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
private val EmptyAdTopPadding = 24.dp

/**
 * Stateful entry point used by navigation. A null callback means its destination does not exist yet: the screen
 * shows "Coming soon" instead. [onFeatureDisabled] is called once, also across recreations, when the flag is off.
 * [onBackUpProject] receives the project chosen with "Back up to a file" (T-013).
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
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val comingSoon = stringResource(R.string.projects_coming_soon)
    val cannotOpen = stringResource(R.string.projects_cannot_open)
    val messages = ProjectsMessage.entries.associateWith { stringResource(it.textRes) }
    // Each message shows at once: the one showing is dismissed. This host only has short messages that hide by
    // themselves; "Duplicating…" has a host of its own (ProjectsScreen) and steps aside while this host shows one.
    val show: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
    val currentOpenProject by rememberUpdatedState(onOpenProject)
    val currentBackUpProject by rememberUpdatedState(onBackUpProject)
    val currentGridState by rememberUpdatedState(gridState)
    val currentMessages by rememberUpdatedState(messages)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProjectsEffect.OpenProject -> currentOpenProject?.invoke(effect.id) ?: show(comingSoon)
                ProjectsEffect.ShowCannotOpen -> show(cannotOpen)
                ProjectsEffect.ShowComingSoon -> show(comingSoon)
                is ProjectsEffect.ShowMessage -> show(currentMessages.getValue(effect.message))
                is ProjectsEffect.BackUpProject ->
                    currentBackUpProject?.invoke(effect.id, effect.name) ?: show(comingSoon)
                ProjectsEffect.ScrollToTop -> scope.launch { currentGridState.animateScrollToItem(0) }
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
    // Read once when a menu or dialog opens, for its "edited <when>".
    val nowMillis = remember(menuState.overlay) { System.currentTimeMillis() }
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
        menuState = menuState,
        nowMillis = nowMillis,
        onProjectLongClick = viewModel::onProjectLongClick,
        onMenuOpen = viewModel::onMenuOpen,
        onMenuRename = viewModel::onMenuRename,
        onMenuDuplicate = viewModel::onMenuDuplicate,
        onMenuBackUp = viewModel::onMenuBackUp,
        onMenuDelete = viewModel::onMenuDelete,
        onRenameConfirm = viewModel::onRenameConfirm,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDismissOverlay = viewModel::dismissOverlay,
    )
}

private val ProjectsMessage.textRes: Int
    get() = when (this) {
        ProjectsMessage.RENAME_FAILED -> R.string.projects_message_rename_failed
        ProjectsMessage.DELETE_FAILED -> R.string.projects_message_delete_failed
        ProjectsMessage.DUPLICATE_FAILED -> R.string.projects_message_duplicate_failed
        ProjectsMessage.NO_SPACE -> R.string.projects_message_no_space
    }

/**
 * Stateless screen. The top bar (wordmark, import, settings) is outside the scrolling content, so it stays in place.
 * [adSlots] draws the ads (plan T-010): a native medium card under the empty state's buttons, the bottom bar and the
 * grid's cards in the list state; in the list state the messages sit over the bottom bar.
 * [gridState] is remembered above the state switch, so the scroll position is saved with the back stack entry even
 * when the state passes through Loading (KB-11). [menuState] draws the project menu or one of its dialogs over the
 * list, and the "Duplicating…" message while a copy runs. "Duplicating…" has a host of its own, shown only while
 * [snackbarHostState] has no message, so other messages show at once over it. [nowMillis] and [zone] give the menu's
 * "edited <when>".
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
    adSlots: ProjectsAdSlots = DefaultProjectsAdSlots,
) {
    // Outside the state switch, so a running "Duplicating…" is kept when the screen moves between List and Empty.
    val duplicatingHostState = remember { SnackbarHostState() }
    InContentAds(projectsInContentAds()) {
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
                        emptyAd = { adModifier -> adSlots.EmptyAd(adModifier) },
                        modifier = Modifier.weight(1f),
                    )
                }
                is ProjectsUiState.List -> Column(Modifier.fillMaxSize()) {
                    ProjectsTopBar(uiState.showSettingsButton, onImport = onImport, onOpenSettings = onOpenSettings)
                    // Inside the List branch: leaving it (last project deleted) destroys the grid's ads.
                    val gridAds = adSlots.rememberGridAds()
                    adSlots.BottomBar { padding ->
                        Box(Modifier.fillMaxSize()) {
                            ProjectGrid(
                                cards = uiState.cards,
                                thumbnails = thumbnails,
                                gridState = gridState,
                                onNewProject = onNewProject,
                                onProjectClick = onProjectClick,
                                onProjectLongClick = onProjectLongClick,
                                onThumbnailNeeded = onThumbnailNeeded,
                                gridAds = gridAds,
                                bottomPadding = padding.calculateBottomPadding(),
                            )
                            // Over the bar, never over the ad.
                            ProjectsSnackbars(
                                snackbarHostState = snackbarHostState,
                                duplicatingHostState = duplicatingHostState,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = padding.calculateBottomPadding()),
                            )
                        }
                    }
                }
            }
            if (uiState !is ProjectsUiState.List) {
                ProjectsSnackbars(
                    snackbarHostState = snackbarHostState,
                    duplicatingHostState = duplicatingHostState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
    ProjectOverlay(
        menuState = menuState,
        thumbnails = thumbnails,
        nowMillis = nowMillis,
        zone = zone,
        onMenuOpen = onMenuOpen,
        onMenuRename = onMenuRename,
        onMenuDuplicate = onMenuDuplicate,
        onMenuBackUp = onMenuBackUp,
        onMenuDelete = onMenuDelete,
        onRenameConfirm = onRenameConfirm,
        onDeleteConfirm = onDeleteConfirm,
        onDismissOverlay = onDismissOverlay,
    )
    val duplicatingName = menuState.duplicatingName
    if (duplicatingName != null) {
        val text = stringResource(R.string.projects_duplicating, duplicatingName)
        // Never hides by itself and has its host to itself; dismissed with TalkBack, the loop shows it again since
        // the copy still runs. Leaving the composition (the copy ended) cancels it and the message goes away, also
        // while another message covers it.
        LaunchedEffect(text) {
            while (isActive) duplicatingHostState.showSnackbar(text, duration = SnackbarDuration.Indefinite)
        }
    }
}

/**
 * The two message hosts at one place. The host of "Duplicating…" is in the composition only while
 * [snackbarHostState] has no message: another message shows at once, and "Duplicating…" comes back after it, since
 * its showSnackbar is still waiting.
 */
@Composable
private fun ProjectsSnackbars(
    snackbarHostState: SnackbarHostState,
    duplicatingHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        if (snackbarHostState.currentSnackbarData == null) SnackbarHost(duplicatingHostState)
        SnackbarHost(snackbarHostState)
    }
}

/** The project menu or one of its dialogs, each in a window of its own over the screen. */
@Composable
private fun ProjectOverlay(
    menuState: ProjectsMenuState,
    thumbnails: Map<ProjectId, ImageBitmap?>,
    nowMillis: Long,
    zone: ZoneId,
    onMenuOpen: () -> Unit,
    onMenuRename: () -> Unit,
    onMenuDuplicate: () -> Unit,
    onMenuBackUp: () -> Unit,
    onMenuDelete: () -> Unit,
    onRenameConfirm: (String) -> Unit,
    onDeleteConfirm: () -> Unit,
    onDismissOverlay: () -> Unit,
) {
    when (val overlay = menuState.overlay) {
        ProjectsOverlay.None -> Unit
        is ProjectsOverlay.Menu -> ProjectMenuSheet(
            card = overlay.card,
            thumbnail = if (overlay.card.readable) thumbnails[overlay.card.id] else null,
            nowMillis = nowMillis,
            zone = zone,
            duplicateEnabled = menuState.duplicatingName == null,
            // Read on the activity window: the sheet's window adds no inset (contentWindowInsets is zero).
            bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            onDismissRequest = onDismissOverlay,
            onOpen = onMenuOpen,
            onRename = onMenuRename,
            onDuplicate = onMenuDuplicate,
            onBackUp = onMenuBackUp,
            onDelete = onMenuDelete,
        )
        is ProjectsOverlay.Rename -> RenameProjectDialog(
            initialName = overlay.card.name.orEmpty(),
            busy = menuState.busy,
            onConfirm = onRenameConfirm,
            onDismiss = onDismissOverlay,
        )
        is ProjectsOverlay.ConfirmDelete -> DeleteProjectDialog(
            name = overlay.card.name ?: stringResource(R.string.projects_card_unnamed),
            busy = menuState.busy,
            onConfirm = onDeleteConfirm,
            onDismiss = onDismissOverlay,
        )
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

/**
 * No project yet: picture, title, text, "New animation", "Try a free lesson", then [emptyAd] under the last button.
 * Scrolls on short screens.
 */
@Composable
private fun EmptyContent(
    showLessonButton: Boolean,
    onNewProject: () -> Unit,
    onOpenLessons: () -> Unit,
    emptyAd: @Composable (Modifier) -> Unit,
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
        // 24 dp plus the column's 12 dp gap: 36 dp under the last button, as in the design.
        emptyAd(Modifier.fillMaxWidth().padding(top = EmptyAdTopPadding).testTag(ProjectsTestTags.EMPTY_AD))
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
    const val EMPTY_AD = "projects_empty_ad"
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
