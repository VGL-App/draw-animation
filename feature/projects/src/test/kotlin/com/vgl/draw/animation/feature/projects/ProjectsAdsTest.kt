package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.ads.AdFormat
import com.vgl.draw.animation.core.ads.AdPlacement
import com.vgl.draw.animation.core.ads.BottomAdBarDefaults
import com.vgl.draw.animation.core.ads.NativeAdCard
import com.vgl.draw.animation.core.ads.NativeAdSize
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import java.io.File
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The ads of the projects list (plan T-010) with fake slots ([FakeProjectsAdSlots]): where the empty state's card,
 * the bottom bar and the grid's cards are, and which state has which. The real slots of :core:ads draw nothing here,
 * since tests have no ads controller.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class ProjectsAdsTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val fake = FakeProjectsAdSlots()
    private var reservation: Dp = 0.dp
    private lateinit var scope: CoroutineScope

    private val empty = ProjectsUiState.Empty(showLessonButton = true, showSettingsButton = true)

    private fun list(count: Int) = ProjectsUiState.List((1..count).map { readableCard(it) }, showSettingsButton = true)

    private fun show(
        uiState: ProjectsUiState,
        menuState: ProjectsMenuState = ProjectsMenuState(),
        snackbarHostState: SnackbarHostState = SnackbarHostState(),
        defaultSlots: Boolean = false,
    ): MutableState<ProjectsUiState> {
        val state = mutableStateOf(uiState)
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                reservation = BottomAdBarDefaults.nativeReservation()
                scope = rememberCoroutineScope()
                val gridState: LazyGridState = rememberLazyGridState()
                if (defaultSlots) {
                    ProjectsScreen(
                        uiState = state.value,
                        thumbnails = emptyMap(),
                        snackbarHostState = snackbarHostState,
                        onNewProject = {},
                        onProjectClick = {},
                        onOpenLessons = {},
                        onOpenSettings = {},
                        onImport = {},
                        gridState = gridState,
                        menuState = menuState,
                    )
                } else {
                    ProjectsScreen(
                        uiState = state.value,
                        thumbnails = emptyMap(),
                        snackbarHostState = snackbarHostState,
                        onNewProject = {},
                        onProjectClick = {},
                        onOpenLessons = {},
                        onOpenSettings = {},
                        onImport = {},
                        gridState = gridState,
                        menuState = menuState,
                        adSlots = fake,
                    )
                }
            }
        }
        return state
    }

    @Test
    fun placements_matchSpec() {
        assertEquals(AdFormat.NATIVE_MEDIUM, AdPlacement.PROJECTS_EMPTY_NATIVE.format)
        assertEquals(AdFormat.NATIVE_SMALL, AdPlacement.PROJECTS_BOTTOM.format)
        assertEquals(AdFormat.NATIVE_IN_COLLECTION, AdPlacement.PROJECTS_GRID.format)
        assertEquals(6, AdPlacement.PROJECTS_GRID.nativeEvery)
        assertEquals("projects_empty_native", AdPlacement.PROJECTS_EMPTY_NATIVE.key)
        assertEquals("projects_bottom", AdPlacement.PROJECTS_BOTTOM.key)
        assertEquals("projects_grid", AdPlacement.PROJECTS_GRID.key)
        assertTrue(AdPlacement.PROJECTS_GRID.keepSpaceWhenPoolEmpty)
    }

    @Test
    fun emptyAd_belowLessonButton_36dp() {
        show(empty)
        val ad = rectOf(composeRule.onNodeWithTag(ProjectsTestTags.EMPTY_AD))
        val lesson = rectOf(composeRule.onNodeWithTag(ProjectsTestTags.LESSON))
        val newButton = rectOf(composeRule.onNodeWithTag(ProjectsTestTags.NEW))
        assertDp("gap under the lesson button", 36.dp, ad.top - lesson.bottom)
        assertTrue("gap is at least 24 dp", ad.top - lesson.bottom >= px(24.dp) - px(0.5.dp))
        assertDp("ad as wide as the buttons", 363.dp, ad.width)
        assertDp("ad as wide as New animation", toDp(newButton.width), ad.width)
        assertTrue(
            "ad under both buttons: $newButton, $lesson, $ad",
            ad.top >= lesson.bottom && ad.top >= newButton.bottom,
        )
    }

    @Test
    fun emptyAd_withoutLessonButton_belowNewButton() {
        show(ProjectsUiState.Empty(showLessonButton = false, showSettingsButton = true))
        // Control: there is no lesson button.
        composeRule.onAllNodesWithTag(ProjectsTestTags.LESSON).assertCountEquals(0)
        val ad = rectOf(composeRule.onNodeWithTag(ProjectsTestTags.EMPTY_AD))
        val newButton = rectOf(composeRule.onNodeWithTag(ProjectsTestTags.NEW))
        assertDp("gap under New animation", 36.dp, ad.top - newButton.bottom)
    }

    @Test
    fun bottomBar_onlyInList() {
        val state = show(empty)
        composeRule.onNodeWithTag(ProjectsTestTags.NEW).assertExists()
        assertEquals(0, composeRule.onAllNodesWithTag(FAKE_BOTTOM_BAR_TAG).fetchSemanticsNodes().size)

        state.value = list(2)
        composeRule.waitForIdle()
        assertEquals(1, composeRule.onAllNodesWithTag(FAKE_BOTTOM_BAR_TAG).fetchSemanticsNodes().size)
        assertEquals(0, composeRule.onAllNodesWithTag(ProjectsTestTags.EMPTY_AD).fetchSemanticsNodes().size)
    }

    @Test
    fun listToEmpty_swapsBarForEmptyAd() {
        val state = show(list(7))
        // Control: the list has its bar and its grid ad.
        composeRule.onNodeWithTag(FAKE_BOTTOM_BAR_TAG).assertExists()
        composeRule.onNodeWithTag(fakeGridAdTag(0)).assertExists()

        state.value = empty
        composeRule.waitForIdle()
        assertEquals(0, composeRule.onAllNodesWithTag(FAKE_BOTTOM_BAR_TAG).fetchSemanticsNodes().size)
        assertEquals(0, composeRule.onAllNodes(isGridAd).fetchSemanticsNodes().size)
        composeRule.onNodeWithTag(ProjectsTestTags.EMPTY_AD).assertExists()
    }

    @Test
    fun list_lastCardScrollsAboveBottomBar() {
        show(list(12))
        // "New animation", the header, 12 cards and one ad.
        composeRule.onNodeWithTag(ProjectsTestTags.GRID).performScrollToIndex(14)
        composeRule.waitForIdle()
        val lastCard = rectOf(composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(12))))
        val bar = rectOf(composeRule.onNodeWithTag(FAKE_BOTTOM_BAR_TAG))
        val rootBottom = composeRule.onRoot().fetchSemanticsNode().size.height.toFloat()
        assertTrue("last card $lastCard above the bar $bar", lastCard.bottom <= bar.top + px(0.5.dp))
        assertDp("bar 16 dp above the bottom", 16.dp, rootBottom - bar.bottom)
        assertDp("bar top at the reservation", reservation, rootBottom - bar.top)
    }

    @Test
    fun snackbar_aboveBottomBar() {
        val snackbarHostState = SnackbarHostState()
        val state = show(list(2), snackbarHostState = snackbarHostState)
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { scope.launch { snackbarHostState.showSnackbar(COMING_SOON) } }
        composeRule.mainClock.advanceTimeBy(500)
        val text = rectOf(composeRule.onNodeWithText(COMING_SOON))
        val bar = rectOf(composeRule.onNodeWithTag(FAKE_BOTTOM_BAR_TAG))
        assertTrue("message $text above the bar $bar", text.bottom <= bar.top + px(0.5.dp))

        // Control: in the empty state the message still shows.
        state.value = empty
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.onNodeWithText(COMING_SOON).assertExists()
    }

    @Test
    fun snackbar_duplicatingAboveBottomBar() {
        val duplicating = ApplicationProvider.getApplicationContext<android.content.Context>()
            .getString(R.string.projects_duplicating, "A")
        composeRule.mainClock.autoAdvance = false
        show(list(2), menuState = ProjectsMenuState(duplicatingName = "A"))
        composeRule.mainClock.advanceTimeBy(500)
        val text = rectOf(composeRule.onNodeWithText(duplicating))
        val bar = rectOf(composeRule.onNodeWithTag(FAKE_BOTTOM_BAR_TAG))
        assertTrue("message $text above the bar $bar", text.bottom <= bar.top + px(0.5.dp))
    }

    @Test
    fun gridAd_afterSixthProject() {
        show(list(7))
        composeRule.onNodeWithTag(fakeGridAdTag(0)).assertExists()
        assertEquals(1, composeRule.onAllNodes(isGridAd).fetchSemanticsNodes().size)
        val ad = rectOf(composeRule.onNodeWithTag(fakeGridAdTag(0)))
        val card1 = cardRect(1)
        val card6 = cardRect(6)
        val card7 = cardRect(7)
        assertDp("ad in the first column", toDp(card1.left), ad.left)
        assertTrue("ad $ad after card 6 $card6", ad.top > card6.top)
        assertDp("card 7 on the ad's row", toDp(ad.top), card7.top)
    }

    @Test
    fun gridAd_noneWithSixProjects() {
        val state = show(list(6))
        composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(6))).assertExists()
        assertEquals(0, composeRule.onAllNodes(isGridAd).fetchSemanticsNodes().size)

        // Control: a seventh project brings the ad.
        state.value = list(7)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(fakeGridAdTag(0)).assertExists()
    }

    @Test
    fun gridAd_twoWithThirteenProjects() {
        val state = show(list(13))
        val grid = composeRule.onNodeWithTag(ProjectsTestTags.GRID)
        grid.performScrollToNode(hasTestTag(fakeGridAdTag(0)))
        composeRule.onNodeWithTag(fakeGridAdTag(0)).assertExists()
        grid.performScrollToNode(hasTestTag(fakeGridAdTag(1)))
        composeRule.onNodeWithTag(fakeGridAdTag(1)).assertExists()

        state.value = list(12)
        composeRule.waitForIdle()
        grid.performScrollToNode(hasTestTag(fakeGridAdTag(0)))
        composeRule.onNodeWithTag(fakeGridAdTag(0)).assertExists()
        // To the end: "New animation", the header, 12 cards and one ad.
        grid.performScrollToIndex(14)
        composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(12))).assertExists()
        assertEquals(0, composeRule.onAllNodesWithTag(fakeGridAdTag(1)).fetchSemanticsNodes().size)
    }

    @Test
    fun gridAd_heightKeepsMediaFloor() {
        val control = mutableStateOf<Pair<Dp, Dp>?>(null)
        val state = mutableStateOf<ProjectsUiState>(list(7))
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                val sizes = control.value
                if (sizes == null) {
                    ProjectsScreen(
                        uiState = state.value,
                        thumbnails = emptyMap(),
                        snackbarHostState = SnackbarHostState(),
                        onNewProject = {},
                        onProjectClick = {},
                        onOpenLessons = {},
                        onOpenSettings = {},
                        onImport = {},
                        adSlots = fake,
                    )
                } else {
                    NativeAdCard(
                        fakeEmptyAdAssets,
                        NativeAdSize.CARD,
                        Modifier.width(sizes.first).testTag(CONTROL_TAG),
                        cardHeight = sizes.second,
                    )
                }
            }
        }
        val ad = rectOf(composeRule.onNodeWithTag(fakeGridAdTag(0)))
        val card7 = cardRect(7)
        val media = rectOf(
            composeRule.onNode(hasTestTag(MEDIA_TAG) and hasAnyAncestor(hasTestTag(fakeGridAdTag(0))), true),
        )
        assertDp("ad cell height", PROJECT_GRID_AD_HEIGHT, ad.height)
        assertTrue("media ${toDp(media.height)} under 120 dp", media.height >= px(120.dp) - px(0.5.dp))
        assertDp("card 7 top-aligned with the ad", toDp(ad.top), card7.top)
        assertTrue("card 7 $card7 lower than the ad $ad", card7.height < ad.height)

        // Control: a card as high as a project card has its media under 120 dp, so the media check can fail.
        control.value = toDp(ad.width) to toDp(card7.height)
        composeRule.waitForIdle()
        val controlMedia = rectOf(
            composeRule.onNode(hasTestTag(MEDIA_TAG) and hasAnyAncestor(hasTestTag(CONTROL_TAG)), true),
        )
        assertTrue("control media ${toDp(controlMedia.height)}", controlMedia.height < px(120.dp))
    }

    @Test
    fun gridAd_ctaInsideCell() {
        show(list(7))
        val ad = rectOf(composeRule.onNodeWithTag(fakeGridAdTag(0)))
        val cta = rectOf(composeRule.onNode(hasText("Install") and hasAnyAncestor(hasTestTag(fakeGridAdTag(0))), true))
        assertTrue(
            "call to action $cta inside the cell $ad",
            cta.left >= ad.left && cta.right <= ad.right && cta.top >= ad.top && cta.bottom <= ad.bottom,
        )
    }

    @Test
    fun gridAd_usesGridColumns() {
        show(list(7))
        val card1 = cardRect(1)
        val card2 = cardRect(2)
        val card3 = cardRect(3)
        assertDp("cards 1 and 2 on one row", toDp(card1.top), card2.top)
        assertTrue("card 3 on the next row", card3.top > card1.top)
        val firstRow = (1..3).count { abs(cardRect(it).top - card1.top) < px(0.5.dp) }
        assertEquals(PROJECT_GRID_COLUMNS, firstRow)
        assertEquals(PROJECT_GRID_COLUMNS, fake.receivedColumns)
    }

    @Test
    fun noFirstStrokeRule() {
        // Control: the scan finds the import and the symbol, and skips comments.
        assertTrue(
            usesFirstStroke(
                "package x\n\nimport com.vgl.draw.animation.core.data.settings.DeviceSettingsRepository\n",
            ),
        )
        assertTrue(usesFirstStroke("val x = settings.firstStrokeDrawn\n"))
        assertFalse(usesFirstStroke("// No rule before firstStrokeDrawn here (spec question 24).\n"))
        for (name in listOf("ProjectsScreen.kt", "ProjectCard.kt", "ProjectsAds.kt", "ProjectsViewModel.kt")) {
            val file = File("src/main/kotlin/com/vgl/draw/animation/feature/projects/$name")
            assertTrue("$name exists", file.isFile)
            val source = file.readText()
            assertTrue("$name is not empty", source.isNotBlank())
            assertFalse("$name has a rule before the first stroke", usesFirstStroke(source))
        }
    }

    @Test
    fun defaultSlots_withoutController_noAdNodes() {
        val state = show(list(7), defaultSlots = true)
        composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(7))).assertExists()
        assertEquals(0, composeRule.onAllNodes(isGridAd).fetchSemanticsNodes().size)
        assertEquals(0, composeRule.onAllNodesWithTag(FAKE_BOTTOM_BAR_TAG).fetchSemanticsNodes().size)

        state.value = empty
        composeRule.waitForIdle()
        val ad = composeRule.onNodeWithTag(ProjectsTestTags.EMPTY_AD).fetchSemanticsNode()
        assertEquals(0, ad.size.height)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-xxhdpi")
    fun small_emptyAdReachable() {
        show(empty)
        composeRule.onNodeWithTag(ProjectsTestTags.EMPTY_AD).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag(ProjectsTestTags.NEW).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag(ProjectsTestTags.LESSON).performScrollTo().assertIsDisplayed()
    }

    private val isGridAd = SemanticsMatcher("a grid ad") { node ->
        node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith("projects_grid_ad_") == true
    }

    private fun cardRect(n: Int): Rect = rectOf(composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(n))))

    /** Bounds in the root, not clipped by the scrolling parent. */
    private fun rectOf(node: SemanticsNodeInteraction): Rect {
        val semantics = node.fetchSemanticsNode()
        return Rect(semantics.positionInRoot, semantics.size.toSize())
    }

    private fun px(dp: Dp): Float = with(composeRule.density) { dp.toPx() }

    private fun toDp(px: Float): Dp = with(composeRule.density) { px.toDp() }

    private fun assertDp(message: String, expected: Dp, actualPx: Float) {
        val actual = toDp(actualPx)
        assertTrue("$message: expected $expected, was $actual", abs(actual.value - expected.value) <= 0.5f)
    }

    /** True when a code line of [source] imports DeviceSettingsRepository or reads firstStrokeDrawn. */
    private fun usesFirstStroke(source: String): Boolean = source.lines()
        .map { it.trim() }
        .filterNot { it.startsWith("//") || it.startsWith("*") || it.startsWith("/*") }
        .any { line ->
            (line.startsWith("import ") && line.endsWith(".DeviceSettingsRepository")) ||
                FIRST_STROKE.containsMatchIn(line)
        }

    private companion object {
        const val COMING_SOON = "Coming soon"
        const val CONTROL_TAG = "control_ad"

        // NativeAdTestTags.MEDIA of :core:ads, internal there.
        const val MEDIA_TAG = "native_ad_media"
        val FIRST_STROKE = Regex("\\bfirstStrokeDrawn\\b")
    }
}
