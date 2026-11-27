package com.vgl.draw.animation.feature.projects.create

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.designsystem.theme.ProjectBackgroundColors
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Resolution
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** JVM UI tests of the stateless [NewProjectScreen] (Robolectric). */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")
class NewProjectScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var closeClicks = 0
    private var createClicks = 0
    private var photoClicks = 0
    private val ratios = mutableListOf<AspectRatio>()
    private val sizes = mutableListOf<Resolution>()
    private val rates = mutableListOf<FrameRate>()
    private val colors = mutableListOf<BackgroundColor>()
    private val darkTheme = mutableStateOf(false)

    /** A fake ad slot [height] high, as the screenshot tests draw a native medium card. */
    private fun fakeAd(height: Dp): @Composable (Modifier) -> Unit = { modifier -> Box(modifier.height(height)) }

    private fun show(uiState: NewProjectUiState, ad: (@Composable (Modifier) -> Unit)? = fakeAd(0.dp)) {
        composeRule.setContent {
            AppTheme(darkTheme = darkTheme.value) {
                val snackbarHostState = remember { SnackbarHostState() }
                if (ad == null) {
                    NewProjectScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        onClose = { closeClicks++ },
                        onNameChange = {},
                        onAspectRatio = { ratios += it },
                        onResolution = { sizes += it },
                        onFrameRate = { rates += it },
                        onSolidBackground = { colors += it },
                        onPhotoClick = { photoClicks++ },
                        onCreate = { createClicks++ },
                    )
                } else {
                    NewProjectScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        onClose = { closeClicks++ },
                        onNameChange = {},
                        onAspectRatio = { ratios += it },
                        onResolution = { sizes += it },
                        onFrameRate = { rates += it },
                        onSolidBackground = { colors += it },
                        onPhotoClick = { photoClicks++ },
                        onCreate = { createClicks++ },
                        newProjectAd = ad,
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun bounds(tag: String): Rect = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInWindow

    private fun textBounds(text: String): Rect =
        composeRule.onNodeWithText(text, useUnmergedTree = true).fetchSemanticsNode().boundsInWindow

    private fun px(dp: Dp): Float = with(composeRule.density) { dp.toPx() }

    private fun assertNearDp(message: String, expected: Dp, actualPx: Float) {
        assertTrue(
            "$message: expected $expected, was ${actualPx / px(1.dp)} dp",
            abs(px(expected) - actualPx) <= px(1.dp),
        )
    }

    private fun tagStartsWith(prefix: String) = SemanticsMatcher("tag starts with $prefix") { node ->
        node.config.getOrElseNullable(SemanticsProperties.TestTag) { null }?.startsWith(prefix) == true
    }

    private fun selectedCount(prefix: String): Int =
        composeRule.onAllNodes(tagStartsWith(prefix) and isSelected()).fetchSemanticsNodes().size

    private fun drawWindow(): Bitmap {
        val view = (composeRule as AndroidComposeTestRule<*, *>).activity.window.decorView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        return bitmap
    }

    private fun assertColor(message: String, expected: Int, actual: Int) {
        val close = abs(android.graphics.Color.red(expected) - android.graphics.Color.red(actual)) <= 2 &&
            abs(android.graphics.Color.green(expected) - android.graphics.Color.green(actual)) <= 2 &&
            abs(android.graphics.Color.blue(expected) - android.graphics.Color.blue(actual)) <= 2
        assertTrue("$message: expected #%08X, was #%08X".format(expected, actual), close)
    }

    @Test
    fun eachGroup_hasExactlyOneSelected() {
        show(readyState())
        assertEquals(1, selectedCount("new_project_ratio_"))
        assertEquals(1, selectedCount("new_project_size_"))
        assertEquals(1, selectedCount("new_project_fps_"))
        assertEquals(1, selectedCount("new_project_bg_"))

        composeRule.onNodeWithTag(NewProjectTags.RATIO_16_9).performClick()
        composeRule.onNodeWithTag(NewProjectTags.SIZE_720P).performClick()
        composeRule.onNodeWithTag(NewProjectTags.FPS_24).performClick()
        composeRule.onNodeWithTag(NewProjectTags.BG_BLUE).performScrollTo().performClick()
        assertEquals(listOf(AspectRatio.LANDSCAPE_16_9), ratios)
        assertEquals(listOf(Resolution.P720), sizes)
        assertEquals(listOf(FrameRate.FPS_24), rates)
        assertEquals(listOf(BackgroundColor.BLUE), colors)

        composeRule.onNodeWithTag(NewProjectTags.BG_PHOTO).performScrollTo().performClick()
        assertEquals(1, photoClicks)
    }

    @Test
    fun selectedTile_showsSelectedState() {
        show(readyState(aspectRatio = AspectRatio.LANDSCAPE_16_9, background = BackgroundChoice.Photo))
        composeRule.onNodeWithTag(NewProjectTags.RATIO_16_9).assertIsSelected()
        composeRule.onNodeWithTag(NewProjectTags.RATIO_9_16).assertIsNotSelected()
        composeRule.onNodeWithTag(NewProjectTags.SIZE_1080P).assertIsSelected()
        composeRule.onNodeWithTag(NewProjectTags.FPS_12).assertIsSelected()
        composeRule.onNodeWithTag(NewProjectTags.BG_PHOTO).assertIsSelected()
        composeRule.onNodeWithTag(NewProjectTags.BG_WHITE).assertIsNotSelected()
        assertEquals(1, selectedCount("new_project_bg_"))
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun selectedColors() {
        var primaryContainer = 0
        var surfaceContainer = 0
        var surfaceVariant = 0
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                primaryContainer = MaterialTheme.colorScheme.primaryContainer.toArgb()
                surfaceContainer = MaterialTheme.colorScheme.surfaceContainer.toArgb()
                surfaceVariant = MaterialTheme.colorScheme.surfaceVariant.toArgb()
                NewProjectScreen(
                    uiState = readyState(aspectRatio = AspectRatio.LANDSCAPE_16_9, frameRate = FrameRate.FPS_24),
                    snackbarHostState = remember { SnackbarHostState() },
                    onClose = {},
                    onNameChange = {},
                    onAspectRatio = {},
                    onResolution = {},
                    onFrameRate = {},
                    onSolidBackground = {},
                    onPhotoClick = {},
                    onCreate = {},
                    newProjectAd = fakeAd(0.dp),
                )
            }
        }
        composeRule.waitForIdle()
        val bitmap = drawWindow()

        // Tiles: 8 dp in from the left edge, at mid height (away from the corners and the outline drawing).
        fun tileColor(tag: String): Int {
            val b = bounds(tag)
            return bitmap.getPixel((b.left + px(8.dp)).toInt(), b.center.y.toInt())
        }

        // Chips: 4 dp in from the left edge, at mid height (the label starts 12 dp in).
        fun chipColor(tag: String): Int {
            val b = bounds(tag)
            return bitmap.getPixel((b.left + px(4.dp)).toInt(), b.center.y.toInt())
        }
        assertColor("selected tile", primaryContainer, tileColor(NewProjectTags.RATIO_16_9))
        assertColor("other tile", surfaceContainer, tileColor(NewProjectTags.RATIO_9_16))
        assertColor("selected chip", primaryContainer, chipColor(NewProjectTags.FPS_24))
        assertColor("other chip", surfaceVariant, chipColor(NewProjectTags.FPS_12))
    }

    @Test
    fun texts_matchDesign() {
        show(readyState())
        listOf(
            "New animation", "Name", "Canvas", "9:16", "16:9", "1:1", "Size", "720p", "1080p", "Frame rate",
            "6", "8", "12 fps", "15", "24", "30", "Background", "Photo", "Create",
        ).forEach { text ->
            assertTrue(
                text,
                composeRule.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty(),
            )
        }
        assertTrue(composeRule.onAllNodesWithText("4:5", useUnmergedTree = true).fetchSemanticsNodes().isEmpty())
        // Only the chosen rate says "fps".
        assertTrue(composeRule.onAllNodesWithText("24 fps", useUnmergedTree = true).fetchSemanticsNodes().isEmpty())
        composeRule.onNodeWithTag(NewProjectTags.NAME_FIELD).assertExists()
        assertTrue(
            composeRule.onAllNodesWithText("My animation 5").fetchSemanticsNodes().isNotEmpty(),
        )
    }

    @Test
    fun photoButtonDisabledWhileLoading() {
        show(readyState(photoLoading = true))
        composeRule.onNodeWithTag(NewProjectTags.BG_PHOTO).assertIsNotEnabled()
        composeRule.onNodeWithTag(NewProjectTags.CREATE).assertIsNotEnabled()
    }

    @Test
    fun createEnabledWhenReady() {
        // Control of photoButtonDisabledWhileLoading and createDisabledWhileCreating.
        show(readyState())
        composeRule.onNodeWithTag(NewProjectTags.BG_PHOTO).assertIsEnabled()
        composeRule.onNodeWithTag(NewProjectTags.CREATE).assertIsEnabled().performClick()
        assertEquals(1, createClicks)
    }

    @Test
    fun createDisabledWhileCreating() {
        show(readyState(creating = true))
        composeRule.onNodeWithTag(NewProjectTags.CREATE).assertIsNotEnabled()
        composeRule.onNodeWithTag(NewProjectTags.CLOSE).assertIsNotEnabled()
    }

    @Test
    fun loading_createDisabledCloseWorks() {
        show(NewProjectUiState.Loading)
        composeRule.onNodeWithTag(NewProjectTags.CREATE).assertIsNotEnabled()
        composeRule.onNodeWithTag(NewProjectTags.CLOSE).performClick()
        assertEquals(1, closeClicks)
    }

    @Test
    fun close_callsOnClose() {
        show(readyState())
        assertEquals(0, closeClicks)
        composeRule.onNodeWithTag(NewProjectTags.CLOSE).performClick()
        assertEquals(1, closeClicks)
        assertEquals(0, createClicks)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun ad_betweenFrameRateAndBackground() {
        show(readyState(), ad = fakeAd(250.dp))
        val ad = bounds(NewProjectTags.AD)
        assertNearDp("ad height", 250.dp, ad.height)
        assertNearDp("frame rate to ad", 24.dp, ad.top - bounds(NewProjectTags.FPS_30).bottom)
        assertNearDp("ad to background", 24.dp, textBounds("Background").top - ad.bottom)
        composeRule.onNode(hasTestTag(NewProjectTags.AD) and hasAnyAncestor(hasTestTag(NewProjectTags.CONTENT)))
            .assertExists()
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun adSlotEmpty_frameRateToBackgroundIs24dp() {
        show(readyState(), ad = null)
        assertNearDp(
            "frame rate to background",
            24.dp,
            textBounds("Background").top - bounds(NewProjectTags.FPS_30).bottom,
        )
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun defaultAdSlot_noController_drawsNothing() {
        show(readyState(), ad = null)
        assertNearDp("default slot height", 0.dp, bounds(NewProjectTags.AD).height)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi")
    fun small_createVisibleAndLastRowReachable() {
        show(readyState(), ad = fakeAd(250.dp))
        val window = composeRule.onRoot().fetchSemanticsNode().boundsInWindow
        val create = bounds(NewProjectTags.CREATE)
        assertTrue("create $create in window $window", create.top >= window.top && create.bottom <= window.bottom)
        composeRule.onNodeWithTag(NewProjectTags.BG_PHOTO).performScrollTo()
        val photo = bounds(NewProjectTags.BG_PHOTO)
        val createAfter = bounds(NewProjectTags.CREATE)
        assertTrue("photo $photo above create $createAfter", photo.bottom <= createAfter.top)
        assertTrue("create still in window", createAfter.bottom <= window.bottom)
    }

    @Test
    @Config(qualifiers = "en-w673dp-h841dp-xxhdpi")
    fun unfolded_contentAtMost600dpCentered() {
        show(readyState())
        val window = composeRule.onRoot().fetchSemanticsNode().boundsInWindow
        val content = bounds(NewProjectTags.CONTENT)
        assertTrue("content width ${content.width / px(1.dp)} dp", content.width <= px(600.dp) + 1f)
        val leftMargin = content.left - window.left
        val rightMargin = window.right - content.right
        assertTrue("margins $leftMargin and $rightMargin", leftMargin > px(16.dp))
        assertTrue("margins $leftMargin and $rightMargin", abs(leftMargin - rightMargin) <= px(1.dp))
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun swatchColors_fixedInBothThemes() {
        show(readyState())
        composeRule.onNodeWithTag(NewProjectTags.BG_DARK).performScrollTo()
        composeRule.waitForIdle()
        fun centerOf(tag: String): Int {
            val b = bounds(tag)
            return drawWindow().getPixel(b.center.x.toInt(), b.center.y.toInt())
        }
        fun screenBackground(): Int =
            drawWindow().getPixel(px(4.dp).toInt(), bounds(NewProjectTags.BG_DARK).center.y.toInt())
        val lightBackground = screenBackground()
        assertColor("dark swatch, light theme", ProjectBackgroundColors.DARK, centerOf(NewProjectTags.BG_DARK))
        assertColor("blue swatch, light theme", ProjectBackgroundColors.BLUE, centerOf(NewProjectTags.BG_BLUE))

        darkTheme.value = true
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(NewProjectTags.BG_DARK).performScrollTo()
        composeRule.waitForIdle()
        assertColor("dark swatch, dark theme", ProjectBackgroundColors.DARK, centerOf(NewProjectTags.BG_DARK))
        assertColor("blue swatch, dark theme", ProjectBackgroundColors.BLUE, centerOf(NewProjectTags.BG_BLUE))
        // Control: the screen did change theme, its own background changed.
        assertTrue("background changed with the theme", lightBackground != screenBackground())
    }
}
