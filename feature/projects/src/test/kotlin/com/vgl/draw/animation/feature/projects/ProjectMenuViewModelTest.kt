package com.vgl.draw.animation.feature.projects

import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * The project menu of [ProjectsViewModel] (plan T-012, section 4): open, rename, duplicate, delete, back up. State is
 * collected in `backgroundScope` and waited for with `runCurrent()`, never `advanceUntilIdle()` (L-005).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProjectMenuViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeProjectsRepository()
    private val flags = FakeFeatureFlags()

    private val walkItem = readableItem(1, name = "Walk cycle", frames = 24)
    private val ballItem = readableItem(3, name = "Bouncing ball", frames = 48)
    private val brokenItem = unreadableItem(2, name = "Old test")

    private fun viewModel() = ProjectsViewModel(
        repository = repository,
        featureFlags = flags,
        analytics = NoAnalytics(),
        logger = RecordingAppLogger(),
    )

    /** Collects uiState and menuState in the background, as the route does. */
    private fun TestScope.started(): ProjectsViewModel {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect {} }
        backgroundScope.launch { viewModel.menuState.collect {} }
        runCurrent()
        return viewModel
    }

    private fun TestScope.effectsOf(viewModel: ProjectsViewModel): MutableList<ProjectsEffect> {
        val effects = mutableListOf<ProjectsEffect>()
        backgroundScope.launch { viewModel.effects.collect { effects += it } }
        runCurrent()
        return effects
    }

    private fun ProjectsViewModel.cards(): List<ProjectCardUi> = (uiState.value as ProjectsUiState.List).cards

    private fun ProjectsViewModel.card(id: ProjectId): ProjectCardUi = cards().first { it.id == id }

    private fun ProjectsViewModel.overlay(): ProjectsOverlay = menuState.value.overlay

    /** Opens the menu of [id] and checks it is open (control of every test that acts from the menu). */
    private fun TestScope.openMenu(viewModel: ProjectsViewModel, id: ProjectId = WALK): ProjectCardUi {
        val card = viewModel.card(id)
        viewModel.onProjectLongClick(card)
        runCurrent()
        assertEquals(ProjectsOverlay.Menu(card), viewModel.overlay())
        return card
    }

    private fun TestScope.openRename(viewModel: ProjectsViewModel): ProjectCardUi {
        val card = openMenu(viewModel)
        viewModel.onMenuRename()
        runCurrent()
        assertEquals(ProjectsOverlay.Rename(card), viewModel.overlay())
        return card
    }

    private fun TestScope.openDelete(viewModel: ProjectsViewModel, id: ProjectId = WALK): ProjectCardUi {
        val card = openMenu(viewModel, id)
        viewModel.onMenuDelete()
        runCurrent()
        assertEquals(ProjectsOverlay.ConfirmDelete(card), viewModel.overlay())
        return card
    }

    @Test
    fun longClick_opensMenuForThatCard() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        // Control: nothing is open before the long press.
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
        val walk = viewModel.card(WALK)
        viewModel.onProjectLongClick(walk)
        runCurrent()
        assertEquals(ProjectsOverlay.Menu(walk), viewModel.overlay())
    }

    @Test
    fun longClick_whileOverlayOpen_ignored() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        val walk = openMenu(viewModel)
        viewModel.onProjectLongClick(viewModel.card(BALL))
        runCurrent()
        assertEquals(ProjectsOverlay.Menu(walk), viewModel.overlay())
    }

    @Test
    fun menuOpen_editorOn_emitsOpenProjectAndCloses() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        openMenu(viewModel)
        viewModel.onMenuOpen()
        runCurrent()
        assertEquals(listOf<ProjectsEffect>(ProjectsEffect.OpenProject(WALK)), effects)
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun menuOpen_editorOff_emitsComingSoon() = runTest {
        flags[FeatureFlag.EDITOR] = false
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        openMenu(viewModel)
        viewModel.onMenuOpen()
        runCurrent()
        assertEquals(listOf<ProjectsEffect>(ProjectsEffect.ShowComingSoon), effects)
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun menuOpen_editorTurnsOffWhileMenuOpen_emitsComingSoon() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        openMenu(viewModel)
        flags[FeatureFlag.EDITOR] = false
        runCurrent()
        viewModel.onMenuOpen()
        runCurrent()
        assertEquals(listOf<ProjectsEffect>(ProjectsEffect.ShowComingSoon), effects)
    }

    @Test
    fun dismiss_closesWithoutRepositoryCalls() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        val before = viewModel.cards()
        openMenu(viewModel)
        viewModel.dismissOverlay()
        runCurrent()
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
        assertEquals(0, repository.writeCalls)
        assertEquals(before, viewModel.cards())
    }

    @Test
    fun menuRename_opensRenameDialog() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val walk = openMenu(viewModel)
        viewModel.onMenuRename()
        runCurrent()
        assertEquals(ProjectsOverlay.Rename(walk), viewModel.overlay())
    }

    @Test
    fun renameConfirm_callsRepositoryAndCloses() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        openRename(viewModel)
        viewModel.onRenameConfirm("Walk loop")
        runCurrent()
        assertEquals(listOf(WALK to "Walk loop"), repository.renameCalls)
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun renameConfirm_listShowsNewName() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        openRename(viewModel)
        viewModel.onRenameConfirm("Walk loop")
        runCurrent()
        assertEquals("Walk loop", viewModel.cards().first().name)
    }

    @Test
    fun renameConfirm_sameName_noWrite() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        openRename(viewModel)
        viewModel.onRenameConfirm(" Walk cycle ")
        runCurrent()
        assertEquals(0, repository.renameCalls.size)
        assertEquals(ProjectsOverlay.None, viewModel.overlay())

        // Control: another name is written.
        openRename(viewModel)
        viewModel.onRenameConfirm("Walk cycle 2")
        runCurrent()
        assertEquals(1, repository.renameCalls.size)
    }

    @Test
    fun renameCancel_noWrite() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        openRename(viewModel)
        viewModel.dismissOverlay()
        runCurrent()
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
        assertEquals(0, repository.writeCalls)
    }

    @Test
    fun renameConfirm_blank_ignored() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val walk = openRename(viewModel)
        viewModel.onRenameConfirm("")
        viewModel.onRenameConfirm("   ")
        runCurrent()
        assertEquals(0, repository.renameCalls.size)
        assertEquals(ProjectsOverlay.Rename(walk), viewModel.overlay())
    }

    @Test
    fun renameConfirm_duplicateName_stillSaves() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        openRename(viewModel)
        viewModel.onRenameConfirm("Bouncing ball")
        runCurrent()
        assertEquals(listOf(WALK to "Bouncing ball"), repository.renameCalls)
    }

    @Test
    fun renameFailure_closesAndEmitsMessage() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        val cases = listOf(
            ProjectFailure.WRITE_FAILED to ProjectsMessage.RENAME_FAILED,
            ProjectFailure.NOT_FOUND to ProjectsMessage.RENAME_FAILED,
            ProjectFailure.UNREADABLE to ProjectsMessage.RENAME_FAILED,
            ProjectFailure.NO_SPACE to ProjectsMessage.NO_SPACE,
        )
        for ((failure, message) in cases) {
            effects.clear()
            repository.renameResult = ProjectResult.Failure(failure)
            openRename(viewModel)
            viewModel.onRenameConfirm("Walk loop")
            runCurrent()
            assertEquals("$failure", ProjectsOverlay.None, viewModel.overlay())
            assertEquals("$failure", listOf<ProjectsEffect>(ProjectsEffect.ShowMessage(message)), effects)
        }
    }

    @Test
    fun renameConfirm_twiceWhileWriting_writesOnce() = runTest {
        repository.projects.value = listOf(walkItem)
        val gate = CompletableDeferred<Unit>()
        repository.renameGate = gate
        val viewModel = started()
        openRename(viewModel)
        viewModel.onRenameConfirm("Walk loop")
        runCurrent()
        assertTrue("busy while writing", viewModel.menuState.value.busy)
        viewModel.onRenameConfirm("Walk loop")
        runCurrent()
        gate.complete(Unit)
        runCurrent()
        assertEquals(1, repository.renameCalls.size)
        assertFalse(viewModel.menuState.value.busy)
    }

    @Test
    fun menuDuplicate_callsRepositoryAndCloses() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals(listOf(WALK), repository.duplicateCalls)
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun duplicateSuccess_copyFirstInList() = runTest {
        repository.projects.value = listOf(ballItem, walkItem)
        val viewModel = started()
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals("Walk cycle copy", viewModel.cards().first().name)
    }

    @Test
    fun duplicateSuccess_scrollsToTop() = runTest {
        repository.projects.value = listOf(ballItem, walkItem)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals(1, effects.count { it == ProjectsEffect.ScrollToTop })

        // Control: a failed copy does not scroll.
        repository.duplicateResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals(1, effects.count { it == ProjectsEffect.ScrollToTop })
    }

    @Test
    fun duplicateNoSpace_emitsNoSpaceMessage() = runTest {
        repository.projects.value = listOf(walkItem)
        repository.duplicateResult = ProjectResult.Failure(ProjectFailure.NO_SPACE)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals(listOf<ProjectsEffect>(ProjectsEffect.ShowMessage(ProjectsMessage.NO_SPACE)), effects)
        assertNull(viewModel.menuState.value.duplicatingName)
    }

    @Test
    fun duplicateWriteFailed_emitsDuplicateFailed() = runTest {
        repository.projects.value = listOf(walkItem)
        repository.duplicateResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals(listOf<ProjectsEffect>(ProjectsEffect.ShowMessage(ProjectsMessage.DUPLICATE_FAILED)), effects)
    }

    @Test
    fun duplicate_inProgress_setsDuplicatingAndBlocksSecond() = runTest {
        repository.projects.value = listOf(walkItem)
        val gate = CompletableDeferred<Unit>()
        repository.duplicateGate = gate
        val viewModel = started()
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals("Walk cycle", viewModel.menuState.value.duplicatingName)

        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        assertEquals(1, repository.duplicateCalls.size)

        gate.complete(Unit)
        runCurrent()
        assertNull(viewModel.menuState.value.duplicatingName)
        assertEquals(1, repository.duplicateCalls.size)
    }

    @Test
    fun duplicate_failure_clearsDuplicating() = runTest {
        repository.projects.value = listOf(walkItem)
        repository.duplicateResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        val gate = CompletableDeferred<Unit>()
        repository.duplicateGate = gate
        val viewModel = started()
        // The duplicating name at the moment each effect arrives.
        val namesAtEffect = mutableListOf<Pair<ProjectsEffect, String?>>()
        backgroundScope.launch {
            viewModel.effects.collect { namesAtEffect += it to viewModel.menuState.value.duplicatingName }
        }
        runCurrent()
        openMenu(viewModel)
        viewModel.onMenuDuplicate()
        runCurrent()
        // Control: the name is set while the copy runs.
        assertEquals("Walk cycle", viewModel.menuState.value.duplicatingName)
        gate.complete(Unit)
        runCurrent()
        assertEquals(
            listOf<Pair<ProjectsEffect, String?>>(ProjectsEffect.ShowMessage(ProjectsMessage.DUPLICATE_FAILED) to null),
            namesAtEffect,
        )
    }

    @Test
    fun menuDelete_opensConfirm() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val walk = openMenu(viewModel)
        viewModel.onMenuDelete()
        runCurrent()
        assertEquals(ProjectsOverlay.ConfirmDelete(walk), viewModel.overlay())
    }

    @Test
    fun deleteConfirm_callsRepositoryAndCardGone() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        openDelete(viewModel)
        viewModel.onDeleteConfirm()
        runCurrent()
        assertEquals(listOf(WALK), repository.deleteCalls)
        assertTrue(viewModel.cards().none { it.id == WALK })
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun deleteCancel_noWrite() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        openDelete(viewModel)
        viewModel.dismissOverlay()
        runCurrent()
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
        assertEquals(0, repository.writeCalls)
    }

    @Test
    fun deleteLastProject_givesEmpty() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        assertTrue(viewModel.uiState.value is ProjectsUiState.List)
        openDelete(viewModel)
        viewModel.onDeleteConfirm()
        runCurrent()
        assertTrue(viewModel.uiState.value is ProjectsUiState.Empty)
    }

    @Test
    fun deleteFailure_closesAndEmitsDeleteFailed() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        for (failure in listOf(ProjectFailure.WRITE_FAILED, ProjectFailure.NO_SPACE)) {
            effects.clear()
            repository.deleteResult = ProjectResult.Failure(failure)
            openDelete(viewModel)
            viewModel.onDeleteConfirm()
            runCurrent()
            assertEquals("$failure", ProjectsOverlay.None, viewModel.overlay())
            assertEquals(
                "$failure",
                listOf<ProjectsEffect>(ProjectsEffect.ShowMessage(ProjectsMessage.DELETE_FAILED)),
                effects,
            )
        }
    }

    @Test
    fun deleteConfirm_twiceWhileWriting_deletesOnce() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val gate = CompletableDeferred<Unit>()
        repository.deleteGate = gate
        val viewModel = started()
        openDelete(viewModel)
        viewModel.onDeleteConfirm()
        runCurrent()
        assertTrue("busy while deleting", viewModel.menuState.value.busy)
        viewModel.onDeleteConfirm()
        runCurrent()
        gate.complete(Unit)
        runCurrent()
        assertEquals(1, repository.deleteCalls.size)
    }

    @Test
    fun dismiss_whileBusy_ignored() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val gate = CompletableDeferred<Unit>()
        repository.deleteGate = gate
        val viewModel = started()
        val walk = openDelete(viewModel)
        viewModel.onDeleteConfirm()
        runCurrent()
        viewModel.dismissOverlay()
        runCurrent()
        assertEquals(ProjectsOverlay.ConfirmDelete(walk), viewModel.overlay())
        gate.complete(Unit)
        runCurrent()
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun unreadable_deleteConfirmed_deletes() = runTest {
        repository.projects.value = listOf(walkItem, brokenItem)
        val viewModel = started()
        openDelete(viewModel, BROKEN)
        viewModel.onDeleteConfirm()
        runCurrent()
        assertEquals(listOf(BROKEN), repository.deleteCalls)
        assertTrue(viewModel.cards().none { it.id == BROKEN })
    }

    @Test
    fun unreadable_neverDeletedWithoutConfirm() = runTest {
        repository.projects.value = listOf(walkItem, brokenItem)
        val viewModel = started()
        openMenu(viewModel, BROKEN)
        viewModel.dismissOverlay()
        runCurrent()
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
        assertEquals(0, repository.deleteCalls.size)
        assertTrue(viewModel.cards().any { it.id == BROKEN })
    }

    @Test
    fun cardRemovedFromList_closesOverlay() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        openMenu(viewModel)

        // Control: the same project with another updatedAtMillis keeps the menu open.
        repository.projects.value =
            listOf(readableItem(1, name = "Walk cycle", frames = 24, updatedAtMillis = 99L), ballItem)
        runCurrent()
        val overlay = viewModel.overlay()
        assertTrue("menu still open: $overlay", overlay is ProjectsOverlay.Menu && overlay.card.id == WALK)

        repository.projects.value = listOf(ballItem)
        runCurrent()
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun items_readOnceForUiAndMenu() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = viewModel()
        // Control: nothing reads the repository before collecting.
        assertEquals(0, repository.collectors)
        val menuJob = backgroundScope.launch { viewModel.menuState.collect {} }
        runCurrent()
        // The menu watches the list (to close when its card is gone).
        assertEquals(1, repository.collectors)
        val uiJob = backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        assertEquals(1, repository.collectors)

        menuJob.cancel()
        uiJob.cancel()
        runCurrent()
        // Control: the 5 s wait of stateIn is still running.
        advanceTimeBy(4_000)
        runCurrent()
        assertEquals(1, repository.collectors)
        advanceTimeBy(1_001)
        runCurrent()
        assertEquals(0, repository.collectors)
    }

    @Test
    fun menuBackUp_emitsBackUpProjectAndCloses() = runTest {
        repository.projects.value = listOf(walkItem)
        val viewModel = started()
        val effects = effectsOf(viewModel)
        openMenu(viewModel)
        viewModel.onMenuBackUp()
        runCurrent()
        assertEquals(listOf<ProjectsEffect>(ProjectsEffect.BackUpProject(WALK, "Walk cycle")), effects)
        assertEquals(ProjectsOverlay.None, viewModel.overlay())
    }

    @Test
    fun overlay_isViewModelState() = runTest {
        repository.projects.value = listOf(walkItem, ballItem)
        val viewModel = started()
        val walk = openMenu(viewModel)
        // The list is sent again (another project changed): the open menu stays.
        repository.projects.value = listOf(walkItem, readableItem(3, name = "Bouncing ball", updatedAtMillis = 77L))
        runCurrent()
        assertEquals(ProjectsOverlay.Menu(walk), viewModel.overlay())
    }

    private companion object {
        val WALK = testProjectId(1)
        val BROKEN = testProjectId(2)
        val BALL = testProjectId(3)
    }
}
