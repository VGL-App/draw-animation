package com.vgl.draw.animation.feature.projects.create

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.vgl.draw.animation.core.ads.NativeAdAssets
import com.vgl.draw.animation.core.ads.NativeAdCard
import com.vgl.draw.animation.core.ads.NativeAdSize
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Resolution
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Screenshot tests (Roborazzi) of [NewProjectScreen]. Images named like a design image
 * (docs/features/projects/design/screens/) use the English texts of the design, the name "My animation 5" and a
 * fake native medium ad with the design's texts (tests have no ads controller). The second color swatch is the fixed
 * project color `#22262E`, not the design's `#E3E6EA` (plan T-011, section 2).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")
class NewProjectScreenScreenshotTest {
    @Test
    fun default_light() {
        captureRoboImage("src/test/screenshots/NewProjectScreen_default_light.png") {
            Screen(readyState(), darkTheme = false)
        }
    }

    @Test
    fun default_dark() {
        captureRoboImage("src/test/screenshots/NewProjectScreen_default_dark.png") {
            Screen(readyState(), darkTheme = true)
        }
    }

    /** The most content (the ad shows), scrolled to the end: the last row sits above "Create". */
    @Test
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi")
    fun default_small() {
        captureRoboImage("src/test/screenshots/NewProjectScreen_default_small.png") {
            Screen(readyState(), darkTheme = false, scrollState = ScrollState(Int.MAX_VALUE))
        }
    }

    @Test
    fun photo_light() {
        captureRoboImage("src/test/screenshots/NewProjectScreen_photo_light.png") {
            Screen(
                readyState(
                    aspectRatio = AspectRatio.LANDSCAPE_16_9,
                    resolution = Resolution.P720,
                    frameRate = FrameRate.FPS_24,
                    background = BackgroundChoice.Photo,
                    photoPreview = stripes(),
                ),
                darkTheme = false,
                scrollState = ScrollState(Int.MAX_VALUE),
            )
        }
    }

    @Test
    @Config(qualifiers = "en-w673dp-h841dp-xxhdpi")
    fun default_unfolded() {
        captureRoboImage("src/test/screenshots/NewProjectScreen_default_unfolded.png") {
            Screen(readyState(), darkTheme = false)
        }
    }

    @Composable
    private fun Screen(
        uiState: NewProjectUiState,
        darkTheme: Boolean,
        scrollState: ScrollState = rememberScrollState(),
    ) {
        AppTheme(darkTheme = darkTheme) {
            NewProjectScreen(
                uiState = uiState,
                snackbarHostState = remember { SnackbarHostState() },
                onClose = {},
                onNameChange = {},
                onAspectRatio = {},
                onResolution = {},
                onFrameRate = {},
                onSolidBackground = {},
                onPhotoClick = {},
                onCreate = {},
                newProjectAd = { modifier: Modifier -> NativeAdCard(designAdAssets, NativeAdSize.MEDIUM, modifier) },
                scrollState = scrollState,
            )
        }
    }

    /** A 144 x 144 preview of six vertical stripes in two colors, standing in for a photo. */
    private fun stripes(): ImageBitmap {
        val image = ImageBitmap(PREVIEW_PX, PREVIEW_PX)
        val canvas = Canvas(image)
        val first = Paint().apply { color = Color(0xFFE8A33D) }
        val second = Paint().apply { color = Color(0xFF3D7BE8) }
        val stripe = PREVIEW_PX / 6f
        for (i in 0 until 6) {
            canvas.drawRect(
                Rect(i * stripe, 0f, (i + 1) * stripe, PREVIEW_PX.toFloat()),
                if (i % 2 ==
                    0
                ) {
                    first
                } else {
                    second
                },
            )
        }
        return image
    }

    private companion object {
        const val PREVIEW_PX = 144

        /** Texts of the design's ad (design/context/NewProjectScreen_default_*.md). */
        val designAdAssets = NativeAdAssets(
            headline = "Sponsored app name",
            advertiser = "Advertiser",
            body = null,
            callToAction = "Install",
            icon = null,
            hasMedia = true,
        )
    }
}
