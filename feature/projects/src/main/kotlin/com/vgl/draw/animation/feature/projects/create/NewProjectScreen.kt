package com.vgl.draw.animation.feature.projects.create

import android.content.ActivityNotFoundException
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vgl.draw.animation.core.ads.AdPlacement
import com.vgl.draw.animation.core.ads.InContentAds
import com.vgl.draw.animation.core.ads.NativeAdSlot
import com.vgl.draw.animation.core.ads.rememberAdSuppression
import com.vgl.draw.animation.core.ads.rememberShowFullScreenAd
import com.vgl.draw.animation.core.designsystem.component.PrimaryButton
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.feature.projects.R
import kotlinx.coroutines.launch

// Numbers of design/context/NewProjectScreen_default_*.md that have no Spacing token.
private val TopBarHeight = 64.dp
private val GroupTitleGap = 10.dp
private val SwatchGap = 12.dp
private val MaxContentWidth = 600.dp

/**
 * Stateful entry point used by navigation (spec projects, KB-3). [onClose] leaves without creating (close button,
 * Back, projects flag off), and after creating while [onOpenProject] is null (no drawing screen yet). [createAd] shows
 * `create_inter` and calls `onFinished` once it closed (at once when nothing shows); [onBeforePhotoPicker] and
 * [onPhotoPickerResult] hold and release the ads that show by themselves around the system photo picker (G-03, way
 * 1; null: [rememberAdSuppression]). [newProjectAd] draws `new_project_native`. Tests pass fakes for the four.
 *
 * The id of a created project is saved until the project opens, so a recreation while the interstitial still covers
 * the screen opens it at once; an `onFinished` of the composition left behind then opens nothing.
 */
@Composable
internal fun NewProjectRoute(
    viewModel: NewProjectViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onOpenProject: ((ProjectId) -> Unit)?,
    createAd: @Composable (onFinished: () -> Unit) -> () -> Unit = {
        rememberShowFullScreenAd(AdPlacement.CREATE_INTER, it)
    },
    onBeforePhotoPicker: (() -> Unit)? = null,
    onPhotoPickerResult: (() -> Unit)? = null,
    newProjectAd: @Composable (Modifier) -> Unit = {
        NativeAdSlot(AdPlacement.NEW_PROJECT_NATIVE, it, keepSpaceWhenEmpty = true)
    },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val show: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
    val comingSoon = stringResource(R.string.projects_coming_soon)
    val photoFailed = stringResource(R.string.new_project_photo_failed)
    val noSpace = stringResource(R.string.projects_message_no_space)
    val createFailed = stringResource(R.string.new_project_create_failed)

    val currentOnClose by rememberUpdatedState(onClose)
    val currentOnOpenProject by rememberUpdatedState(onOpenProject)
    // The project created and not opened yet; saved, so a recreation while the ad covers the screen opens it.
    // ProjectId is a value class and cannot go into a Bundle by itself: its value is kept.
    var pendingOpen by rememberSaveable { mutableStateOf<String?>(null) }
    // Set when this composition leaves: an onFinished it handed to the ad must not open the project again (the state
    // object of a composition that left still holds the id).
    val left = remember { LeftMark() }
    DisposableEffect(Unit) { onDispose { left.value = true } }
    val openPending: () -> Unit = {
        val id = pendingOpen
        if (id != null && !left.value) {
            pendingOpen = null
            currentOnOpenProject?.invoke(ProjectId(id)) ?: currentOnClose()
        }
    }
    val showAd = createAd(openPending)
    val currentShowAd by rememberUpdatedState(showAd)
    val pendingAtStart = remember { pendingOpen != null }
    LaunchedEffect(Unit) {
        // Built again while the interstitial covered the screen: open now, without another ad.
        if (pendingAtStart) openPending()
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NewProjectEffect.ProjectCreated -> {
                    pendingOpen = effect.id.value
                    // Created while the app was in the background: no ad when it comes back.
                    if (effect.interstitialAllowed && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        currentShowAd()
                    } else {
                        openPending()
                    }
                }
                NewProjectEffect.ShowComingSoon -> show(comingSoon)
                NewProjectEffect.ShowPhotoFailed -> show(photoFailed)
                NewProjectEffect.Close -> currentOnClose()
            }
        }
    }
    val error = (uiState as? NewProjectUiState.Ready)?.error
    LaunchedEffect(error) {
        if (error != null) {
            show(if (error == NewProjectError.NO_SPACE) noSpace else createFailed)
            viewModel.onErrorShown()
        }
    }
    // Always on: Back while creating does nothing, so the write is never cancelled half way.
    BackHandler {
        val creating = (viewModel.uiState.value as? NewProjectUiState.Ready)?.creating == true
        if (!creating) currentOnClose()
    }

    val suppression = rememberAdSuppression()
    val beforePicker = onBeforePhotoPicker ?: suppression::hold
    val afterPicker = onPhotoPickerResult ?: suppression::release
    val currentAfterPicker by rememberUpdatedState(afterPicker)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        currentAfterPicker()
        viewModel.onPhotoPicked(uri?.toString())
    }
    NewProjectScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onClose = onClose,
        onNameChange = viewModel::onNameChange,
        onAspectRatio = viewModel::onAspectRatio,
        onResolution = viewModel::onResolution,
        onFrameRate = viewModel::onFrameRate,
        onSolidBackground = viewModel::onSolidBackground,
        onPhotoClick = {
            beforePicker()
            try {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } catch (e: ActivityNotFoundException) {
                // Released even when opening failed (docs/ops/ads-guide.md, leaving the app).
                afterPicker()
                viewModel.onPhotoPickerFailed()
            }
        },
        onCreate = viewModel::onCreate,
        newProjectAd = newProjectAd,
    )
}

