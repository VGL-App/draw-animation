package com.vgl.draw.animation.feature.projects

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.abs
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * JVM UI tests of the project menu, its dialogs and the long press on a card (plan T-012, section 4). The real
 * [ProjectMenuSheet] and dialogs open windows of their own: texts are looked for in every window. Pixel colors are
 * read on the activity window only, with the menu content and the dialog cards drawn there (plan section 3.4).
 */
@RunWith(AndroidJUnit4::class)
class ProjectMenuTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val walk = ProjectCardUi(
        id = WALK,
        name = "Walk cycle",
        frameCount = 24,
        fps = 12,
        updatedAtMillis = NOW - 60_000L,
        readable = true,
    )
    private val broken = ProjectCardUi(
        id = BROKEN,
        name = "Old test",
        frameCount = 0,
        fps = 0,
        updatedAtMillis = 0L,
        readable = false,
    )
    private val ball = readableCard(3, "Bouncing ball", frames = 48, fps = 12)

    private val clicks = mutableListOf<ProjectCardUi>()
    private val longClicks = mutableListOf<ProjectCardUi>()
    private var dismissCount = 0
    private var openCount = 0
    private var renameCount = 0
    private var duplicateCount = 0
    private var deleteCount = 0
    private val renamed = mutableListOf<String>()
    private var deleteConfirmCount = 0

    /**
     * [ProjectsScreen] with the menu state held here. The callbacks change the state as the ViewModel does, so the
     * menu and dialogs come and go; [openOnLongClick] false only records the long press.
     */
    private fun showScreen(
        initial: ProjectsMenuState,
        cards: List<ProjectCardUi> = listOf(walk, ball),
        openOnLongClick: Boolean = true,
    ): MutableState<ProjectsMenuState> {
        val state = mutableStateOf(initial)
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                Screen(state, cards, openOnLongClick)
            }
        }
        composeRule.waitForIdle()
        return state
    }

    @Composable
    private fun Screen(state: MutableState<ProjectsMenuState>, cards: List<ProjectCardUi>, openOnLongClick: Boolean) {
        val menuState by state
        fun card(): ProjectCardUi? = when (val overlay = state.value.overlay) {
            is ProjectsOverlay.Menu -> overlay.card
            is ProjectsOverlay.Rename -> overlay.card
            is ProjectsOverlay.ConfirmDelete -> overlay.card
            ProjectsOverlay.None -> null
        }
        ProjectsScreen(
            uiState = ProjectsUiState.List(cards, showSettingsButton = true),
            thumbnails = emptyMap(),
            snackbarHostState = remember { SnackbarHostState() },
            onNewProject = {},
            onProjectClick = { clicks += it },
            onOpenLessons = {},
            onOpenSettings = {},
            onImport = {},
            menuState = menuState,
            nowMillis = NOW,
            zone = UTC,
            onProjectLongClick = {
                longClicks += it
                if (openOnLongClick) state.value = state.value.copy(overlay = ProjectsOverlay.Menu(it))
            },
            onMenuOpen = {
                openCount++
                state.value = state.value.copy(overlay = ProjectsOverlay.None)
            },
            onMenuRename = {
                renameCount++
                state.value = state.value.copy(overlay = ProjectsOverlay.Rename(card()!!))
            },
            onMenuDuplicate = { duplicateCount++ },
            onMenuBackUp = {},
            onMenuDelete = {
                deleteCount++
                state.value = state.value.copy(overlay = ProjectsOverlay.ConfirmDelete(card()!!))
            },
            onRenameConfirm = { renamed += it },
            onDeleteConfirm = { deleteConfirmCount++ },
            onDismissOverlay = {
                dismissCount++
                state.value = state.value.copy(overlay = ProjectsOverlay.None)
            },
        )
    }

    private fun waitForMenu() {
        composeRule.waitUntil(WAIT_MILLIS) { menuNodes().isNotEmpty() }
    }

    private fun menuNodes(): List<SemanticsNode> =
        composeRule.onAllNodesWithTag(ProjectsTestTags.MENU, useUnmergedTree = true).fetchSemanticsNodes()

    private fun inside(tag: String, text: String): SemanticsMatcher = hasText(text) and hasAnyAncestor(hasTestTag(tag))

    private fun inMenu(text: String): SemanticsNodeInteraction =
        composeRule.onNode(inside(ProjectsTestTags.MENU, text), useUnmergedTree = true)

    private fun countInMenu(text: String): Int =
        composeRule.onAllNodes(inside(ProjectsTestTags.MENU, text), useUnmergedTree = true).fetchSemanticsNodes().size

    private fun bounds(tag: String): Rect =
        composeRule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().boundsInWindow

    private fun px(dp: Dp): Float = with(composeRule.density) { dp.toPx() }

    /** Height of the window that holds the menu (the sheet's own window). */
    private fun menuWindowHeight(): Int {
        val node = composeRule.onNodeWithTag(ProjectsTestTags.MENU, useUnmergedTree = true).fetchSemanticsNode()
        return (node.root as ViewRootForTest).view.rootView.height
    }

    private fun assertMenuClosedBy(action: () -> Unit) {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Menu(walk)))
        waitForMenu()
        action()
        composeRule.waitForIdle()
        assertEquals(1, dismissCount)
        assertTrue("menu still shown", menuNodes().isEmpty())
        assertEquals(0, renameCount)
        assertEquals(0, deleteCount)
        assertEquals(0, openCount)
    }

    @Test
    fun cardTap_stillOpens_longPressOpensMenu() {
        showScreen(ProjectsMenuState(), openOnLongClick = false)
        composeRule.onNodeWithTag(ProjectsTestTags.card(WALK)).performTouchInput { longClick() }
        composeRule.waitForIdle()
        assertEquals(listOf(walk), longClicks)
        assertEquals(0, clicks.size)

        composeRule.onNodeWithTag(ProjectsTestTags.card(WALK)).performClick()
        composeRule.waitForIdle()
        assertEquals(listOf(walk), clicks)
        assertEquals(1, longClicks.size)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers = "w411dp-h891dp-xxhdpi")
    fun menuSheet_isAtBottom() {
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                ProjectMenuSheet(
                    card = walk,
                    thumbnail = null,
                    nowMillis = NOW,
                    zone = UTC,
                    duplicateEnabled = true,
                    bottomInset = 0.dp,
                    onDismissRequest = {},
                    onOpen = {},
                    onRename = {},
                    onDuplicate = {},
                    onBackUp = {},
                    onDelete = {},
                )
            }
        }
        waitForMenu()
        composeRule.waitForIdle()
        val windowHeight = menuWindowHeight()
        assertTrue("window height $windowHeight", windowHeight > 0)
        val menu = bounds(ProjectsTestTags.MENU)
        assertEquals(windowHeight.toFloat(), menu.bottom, 1f)
        assertTrue("menu top ${menu.top} in the lower half of $windowHeight", menu.top > windowHeight / 2f)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun menuContent_bottomInsetAddedOnce() {
        val inset = mutableStateOf(48.dp)
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                MenuContentOnBackground(walk, bottomInset = inset.value)
            }
        }
        composeRule.waitForIdle()
        var menu = bounds(ProjectsTestTags.MENU)
        var delete = bounds(ProjectsTestTags.MENU_DELETE)
        assertEquals(menu.bottom - px(16.dp + 48.dp), delete.bottom, 1f)

        // Control: no system bar inset leaves the 16 dp of the design.
        inset.value = 0.dp
        composeRule.waitForIdle()
        menu = bounds(ProjectsTestTags.MENU)
        delete = bounds(ProjectsTestTags.MENU_DELETE)
        assertEquals(menu.bottom - px(16.dp), delete.bottom, 1f)
    }

    @Test
    fun menu_showsHeaderAndFiveItems() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Menu(walk)))
        waitForMenu()
        val texts = listOf(
            "Walk cycle",
            "24 frames · 12 fps · edited today",
            "Open",
            "Rename",
            "Duplicate",
            "Back up to a file",
            "Delete",
        )
        val tops = texts.map { inMenu(it).fetchSemanticsNode().boundsInWindow.top }
        assertEquals("texts in order from top to bottom: $tops", tops.sorted(), tops)
        assertEquals("one top per text: $tops", texts.size, tops.distinct().size)
        val extension = inMenu(".frameloop").fetchSemanticsNode().boundsInWindow
        val backUp = inMenu("Back up to a file").fetchSemanticsNode().boundsInWindow
        assertTrue("extension on the back up row", extension.center.y in backUp.top..backUp.bottom)
        assertTrue("extension after the label", extension.left > backUp.right)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun deleteItem_usesErrorColor() {
        lateinit var colors: ColorScheme
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                colors = MaterialTheme.colorScheme
                MenuContentOnBackground(walk, bottomInset = 0.dp)
            }
        }
        composeRule.waitForIdle()
        val bitmap = windowBitmap()
        val background = colors.surfaceContainerHigh.toArgb()
        val deleteInk = inkColorsIn(bitmap, leadingIcon(ProjectsTestTags.MENU_DELETE), background)
        assertTrue("delete icon pixels: ${deleteInk.size}", deleteInk.size >= MIN_INK)
        assertTrue(
            "delete icon closer to error",
            shareCloser(deleteInk, colors.error.toArgb(), colors.onSurface.toArgb()) > CLOSER_SHARE,
        )
        // Control: the Open icon is onSurface.
        val openInk = inkColorsIn(bitmap, leadingIcon(ProjectsTestTags.MENU_OPEN), background)
        assertTrue("open icon pixels: ${openInk.size}", openInk.size >= MIN_INK)
        assertTrue(
            "open icon closer to onSurface",
            shareCloser(openInk, colors.onSurface.toArgb(), colors.error.toArgb()) > CLOSER_SHARE,
        )
    }

    @Test
    fun back_closesMenu() = assertMenuClosedBy { Espresso.pressBack() }

    @Test
    fun tapOutside_closesMenu() = assertMenuClosedBy {
        menuWindowRoot().performTouchInput { click(Offset(centerX, 20f)) }
    }

    @Test
    fun swipeDown_closesMenu() = assertMenuClosedBy {
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_OPEN).performTouchInput {
            swipeDown(startY = top, endY = top + 2000f)
        }
    }

    @Test
    fun menuFiles_importNothingFromCoreAds() {
        // Control: the scan finds an ads import.
        assertTrue(importsCoreAds("package x\n\nimport com.vgl.draw.animation.core.ads.AdSlot\n"))
        assertFalse(importsCoreAds("// No ads in the menu: com.vgl.draw.animation.core.ads is not used here.\n"))
        val files = mapOf(
            "ProjectMenuSheet.kt" to "ModalBottomSheet",
            "ProjectDialogs.kt" to "BasicAlertDialog",
        )
        for ((name, marker) in files) {
            val source = File("src/main/kotlin/com/vgl/draw/animation/feature/projects/$name").readText()
            // Control: the file read is the real menu file, with its imports.
            assertTrue("$name has imports", source.lines().any { it.startsWith("import ") })
            assertTrue("$name uses $marker", source.contains(marker))
            assertFalse("$name imports from :core:ads", importsCoreAds(source))
        }
    }

    @Test
    fun renameDialog_showsTitleButtonsAndCurrentName_focused() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Rename(walk)))
        composeRule.waitUntil(WAIT_MILLIS) {
            composeRule.onAllNodesWithTag(ProjectsTestTags.RENAME_FIELD).fetchSemanticsNodes().isNotEmpty()
        }
        listOf("Rename", "Save", "Cancel").forEach {
            composeRule.onNode(inside(ProjectsTestTags.RENAME_DIALOG, it), useUnmergedTree = true).assertExists()
        }
        assertEquals("Walk cycle", fieldText())
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD).assertIsFocused()
    }

    @Test
    fun renameCancel_callsDismiss() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Rename(walk)))
        assertEquals(0, dismissCount)
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_CANCEL).performClick()
        composeRule.waitForIdle()
        assertEquals(1, dismissCount)
        assertEquals(emptyList<String>(), renamed)
    }

    @Test
    fun renameSave_disabledWhenBlank() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Rename(walk)))
        // Control: the current name can be saved.
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_SAVE).assertIsEnabled()
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_SAVE).assertIsNotEnabled()
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD).performTextReplacement("   ")
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_SAVE).assertIsNotEnabled()
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD).performTextReplacement("a")
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_SAVE).assertIsEnabled()
    }

    @Test
    fun renameField_cutsAt40CodePoints() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Rename(walk)))
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD).performTextReplacement("a".repeat(45))
        assertEquals("a".repeat(40), fieldText())
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD).performTextReplacement("a".repeat(39) + "🎬🎬")
        assertEquals("a".repeat(39) + "🎬", fieldText())
    }

    @Test
    fun deleteDialog_showsTexts() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.ConfirmDelete(walk)))
        composeRule.waitUntil(WAIT_MILLIS) {
            composeRule.onAllNodesWithTag(ProjectsTestTags.DELETE_DIALOG).fetchSemanticsNodes().isNotEmpty()
        }
        listOf("Delete “Walk cycle”?", "This can't be undone.", "Delete", "Cancel").forEach {
            composeRule.onNode(inside(ProjectsTestTags.DELETE_DIALOG, it), useUnmergedTree = true).assertExists()
        }
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun deleteDialog_confirmUsesErrorColor() {
        lateinit var colors: ColorScheme
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                colors = MaterialTheme.colorScheme
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    DeleteDialogCard(name = "Walk cycle", busy = false, onConfirm = {}, onDismiss = {})
                }
            }
        }
        composeRule.waitForIdle()
        val bitmap = windowBitmap()
        val background = colors.surfaceContainerHigh.toArgb()
        val confirm = inkColorsIn(bitmap, bounds(ProjectsTestTags.DELETE_CONFIRM), background)
        assertTrue("confirm pixels: ${confirm.size}", confirm.size >= MIN_INK)
        assertTrue(
            "Delete closer to error",
            shareCloser(confirm, colors.error.toArgb(), colors.primary.toArgb()) > CLOSER_SHARE,
        )
        // Control: Cancel is primary.
        val cancel = inkColorsIn(bitmap, bounds(ProjectsTestTags.DELETE_CANCEL), background)
        assertTrue("cancel pixels: ${cancel.size}", cancel.size >= MIN_INK)
        assertTrue(
            "Cancel closer to primary",
            shareCloser(cancel, colors.primary.toArgb(), colors.error.toArgb()) > CLOSER_SHARE,
        )
    }

    @Test
    fun deleteCancel_callsDismiss() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.ConfirmDelete(walk)))
        assertEquals(0, dismissCount)
        composeRule.onNodeWithTag(ProjectsTestTags.DELETE_CANCEL).performClick()
        composeRule.waitForIdle()
        assertEquals(1, dismissCount)
        assertEquals(0, deleteConfirmCount)
    }

    @Test
    fun unreadableMenu_onlyDelete() {
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Menu(broken)), cards = listOf(broken))
        waitForMenu()
        inMenu("Old test").assertExists()
        inMenu("Delete").assertExists()
        listOf("Open", "Rename", "Duplicate", "Back up to a file").forEach {
            assertEquals("$it in the menu", 0, countInMenu(it))
        }
        val frames = composeRule.onAllNodes(
            SemanticsMatcher("text with frames") { node ->
                node.config.getOrNull(SemanticsProperties.Text).orEmpty().any { "frames" in it.text }
            } and hasAnyAncestor(hasTestTag(ProjectsTestTags.MENU)),
            useUnmergedTree = true,
        ).fetchSemanticsNodes()
        assertEquals(0, frames.size)
    }

    @Test
    fun unreadableNoName_menuAndDeleteTitleUsePlaceholder() {
        // Control: a broken project with a readable name shows that name, not the placeholder.
        val state = showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Menu(broken)), cards = listOf(walk))
        waitForMenu()
        inMenu("Old test").assertExists()
        assertEquals(0, countInMenu("Untitled project"))

        val unnamed = broken.copy(name = null)
        state.value = ProjectsMenuState(overlay = ProjectsOverlay.Menu(unnamed))
        composeRule.waitForIdle()
        waitForMenu()
        inMenu("Untitled project").assertExists()
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_DELETE).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Delete “Untitled project”?").assertExists()
    }

    @Test
    fun scrollToTop_scrollsGridToFirstCard() {
        val repository = FakeProjectsRepository()
        repository.projects.value = (1..20).map { readableItem(it) }
        val viewModel = viewModel(repository, FakeFeatureFlags())
        lateinit var gridState: LazyGridState
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                gridState = rememberLazyGridState()
                Route(viewModel, gridState = gridState)
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.GRID).performScrollToIndex(15)
        composeRule.waitForIdle()
        // Control: the grid did scroll.
        assertTrue("scrolled: ${gridState.firstVisibleItemIndex}", gridState.firstVisibleItemIndex > 0)

        // Item 15 of the grid is the 14th card: after "New animation" and the header.
        composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(14))).performTouchInput { longClick() }
        waitForMenu()
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_DUPLICATE).performClick()
        composeRule.waitUntil(WAIT_MILLIS) { gridState.firstVisibleItemIndex == 0 }
        val copy = repository.projects.value.first().id
        composeRule.onNodeWithTag(ProjectsTestTags.card(copy)).assertExists()
    }

    @Test
    fun duplicating_otherMessageShowsImmediately() {
        val repository = FakeProjectsRepository()
        repository.projects.value = listOf(readableItem(1, name = "Walk cycle", frames = 24), readableItem(3))
        val gate = CompletableDeferred<Unit>()
        repository.duplicateGate = gate
        val viewModel = viewModel(repository, FakeFeatureFlags(FeatureFlag.EDITOR))
        composeRule.setContent { AppTheme(darkTheme = false) { Route(viewModel) } }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.card(WALK)).performTouchInput { longClick() }
        waitForMenu()
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_DUPLICATE).performClick()
        composeRule.waitUntil(WAIT_MILLIS) {
            composeRule.onAllNodesWithText(DUPLICATING_WALK).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(3))).performClick()
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.onNodeWithText(COMING_SOON).assertExists()
        composeRule.onAllNodesWithText("Duplicating", substring = true).assertCountEquals(0)

        // "Coming soon" hides after 4 s; the copy still runs, so "Duplicating…" shows again.
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.onNodeWithText(DUPLICATING_WALK).assertExists()

        gate.complete(Unit)
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.onAllNodesWithText("Duplicating", substring = true).assertCountEquals(0)
    }

    @Test
    fun duplicating_showsSnackbarAndDisablesItem() {
        val state = showScreen(
            ProjectsMenuState(overlay = ProjectsOverlay.Menu(walk), duplicatingName = "Walk cycle"),
        )
        waitForMenu()
        composeRule.onNodeWithText(DUPLICATING_WALK).assertExists()
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_DUPLICATE).assertIsNotEnabled()

        // Control: no copy running.
        state.value = state.value.copy(duplicatingName = null)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_DUPLICATE).assertIsEnabled()
        composeRule.onAllNodesWithText("Duplicating", substring = true).assertCountEquals(0)
    }

    @Test
    fun backUp_withoutDestination_showsComingSoon() {
        val repository = FakeProjectsRepository()
        repository.projects.value = listOf(readableItem(1, name = "Walk cycle", frames = 24))
        val viewModel = viewModel(repository, FakeFeatureFlags())
        composeRule.setContent { AppTheme(darkTheme = false) { Route(viewModel) } }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.card(WALK)).performTouchInput { longClick() }
        waitForMenu()
        composeRule.mainClock.autoAdvance = false
        composeRule.onAllNodesWithText(COMING_SOON).assertCountEquals(0)
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_BACKUP).performClick()
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.onNodeWithText(COMING_SOON).assertExists()
        assertTrue("menu closed", menuNodes().isEmpty())
    }

    @Test
    fun backUp_withDestination_callsWithIdAndName() {
        val repository = FakeProjectsRepository()
        repository.projects.value = listOf(readableItem(1, name = "Walk cycle", frames = 24))
        val viewModel = viewModel(repository, FakeFeatureFlags())
        val backUps = mutableListOf<Pair<ProjectId, String>>()
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                Route(viewModel, onBackUpProject = { id, name -> backUps += id to name })
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.card(WALK)).performTouchInput { longClick() }
        waitForMenu()
        composeRule.onNodeWithTag(ProjectsTestTags.MENU_BACKUP).performClick()
        composeRule.waitForIdle()
        assertEquals(listOf(WALK to "Walk cycle"), backUps)
        composeRule.onAllNodesWithText(COMING_SOON).assertCountEquals(0)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun small_menuFits() {
        val longName = "W".repeat(40)
        showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Menu(walk.copy(name = longName))))
        waitForMenu()
        composeRule.waitForIdle()
        val windowHeight = menuWindowHeight()
        val menu = bounds(ProjectsTestTags.MENU)
        val delete = bounds(ProjectsTestTags.MENU_DELETE)
        assertTrue("menu top ${menu.top}", menu.top >= 0f)
        assertTrue("delete $delete inside window of $windowHeight", delete.top >= 0f && delete.bottom <= windowHeight)
        val name = inMenu(longName).fetchSemanticsNode()
        val layouts = mutableListOf<TextLayoutResult>()
        name.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(layouts)
        val layout = layouts.single()
        assertEquals(1, layout.lineCount)
        assertTrue("name is cut with an ellipsis", layout.isLineEllipsized(0))
    }

    @Test
    @Config(qualifiers = "vi")
    fun vietnamese_menuMetaKeepsNumbers() {
        val state = showScreen(ProjectsMenuState(overlay = ProjectsOverlay.Menu(walk)))
        waitForMenu()
        inMenu("24 khung · 12 fps · sửa hôm nay").assertExists()
        state.value = ProjectsMenuState(overlay = ProjectsOverlay.ConfirmDelete(walk))
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Xóa “Walk cycle”?").assertExists()
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun renameDialog_buttonsUsePrimary() {
        lateinit var colors: ColorScheme
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                colors = MaterialTheme.colorScheme
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    RenameDialogCard(initialName = "Walk cycle", busy = false, onConfirm = {}, onDismiss = {})
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_SAVE).assertIsEnabled()
        val bitmap = windowBitmap()
        val background = colors.surfaceContainerHigh.toArgb()
        for (tag in listOf(ProjectsTestTags.RENAME_SAVE, ProjectsTestTags.RENAME_CANCEL)) {
            val ink = inkColorsIn(bitmap, bounds(tag), background)
            assertTrue("$tag pixels: ${ink.size}", ink.size >= MIN_INK)
            assertTrue(
                "$tag closer to primary",
                shareCloser(ink, colors.primary.toArgb(), colors.onSurface.toArgb()) > CLOSER_SHARE,
            )
        }
    }

    @Test
    fun renameText_survivesRestore() {
        val restorationTester = StateRestorationTester(composeRule)
        val state = mutableStateOf(ProjectsMenuState(overlay = ProjectsOverlay.Rename(walk)))
        restorationTester.setContent {
            AppTheme(darkTheme = false) { Screen(state, listOf(walk), openOnLongClick = true) }
        }
        composeRule.waitForIdle()
        // Control: the field starts with the current name.
        assertEquals("Walk cycle", fieldText())
        composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD).performTextReplacement("Walk lo")
        assertEquals("Walk lo", fieldText())

        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.waitForIdle()
        assertEquals("Walk lo", fieldText())
    }

    @Composable
    private fun MenuContentOnBackground(card: ProjectCardUi, bottomInset: Dp) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
                ProjectMenuContent(
                    card = card,
                    thumbnail = null,
                    nowMillis = NOW,
                    zone = UTC,
                    duplicateEnabled = true,
                    bottomInset = bottomInset,
                    onOpen = {},
                    onRename = {},
                    onDuplicate = {},
                    onBackUp = {},
                    onDelete = {},
                )
            }
        }
    }

    @Composable
    private fun Route(
        viewModel: ProjectsViewModel,
        gridState: LazyGridState = rememberLazyGridState(),
        onBackUpProject: ((ProjectId, String) -> Unit)? = null,
    ) {
        ProjectsRoute(
            viewModel = viewModel,
            onNewProject = null,
            onOpenProject = null,
            onOpenLessons = null,
            onOpenSettings = null,
            onImport = null,
            onFeatureDisabled = {},
            onBackUpProject = onBackUpProject,
            gridState = gridState,
        )
    }

    private fun viewModel(repository: FakeProjectsRepository, flags: FakeFeatureFlags) = ProjectsViewModel(
        repository = repository,
        featureFlags = flags,
        analytics = NoAnalytics(),
        logger = RecordingAppLogger(),
    )

    private fun fieldText(): String = composeRule.onNodeWithTag(ProjectsTestTags.RENAME_FIELD)
        .fetchSemanticsNode().config[SemanticsProperties.EditableText].text

    /** The root of the window that holds the menu: the sheet window, with its scrim. */
    private fun menuWindowRoot(): SemanticsNodeInteraction {
        val roots = composeRule.onAllNodes(isRoot(), useUnmergedTree = true)
        val count = roots.fetchSemanticsNodes().size
        val index = (0 until count).first { hasTag(roots[it].fetchSemanticsNode(), ProjectsTestTags.MENU) }
        return roots[index]
    }

    private fun hasTag(node: SemanticsNode, tag: String): Boolean =
        node.config.getOrNull(SemanticsProperties.TestTag) == tag || node.children.any { hasTag(it, tag) }

    /** The 24 dp leading icon of a menu item: 16 dp from the item's left edge, centered vertically. */
    private fun leadingIcon(tag: String): Rect {
        val item = bounds(tag)
        val left = item.left + px(16.dp)
        val center = item.center.y
        return Rect(left, center - px(12.dp), left + px(24.dp), center + px(12.dp))
    }

    private fun windowBitmap(): Bitmap {
        val view = (composeRule as AndroidComposeTestRule<*, *>).activity.window.decorView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        return bitmap
    }

    /** Every pixel of [area] that differs from [background] by more than 24 over the three channels. */
    private fun inkColorsIn(bitmap: Bitmap, area: Rect, background: Int): List<Int> {
        val pixels = mutableListOf<Int>()
        for (y in area.top.toInt().coerceAtLeast(0) until area.bottom.toInt().coerceAtMost(bitmap.height)) {
            for (x in area.left.toInt().coerceAtLeast(0) until area.right.toInt().coerceAtMost(bitmap.width)) {
                val pixel = bitmap.getPixel(x, y)
                if (distance(pixel, background) > INK_THRESHOLD) pixels += pixel
            }
        }
        return pixels
    }

    /** Share of [pixels] closer to [target] than to [other]. */
    private fun shareCloser(pixels: List<Int>, target: Int, other: Int): Float =
        pixels.count { distance(it, target) < distance(it, other) }.toFloat() / pixels.size

    private fun distance(a: Int, b: Int): Int {
        val ca = Color(a)
        val cb = Color(b)
        return abs(channel(ca.red) - channel(cb.red)) + abs(channel(ca.green) - channel(cb.green)) +
            abs(channel(ca.blue) - channel(cb.blue))
    }

    private fun channel(value: Float): Int = (value * 255f + 0.5f).toInt()

    /** True when [source] has an import line from :core:ads. */
    private fun importsCoreAds(source: String): Boolean =
        source.lines().any { it.trim().startsWith("import com.vgl.draw.animation.core.ads") }

    private companion object {
        val WALK: ProjectId = testProjectId(1)
        val BROKEN: ProjectId = testProjectId(2)
        val UTC: ZoneId = ZoneOffset.UTC

        // 2026-11-22 12:00 UTC: the middle of the day, so "edited today" does not depend on the machine's zone.
        val NOW: Long = Instant.parse("2026-11-22T12:00:00Z").toEpochMilli()
        const val WAIT_MILLIS = 3_000L
        const val MIN_INK = 20
        const val CLOSER_SHARE = 0.7f
        const val INK_THRESHOLD = 24
        const val COMING_SOON = "Coming soon"
        const val DUPLICATING_WALK = "Duplicating “Walk cycle”…"
    }
}
