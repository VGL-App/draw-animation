package com.vgl.draw.animation.feature.projects

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.TextLayoutResult
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
    ) {
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                ProjectsScreen(
                    uiState = uiState,
                    thumbnails = thumbnails,
                    snackbarHostState = remember { SnackbarHostState() },
                    onNewProject = { newClicks++ },
                    onProjectClick = { clickedCards += it },
                    onOpenLessons = { lessonClicks++ },
                    onOpenSettings = { settingsClicks++ },
                    onImport = {},
                    gridState = gridState?.invoke() ?: rememberLazyGridState(),
                )
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
        val nameNode = composeRule.onNodeWithText(longName).fetchSemanticsNode()
        val layouts = mutableListOf<TextLayoutResult>()
        nameNode.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(layouts)
        val layout = layouts.single()
        assertEquals(1, layout.lineCount)
        assertTrue("name is cut with an ellipsis", layout.isLineEllipsized(0))
        val meta = composeRule.onNodeWithText("48 frames · 12 fps").fetchSemanticsNode().boundsInRoot
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
}