/** Whether a composition of [NewProjectRoute] has left. Not a state: nothing is drawn from it. */
private class LeftMark {
    var value = false
}

/**
 * Stateless screen: top bar, the choices in a scrolling column, "Create" pinned at the bottom. [newProjectAd] sits
 * between "Frame rate" and "Background", 24 dp from each, and takes no gap when it draws nothing. [scrollState] is a
 * parameter so screenshots can open scrolled to the end. On screens wider than [MaxContentWidth] the content is one
 * centered column of that width (spec question 21).
 */
@Composable
fun NewProjectScreen(
    uiState: NewProjectUiState,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit,
    onNameChange: (String) -> Unit,
    onAspectRatio: (AspectRatio) -> Unit,
    onResolution: (Resolution) -> Unit,
    onFrameRate: (FrameRate) -> Unit,
    onSolidBackground: (BackgroundColor) -> Unit,
    onPhotoClick: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
    newProjectAd: @Composable (Modifier) -> Unit = {
        NativeAdSlot(AdPlacement.NEW_PROJECT_NATIVE, it, keepSpaceWhenEmpty = true)
    },
    scrollState: ScrollState = rememberScrollState(),
) {
    val ready = uiState as? NewProjectUiState.Ready
    InContentAds(listOf(AdPlacement.NEW_PROJECT_NATIVE)) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag(NewProjectTags.SCREEN),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(Modifier.widthIn(max = MaxContentWidth).fillMaxHeight()) {
                TopBar(closeEnabled = ready?.creating != true, onClose = onClose)
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    if (ready != null) {
                        Choices(
                            state = ready,
                            onNameChange = onNameChange,
                            onAspectRatio = onAspectRatio,
                            onResolution = onResolution,
                            onFrameRate = onFrameRate,
                            onSolidBackground = onSolidBackground,
                            onPhotoClick = onPhotoClick,
                            newProjectAd = newProjectAd,
                            scrollState = scrollState,
                        )
                    }
                }
                PrimaryButton(
                    text = stringResource(R.string.new_project_create),
                    onClick = onCreate,
                    enabled = ready != null && !ready.creating && !ready.photoLoading,
                    icon = rememberVectorPainter(Icons.Filled.Check),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.lg)
                        .testTag(NewProjectTags.CREATE),
                )
            }
            SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun TopBar(closeEnabled: Boolean, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TopBarHeight)
            .padding(start = Spacing.xs, end = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose, enabled = closeEnabled, modifier = Modifier.testTag(NewProjectTags.CLOSE)) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.new_project_close),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = stringResource(R.string.new_project_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun Choices(
    state: NewProjectUiState.Ready,
    onNameChange: (String) -> Unit,
    onAspectRatio: (AspectRatio) -> Unit,
    onResolution: (Resolution) -> Unit,
    onFrameRate: (FrameRate) -> Unit,
    onSolidBackground: (BackgroundColor) -> Unit,
    onPhotoClick: () -> Unit,
    newProjectAd: @Composable (Modifier) -> Unit,
    scrollState: ScrollState,
) {
    val enabled = !state.creating
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag(NewProjectTags.CONTENT)
            .padding(start = Spacing.md, end = Spacing.md, top = Spacing.sm, bottom = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.new_project_name)) },
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().testTag(NewProjectTags.NAME_FIELD),
        )
        Group(stringResource(R.string.new_project_canvas)) {
            Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                AspectRatio.entries.forEach { ratio ->
                    RatioTile(
                        aspectRatio = ratio,
                        label = stringResource(ratio.labelRes),
                        selected = state.aspectRatio == ratio,
                        onClick = { onAspectRatio(ratio) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Group(stringResource(R.string.new_project_size)) {
            Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Resolution.entries.forEach { resolution ->
                    OptionChip(
                        label = stringResource(resolution.labelRes),
                        selected = state.resolution == resolution,
                        onClick = { onResolution(resolution) },
                        tag = NewProjectTags.size(resolution),
                    )
                }
            }
        }
        // One item of the column: the slot adds its own 24 dp gap only when it draws something.
        Column {
            Group(stringResource(R.string.new_project_frame_rate)) {
                Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FrameRate.entries.forEach { rate ->
                        val selected = state.frameRate == rate
                        OptionChip(
                            label = if (selected) {
                                stringResource(R.string.new_project_fps, rate.fps)
                            } else {
                                stringResource(R.string.new_project_fps_number, rate.fps)
                            },
                            selected = selected,
                            onClick = { onFrameRate(rate) },
                            tag = NewProjectTags.fps(rate),
                        )
                    }
                }
            }
            newProjectAd(Modifier.fillMaxWidth().gapAboveWhenShown(Spacing.lg).testTag(NewProjectTags.AD))
        }
        Group(stringResource(R.string.new_project_background)) {
            Row(
                modifier = Modifier.selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(SwatchGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackgroundColor.entries.forEach { color ->
                    ColorSwatch(
                        color = color,
                        selected = state.background == BackgroundChoice.Solid(color),
                        enabled = enabled,
                        onClick = { onSolidBackground(color) },
                    )
                }
                PhotoButton(
                    preview = state.photoPreview,
                    selected = state.background == BackgroundChoice.Photo,
                    enabled = enabled && !state.photoLoading,
                    onClick = onPhotoClick,
                )
            }
        }
    }
}

@Composable
private fun Group(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(GroupTitleGap)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
        content()
    }
}

