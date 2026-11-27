package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.project.model.ProjectId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** JVM UI tests of the stateless [ProjectsScreen] (Robolectric). */
@RunWith(AndroidJUnit4::class)
class ProjectsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var newClicks = 0
    private var lessonClicks = 0
    private var settingsClicks = 0
    private val clickedCards = mutableListOf<ProjectCardUi>()

    private fun show(
        uiState: ProjectsUiState,
        thumbnails: Map<ProjectId, ImageBitmap?> = emptyMap(),
        gridState: (() -> LazyGridState)? = null,
        privacyOptionsButton: (@Composable (Modifier) -> Unit)? = null,
    ) {
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                Screen(uiState, thumbnails, gridState?.invoke() ?: rememberLazyGridState(), privacyOptionsButton)
            }
        }
    }

    /** [privacyOptionsButton] null: the screen's own default slot. */
    @Composable
    private fun Screen(
        uiState: ProjectsUiState,
        thumbnails: Map<ProjectId, ImageBitmap?> = emptyMap(),
        gridState: LazyGridState = rememberLazyGridState(),
        privacyOptionsButton: (@Composable (Modifier) -> Unit)? = null,
    ) {
        if (privacyOptionsButton == null) {
            ProjectsScreen(
                uiState = uiState,
                thumbnails = thumbnails,
                snackbarHostState = remember { SnackbarHostState() },
                onNewProject = { newClicks++ },
                onProjectClick = { clickedCards += it },
                onOpenLessons = { lessonClicks++ },
                onOpenSettings = { settingsClicks++ },
                onImport = {},
                gridState = gridState,
            )
        } else {
            ProjectsScreen(
                uiState = uiState,
                thumbnails = thumbnails,
                snackbarHostState = remember { SnackbarHostState() },
                onNewProject = { newClicks++ },
                onProjectClick = { clickedCards += it },
                onOpenLessons = { lessonClicks++ },
                onOpenSettings = { settingsClicks++ },
                onImport = {},
                gridState = gridState,
                privacyOptionsButton = privacyOptionsButton,
            )
        }
    }

    /** Sets the screen once over [uiState], which the test changes afterwards. */
    private fun showChanging(
        uiState: MutableState<ProjectsUiState>,
        privacyOptionsButton: @Composable (Modifier) -> Unit,
    ) {
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                Screen(uiState.value, privacyOptionsButton = privacyOptionsButton)
            }
        }
    }

    private val empty = ProjectsUiState.Empty(showLessonButton = true, showSettingsButton = true)

    private fun list(cards: List<ProjectCardUi>) = ProjectsUiState.List(cards, showSettingsButton = true)

    @Test
    fun empty_showsDesignTexts() {
        show(empty)
        composeRule.onNodeWithTag(ProjectsTestTags.EMPTY).assertExists()
        composeRule.onNodeWithText("Frameloop").assertExists()
        composeRule.onNodeWithText("Make your first animation").assertExists()
        composeRule.onNodeWithText(
            "Draw frame by frame, add sound, then export a video or GIF. Everything saves on your phone as you go.",
        ).assertExists()
        composeRule.onNodeWithText("New animation").assertExists()
        composeRule.onNodeWithText("Try a free lesson").assertExists()
    }

    @Test
    fun newAnimation_callsCallback() {
        show(empty)
        composeRule.onNodeWithTag(ProjectsTestTags.NEW).performClick()
        assertEquals(1, newClicks)
    }

    @Test
    fun lessonButton_callsCallback() {
        show(empty)
        composeRule.onNodeWithTag(ProjectsTestTags.LESSON).performScrollTo().performClick()
        assertEquals(1, lessonClicks)
    }

    @Test
    fun empty_withoutLessonButton_hasNoLessonNode() {
        show(ProjectsUiState.Empty(showLessonButton = false, showSettingsButton = true))
        // Control: the screen is drawn, with its other button.
        composeRule.onNodeWithTag(ProjectsTestTags.NEW).assertExists()
        composeRule.onAllNodesWithTag(ProjectsTestTags.LESSON).assertCountEquals(0)
        composeRule.onAllNodesWithText("Try a free lesson").assertCountEquals(0)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun small_emptyButtonsReachable() {
        show(empty)
        composeRule.onNodeWithTag(ProjectsTestTags.LESSON).performScrollTo()
        val screen = composeRule.onNodeWithTag(ProjectsTestTags.SCREEN).fetchSemanticsNode().boundsInRoot
        val newButton = composeRule.onNodeWithTag(ProjectsTestTags.NEW).fetchSemanticsNode().boundsInRoot
        val lesson = composeRule.onNodeWithTag(ProjectsTestTags.LESSON).fetchSemanticsNode().boundsInRoot
        assertTrue("lesson button $lesson inside $screen", lesson.top >= screen.top && lesson.bottom <= screen.bottom)
        assertTrue("lesson button has a size", lesson.height > 0f)
        assertTrue("buttons overlap: $newButton, $lesson", newButton.bottom <= lesson.top)
    }

    @Test
    fun card_showsFramesLine() {
        show(list(listOf(readableCard(1, "Bouncing ball", frames = 48, fps = 12), readableCard(2, "One", frames = 1))))
        composeRule.onNodeWithText("48 frames · 12 fps").assertExists()
        composeRule.onNodeWithText("1 frame · 12 fps").assertExists()
    }

    @Test
    fun list_showsDesignTexts() {
        show(list(designCards()))
        composeRule.onNodeWithTag(ProjectsTestTags.GRID).assertExists()
        composeRule.onNodeWithText("Your projects").assertExists()
        composeRule.onNodeWithText("Recent").assertExists()
        composeRule.onNodeWithText("New animation").assertExists()
        listOf("Bouncing ball", "Walk cycle", "Logo sting", "Blink test").forEach {
            composeRule.onNodeWithText(it).assertExists()
        }
        listOf("48 frames · 12 fps", "24 frames · 12 fps", "90 frames · 24 fps", "16 frames · 8 fps").forEach {
            composeRule.onNodeWithText(it).assertExists()
        }
    }

    @Test
    fun cardClick_callsCallbackWithCard() {
        val cards = designCards()
        show(list(cards))
        composeRule.onNodeWithTag(ProjectsTestTags.card(cards[1].id)).performClick()
        assertEquals(listOf(cards[1]), clickedCards)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers = "w411dp-h891dp-xxhdpi")
    fun longName_isEllipsizedOnOneLine() {
        val longName = "A very long project name that never fits on one card line"
        show(list(listOf(readableCard(1, longName, frames = 48, fps = 12), readableCard(2))))
        // The card merges its texts for accessibility: read the text nodes themselves.
        val nameNode = composeRule.onNodeWithText(longName, useUnmergedTree = true).fetchSemanticsNode()
        val layouts = mutableListOf<TextLayoutResult>()
        nameNode.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(layouts)
        val layout = layouts.single()
        assertEquals(1, layout.lineCount)
        assertTrue("name is cut with an ellipsis", layout.isLineEllipsized(0))
        val meta = composeRule.onNodeWithText("48 frames · 12 fps", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        assertTrue("name ${nameNode.boundsInRoot} over meta line $meta", nameNode.boundsInRoot.bottom <= meta.top)
    }

    @Test
    fun scrollGrid_topBarStays() {
        val cards = (1..20).map { readableCard(it) }
        show(list(cards))
        val before = composeRule.onNodeWithTag(ProjectsTestTags.SETTINGS).fetchSemanticsNode().boundsInRoot.top
        composeRule.onNodeWithTag(ProjectsTestTags.GRID)
            .performScrollToNode(hasTestTag(ProjectsTestTags.card(cards.last().id)))
        composeRule.onNodeWithTag(ProjectsTestTags.card(cards.last().id)).assertExists()
        // Control: the first card left the screen, so the grid did scroll.
        composeRule.onAllNodesWithTag(ProjectsTestTags.card(cards.first().id)).assertCountEquals(0)
        val after = composeRule.onNodeWithTag(ProjectsTestTags.SETTINGS).fetchSemanticsNode().boundsInRoot.top
        assertEquals(before, after)
    }

    // NATIVE: the default graphics mode cannot create the bitmap behind ImageBitmap.
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun unreadable_showsNameAndEmptyThumbnail() {
        val readable = readableCard(1, "Walk cycle")
        val broken = unreadableCard(2, "Broken one")
        // Both ids have an image: the screen must still not draw one for the broken card.
        val image = ImageBitmap(4, 4)
        show(list(listOf(readable, broken)), thumbnails = mapOf(readable.id to image, broken.id to image))
        composeRule.onNodeWithText("Broken one").assertExists()
        // Control: the readable card draws its image.
        composeRule.onNodeWithTag(ProjectsTestTags.thumbnail(readable.id), useUnmergedTree = true).assertExists()
        composeRule.onAllNodesWithTag(ProjectsTestTags.thumbnail(broken.id), useUnmergedTree = true)
            .assertCountEquals(0)
    }

    @Test
    fun unreadable_withoutName_showsPlaceholder() {
        show(list(listOf(unreadableCard(1, name = null))))
        composeRule.onNodeWithText("Untitled project").assertExists()
    }

    @Test
    fun unreadable_withName_showsNameNotPlaceholder() {
        // Control of unreadable_withoutName_showsPlaceholder: a readable name is kept.
        show(list(listOf(unreadableCard(1, name = "Walk cycle"))))
        composeRule.onNodeWithText("Walk cycle").assertExists()
        composeRule.onAllNodesWithText("Untitled project").assertCountEquals(0)
    }

    @Test
    fun settings_callsCallback() {
        show(list(designCards()))
        composeRule.onNodeWithTag(ProjectsTestTags.SETTINGS).performClick()
        assertEquals(1, settingsClicks)
    }

    @Test
    fun settingsHidden_hasNoSettingsNode() {
        show(ProjectsUiState.List(designCards(), showSettingsButton = false))
        composeRule.onNodeWithTag(ProjectsTestTags.IMPORT).assertExists()
        composeRule.onAllNodesWithTag(ProjectsTestTags.SETTINGS).assertCountEquals(0)
    }

    @Test
    fun scrollPosition_survivesRestore() {
        val cards = (1..20).map { readableCard(it) }
        val restorationTester = StateRestorationTester(composeRule)
        var gridState: LazyGridState? = null
        restorationTester.setContent {
            AppTheme(darkTheme = false) {
                val state = rememberLazyGridState()
                gridState = state
                ProjectsScreen(
                    uiState = list(cards),
                    thumbnails = emptyMap(),
                    snackbarHostState = remember { SnackbarHostState() },
                    onNewProject = {},
                    onProjectClick = {},
                    onOpenLessons = {},
                    onOpenSettings = {},
                    onImport = {},
                    gridState = state,
                )
            }
        }
        composeRule.waitForIdle()
        assertEquals(0, gridState!!.firstVisibleItemIndex)
        composeRule.onNodeWithTag(ProjectsTestTags.GRID)
            .performScrollToNode(hasTestTag(ProjectsTestTags.card(cards[14].id)))
        composeRule.waitForIdle()
        val scrolled = gridState!!.firstVisibleItemIndex
        assertNotEquals(0, scrolled)

        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.waitForIdle()
        assertEquals(scrolled, gridState!!.firstVisibleItemIndex)
    }

    @Test
    @Config(qualifiers = "vi")
    fun vietnamese_framesLineKeepsNumbers() {
        show(list(listOf(readableCard(1, "Bouncing ball", frames = 48, fps = 12))))
        composeRule.onNodeWithText("48 khung · 12 fps").assertExists()
    }

    @Test
    fun settingsOff_showsPrivacyButtonInPlaceOfSettings() {
        val uiState = mutableStateOf<ProjectsUiState>(
            ProjectsUiState.Empty(showLessonButton = true, showSettingsButton = false),
        )
        showChanging(uiState) { FakePrivacyButton(modifier = it) }
        assertPrivacyInPlaceOfSettings()

        uiState.value = ProjectsUiState.List(designCards(), showSettingsButton = false)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.GRID).assertExists()
        assertPrivacyInPlaceOfSettings()
    }

    private fun assertPrivacyInPlaceOfSettings() {
        val privacy = composeRule.onNodeWithTag(ProjectsTestTags.PRIVACY).fetchSemanticsNode().boundsInRoot
        composeRule.onAllNodesWithTag(ProjectsTestTags.SETTINGS).assertCountEquals(0)
        val import = composeRule.onNodeWithTag(ProjectsTestTags.IMPORT).fetchSemanticsNode().boundsInRoot
        assertTrue("privacy $privacy after import $import", privacy.left >= import.right)
        val topBarBottom = with(composeRule.density) { 64.dp.toPx() }
        assertTrue("privacy $privacy inside the top bar ($topBarBottom px)", privacy.bottom <= topBarBottom)
    }

    @Test
    fun settingsOn_doesNotComposePrivacySlot() {
        var slotComposed = false
        val uiState = mutableStateOf<ProjectsUiState>(list(designCards()))
        showChanging(uiState) {
            slotComposed = true
            FakePrivacyButton(modifier = it)
        }
        assertEquals(false, slotComposed)
        composeRule.onNodeWithTag(ProjectsTestTags.SETTINGS).assertExists()
        composeRule.onAllNodesWithTag(ProjectsTestTags.PRIVACY).assertCountEquals(0)

        // Control: with the flag off the same slot is composed.
        uiState.value = ProjectsUiState.List(designCards(), showSettingsButton = false)
        composeRule.waitForIdle()
        assertEquals(true, slotComposed)
    }

    @Test
    fun privacyButton_tapReachesButton() {
        var privacyClicks = 0
        show(
            ProjectsUiState.List(designCards(), showSettingsButton = false),
            privacyOptionsButton = { FakePrivacyButton(modifier = it, onClick = { privacyClicks++ }) },
        )
        composeRule.onNodeWithTag(ProjectsTestTags.PRIVACY).performClick()
        assertEquals(1, privacyClicks)
        assertEquals(0, settingsClicks)
    }

    @Test
    fun settingsOff_emptySlot_noTopButton() {
        var slotComposed = false
        show(
            ProjectsUiState.List(designCards(), showSettingsButton = false),
            privacyOptionsButton = { slotComposed = true },
        )
        // Control: the slot was called, it just draws nothing (like PrivacyOptionsButton where not required).
        assertEquals(true, slotComposed)
        composeRule.onAllNodesWithTag(ProjectsTestTags.SETTINGS).assertCountEquals(0)
        composeRule.onAllNodesWithTag(ProjectsTestTags.PRIVACY).assertCountEquals(0)
        composeRule.onAllNodesWithText(PRIVACY_TEXT).assertCountEquals(0)
        composeRule.onNodeWithTag(ProjectsTestTags.IMPORT).assertExists()
    }

    // Guard: passes as soon as the tag compiles; catches a default slot that draws something else than
    // PrivacyOptionsButton, which draws nothing without an ads controller.
    @Test
    fun settingsOff_defaultSlot_rendersNothingWithoutAds() {
        show(ProjectsUiState.List(designCards(), showSettingsButton = false))
        composeRule.onAllNodesWithText(PRIVACY_TEXT).assertCountEquals(0)
        composeRule.onAllNodesWithTag(ProjectsTestTags.PRIVACY).assertCountEquals(0)
        composeRule.onAllNodesWithTag(ProjectsTestTags.SETTINGS).assertCountEquals(0)
        // Control: the top bar is drawn.
        composeRule.onNodeWithTag(ProjectsTestTags.IMPORT).assertExists()
    }

    @Test
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun privacyButton_fitsTopBar_small() {
        show(
            ProjectsUiState.List(designCards(), showSettingsButton = false),
            privacyOptionsButton = { FakePrivacyButton(modifier = it) },
        )
        val textNode = composeRule.onNodeWithText(PRIVACY_TEXT, useUnmergedTree = true).fetchSemanticsNode()
        val layout = textLayout(textNode)
        assertEquals(1, layout.lineCount)
        assertEquals(false, layout.isLineEllipsized(0))
        assertTrue(
            "line right ${layout.getLineRight(0)} within ${textNode.boundsInRoot.width}",
            layout.getLineRight(0) <= textNode.boundsInRoot.width,
        )
        val wordmark = composeRule.onNodeWithText("Frameloop", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val import = composeRule.onNodeWithTag(ProjectsTestTags.IMPORT).fetchSemanticsNode().boundsInRoot
        val privacy = composeRule.onNodeWithTag(ProjectsTestTags.PRIVACY).fetchSemanticsNode().boundsInRoot
        val root = composeRule.onRoot().fetchSemanticsNode().boundsInRoot
        assertTrue("wordmark $wordmark before import $import", wordmark.right <= import.left)
        assertTrue("import $import before privacy $privacy", import.right <= privacy.left)
        assertTrue("privacy $privacy inside root $root", privacy.right <= root.width)
    }

    @Test
    @Config(qualifiers = "en-w360dp-h640dp-xxhdpi", fontScale = 2.0f)
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun privacyButton_largeFont_notClipped() {
        show(
            ProjectsUiState.List(designCards(), showSettingsButton = false),
            privacyOptionsButton = { FakePrivacyButton(modifier = it) },
        )
        val textNode = composeRule.onNodeWithText(PRIVACY_TEXT, useUnmergedTree = true).fetchSemanticsNode()
        val layout = textLayout(textNode)
        assertEquals(false, layout.hasVisualOverflow)
        val lastLineBottom = layout.getLineBottom(layout.lineCount - 1)
        assertTrue(
            "last line bottom $lastLineBottom within ${textNode.boundsInRoot.height}",
            lastLineBottom <= textNode.boundsInRoot.height,
        )
        (0 until layout.lineCount).forEach { line ->
            assertEquals("line $line ellipsized", false, layout.isLineEllipsized(line))
        }
        val privacy = composeRule.onNodeWithTag(ProjectsTestTags.PRIVACY).fetchSemanticsNode().boundsInRoot
        val grid = composeRule.onNodeWithTag(ProjectsTestTags.GRID).fetchSemanticsNode().boundsInRoot
        assertTrue("privacy $privacy above the grid $grid", privacy.bottom <= grid.top)
    }

    private fun textLayout(node: SemanticsNode): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        node.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(layouts)
        return layouts.single()
    }

    private companion object {
        const val PRIVACY_TEXT = "Privacy settings"
    }
}
