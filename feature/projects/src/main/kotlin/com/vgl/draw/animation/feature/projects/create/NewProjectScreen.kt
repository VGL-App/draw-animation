package com.vgl.draw.animation.feature.projects.create

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import com.vgl.draw.animation.core.ads.AdPlacement
import com.vgl.draw.animation.core.ads.NativeAdSlot
import com.vgl.draw.animation.core.ads.rememberShowFullScreenAd
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution

/** Test-first skeleton (T-011): filled in by the code commit. */
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
    Box(Modifier.fillMaxSize().testTag(NewProjectTags.SCREEN))
}

/** Test-first skeleton (T-011): filled in by the code commit. */
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
    Box(modifier.fillMaxSize().testTag(NewProjectTags.SCREEN))
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