/** [gap] above the content while it has a height; nothing at all while it is 0 dp high (no ad). */
private fun Modifier.gapAboveWhenShown(gap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(minHeight = 0))
    if (placeable.height == 0) {
        layout(placeable.width, 0) { placeable.place(0, 0) }
    } else {
        val gapPx = gap.roundToPx()
        layout(placeable.width, placeable.height + gapPx) { placeable.place(0, gapPx) }
    }
}

private val AspectRatio.labelRes: Int
    get() = when (this) {
        AspectRatio.PORTRAIT_9_16 -> R.string.new_project_ratio_9_16
        AspectRatio.LANDSCAPE_16_9 -> R.string.new_project_ratio_16_9
        AspectRatio.SQUARE_1_1 -> R.string.new_project_ratio_1_1
    }

private val Resolution.labelRes: Int
    get() = when (this) {
        Resolution.P720 -> R.string.new_project_size_720p
        Resolution.P1080 -> R.string.new_project_size_1080p
    }

/** Tags for UI tests and for the scenario files of the plans (uiautomator resource-id). */
object NewProjectTags {
    const val SCREEN = "new_project_screen"
    const val CREATE = "new_project_create"
    const val CLOSE = "new_project_close"
    const val NAME_FIELD = "new_project_name"
    const val CONTENT = "new_project_content"
    const val AD = "new_project_ad"
    const val RATIO_9_16 = "new_project_ratio_9_16"
    const val RATIO_16_9 = "new_project_ratio_16_9"
    const val RATIO_1_1 = "new_project_ratio_1_1"
    const val SIZE_720P = "new_project_size_720p"
    const val SIZE_1080P = "new_project_size_1080p"
    const val FPS_6 = "new_project_fps_6"
    const val FPS_8 = "new_project_fps_8"
    const val FPS_12 = "new_project_fps_12"
    const val FPS_15 = "new_project_fps_15"
    const val FPS_24 = "new_project_fps_24"
    const val FPS_30 = "new_project_fps_30"
    const val BG_WHITE = "new_project_bg_white"
    const val BG_DARK = "new_project_bg_dark"
    const val BG_BLUE = "new_project_bg_blue"
    const val BG_PHOTO = "new_project_bg_photo"

    fun ratio(aspectRatio: AspectRatio): String = when (aspectRatio) {
        AspectRatio.PORTRAIT_9_16 -> RATIO_9_16
        AspectRatio.LANDSCAPE_16_9 -> RATIO_16_9
        AspectRatio.SQUARE_1_1 -> RATIO_1_1
    }

    fun size(resolution: Resolution): String = when (resolution) {
        Resolution.P720 -> SIZE_720P
        Resolution.P1080 -> SIZE_1080P
    }

    fun fps(frameRate: FrameRate): String = "new_project_fps_${frameRate.fps}"

    fun background(color: BackgroundColor): String = when (color) {
        BackgroundColor.WHITE -> BG_WHITE
        BackgroundColor.DARK -> BG_DARK
        BackgroundColor.BLUE -> BG_BLUE
    }
}
