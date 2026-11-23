package com.vgl.draw.animation.feature.projects

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.FrameRate
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests of [ProjectsViewModel]. State is collected in `backgroundScope` and waited for with `runCurrent()`;
 * never `advanceUntilIdle()`, which stops while only background work is left (L-005).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProjectsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeProjectsRepository()
    private val flags = FakeFeatureFlags()
    private val logger = RecordingAppLogger()

    private fun viewModel() = ProjectsViewModel(
        repository = repository,
        featureFlags = flags,
        analytics = NoAnalytics(),
        logger = logger,
    )

    /** Collects uiState (and thumbnails) in the background, as the route does. */
    private fun TestScope.started(): ProjectsViewModel {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect {} }
        backgroundScope.launch { viewModel.thumbnails.collect {} }
        runCurrent()
        return viewModel
    }

    private fun TestScope.effectsOf(viewModel: ProjectsViewModel): List<ProjectsEffect> {
        val effects = mutableListOf<ProjectsEffect>()
        backgroundScope.launch { viewModel.effects.collect { effects += it } }
        runCurrent()
        return effects
    }

    private fun ProjectsViewModel.cards(): List<ProjectCardUi> = (uiState.value as ProjectsUiState.List).cards

    private fun image(): ImageBitmap = mockk(relaxed = true)

    @Test
    fun emptyRepository_givesEmpty() = runTest {
        val viewModel = started()
        assertEquals(ProjectsUiState.Empty(showLessonButton = true, showSettingsButton = true), viewModel.uiState.value)

        // Control: one project gives a list; deleting the last one goes back to empty at once (KB-1).
        repository.projects.value = listOf(readableItem(1))
        runCurrent()
        assertTrue(viewModel.uiState.value is ProjectsUiState.List)
        repository.projects.value = emptyList()
        runCurrent()
        assertEquals(ProjectsUiState.Empty(showLessonButton = true, showSettingsButton = true), viewModel.uiState.value)
    }

    @Test
    fun lessonsFlagOff_hidesLessonButton() = runTest {
        flags[FeatureFlag.LESSONS] = false
        val viewModel = started()
        assertEquals(
            ProjectsUiState.Empty(showLessonButton = false, showSettingsButton = true),
            viewModel.uiState.value,
        )
    }

    @Test
    fun lessonsFlagTurnsOffWhileShown_hidesLessonButton() = runTest {
        val viewModel = started()
        assertEquals(true, (viewModel.uiState.value as ProjectsUiState.Empty).showLessonButton)
        flags[FeatureFlag.LESSONS] = false
        runCurrent()
        assertEquals(false, (viewModel.uiState.value as ProjectsUiState.Empty).showLessonButton)
    }

    @Test
    fun projects_keepRepositoryOrder() = runTest {
        // The repository order is the contract (T-002); the ViewModel must not sort again, even by updatedAtMillis.
        repository.projects.value = listOf(
            readableItem(1, updatedAtMillis = 10L),
            readableItem(2, updatedAtMillis = 30L),
            readableItem(3, updatedAtMillis = 20L),
        )
        val viewModel = started()
        assertEquals(listOf(testProjectId(1), testProjectId(2), testProjectId(3)), viewModel.cards().map { it.id })
    }

    @Test
    fun card_hasFrameCountAndFps() = runTest {
        repository.projects.value = listOf(readableItem(1, name = "Bouncing ball", frames = 48, fps = FrameRate.FPS_12))
        val viewModel = started()
        val card = viewModel.cards().single()
        assertEquals("Bouncing ball", card.name)
        assertEquals(48, card.frameCount)
        assertEquals(12, card.fps)
        assertTrue(card.readable)
    }

    @Test
    fun thumbnail_reloadsWhenUpdatedAtChanges() = runTest {
        val first = image()
        repository.projects.value = listOf(readableItem(1, updatedAtMillis = 100L))
        repository.images[testProjectId(1)] = first
        val viewModel = started()
        viewModel.onThumbnailNeeded(viewModel.cards().single(), 200)
        runCurrent()
        assertSame(first, viewModel.thumbnails.value[testProjectId(1)])
        assertEquals(1, repository.readsOf(testProjectId(1)))

        // The same version again: served from memory, no new read.
        viewModel.onThumbnailNeeded(viewModel.cards().single(), 200)
        runCurrent()
        assertEquals(1, repository.readsOf(testProjectId(1)))

        // The project was edited: the new version is read and replaces the old image.
        val second = image()
        repository.images[testProjectId(1)] = second
        repository.projects.value = listOf(readableItem(1, updatedAtMillis = 200L))
        runCurrent()
        viewModel.onThumbnailNeeded(viewModel.cards().single(), 200)
        runCurrent()
        assertEquals(2, repository.readsOf(testProjectId(1)))
        assertSame(second, viewModel.thumbnails.value[testProjectId(1)])
    }

    @Test
    fun thumbnail_twoRequestsSameKey_readOnce() = runTest {
        repository.projects.value = listOf(readableItem(1))
        repository.images[testProjectId(1)] = image()
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate
        val viewModel = started()
        val card = viewModel.cards().single()
        viewModel.onThumbnailNeeded(card, 200)
        runCurrent()
        viewModel.onThumbnailNeeded(card, 200)
        runCurrent()
        gate.complete(Unit)
        runCurrent()
        assertEquals(1, repository.readsOf(testProjectId(1)))
        assertTrue(viewModel.thumbnails.value[testProjectId(1)] != null)
    }

    @Test
    fun thumbnail_droppedWhenProjectRemoved() = runTest {
        repository.projects.value = listOf(readableItem(1), readableItem(2))
        repository.images[testProjectId(1)] = image()
        repository.images[testProjectId(2)] = image()
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        // Only uiState collects the repository: no second collection of items.
        assertEquals(1, repository.collectors)

        viewModel.cards().forEach { viewModel.onThumbnailNeeded(it, 200) }
        runCurrent()
        assertEquals(setOf(testProjectId(1), testProjectId(2)), viewModel.thumbnails.value.keys)

        repository.projects.value = listOf(readableItem(2))
        runCurrent()
        assertFalse(viewModel.thumbnails.value.containsKey(testProjectId(1)))
        // Control: the project still in the list keeps its image.
        assertTrue(viewModel.thumbnails.value.containsKey(testProjectId(2)))
        assertEquals(1, repository.collectors)
    }

    @Test
    fun thumbnail_cacheKeepsAtMost40() = runTest {
        val count = ProjectsViewModel.THUMBNAIL_CACHE_SIZE + 1
        repository.projects.value = (1..count).map { readableItem(it) }
        (1..count).forEach { repository.images[testProjectId(it)] = image() }
        val viewModel = started()
        viewModel.cards().forEach {
            viewModel.onThumbnailNeeded(it, 200)
            runCurrent()
        }
        assertEquals(40, viewModel.thumbnails.value.size)
        assertFalse(viewModel.thumbnails.value.containsKey(testProjectId(1)))

        viewModel.onThumbnailNeeded(viewModel.cards().first(), 200)
        runCurrent()
        assertEquals(2, repository.readsOf(testProjectId(1)))
        assertTrue(viewModel.thumbnails.value.containsKey(testProjectId(1)))
    }

    @Test
    fun clickReadable_editorOn_emitsOpenProject() = runTest {
        repository.projects.value = listOf(readableItem(1))
        val viewModel = started()
        val effects = effectsOf(viewModel)
        viewModel.onProjectClick(viewModel.cards().single())
        runCurrent()
        assertEquals(listOf(ProjectsEffect.OpenProject(testProjectId(1))), effects)
    }

    @Test
    fun clickReadable_editorOff_emitsComingSoon() = runTest {
        flags[FeatureFlag.EDITOR] = false
        repository.projects.value = listOf(readableItem(1))
        val viewModel = started()
        val effects = effectsOf(viewModel)
        viewModel.onProjectClick(viewModel.cards().single())
        runCurrent()
        assertEquals(listOf(ProjectsEffect.ShowComingSoon), effects)
    }

    @Test
    fun editorFlagTurnsOff_thenClick_emitsComingSoon() = runTest {
        repository.projects.value = listOf(readableItem(1))
        val viewModel = started()
        val effects = effectsOf(viewModel)
        val card = viewModel.cards().single()
        viewModel.onProjectClick(card)
        runCurrent()
        flags[FeatureFlag.EDITOR] = false
        runCurrent()
        viewModel.onProjectClick(card)
        runCurrent()
        assertEquals(listOf(ProjectsEffect.OpenProject(testProjectId(1)), ProjectsEffect.ShowComingSoon), effects)
    }

    @Test
    fun unreadable_isShownAndOthersOpen() = runTest {
        repository.projects.value = listOf(readableItem(1, name = "Walk"), unreadableItem(2, name = "Broken"))
        val viewModel = started()
        val cards = viewModel.cards()
        assertEquals(listOf(true, false), cards.map { it.readable })
        assertEquals("Broken", cards[1].name)
        val effects = effectsOf(viewModel)
        viewModel.onProjectClick(cards[0])
        runCurrent()
        assertEquals(listOf(ProjectsEffect.OpenProject(testProjectId(1))), effects)
    }

    @Test
    fun clickUnreadable_emitsCannotOpen() = runTest {
        repository.projects.value = listOf(unreadableItem(2))
        val viewModel = started()
        val effects = effectsOf(viewModel)
        viewModel.onProjectClick(viewModel.cards().single())
        runCurrent()
        assertEquals(listOf(ProjectsEffect.ShowCannotOpen), effects)
        assertNull(viewModel.cards().single().name)
    }

    @Test
    fun unreadable_thumbnailNotRead() = runTest {
        repository.projects.value = listOf(readableItem(1), unreadableItem(2, name = "Broken"))
        // Both ids have an image: a broken folder may still hold an old thumbnail.png.
        repository.images[testProjectId(1)] = image()
        repository.images[testProjectId(2)] = image()
        val viewModel = started()
        viewModel.cards().forEach { viewModel.onThumbnailNeeded(it, 200) }
        runCurrent()
        assertTrue(viewModel.thumbnails.value[testProjectId(1)] != null)
        assertNull(viewModel.thumbnails.value[testProjectId(2)])
        assertEquals(0, repository.readsOf(testProjectId(2)))
    }

    @Test
    fun settingsFlagOff_hidesSettings() = runTest {
        flags[FeatureFlag.SETTINGS] = false
        repository.projects.value = listOf(readableItem(1))
        val viewModel = started()
        assertEquals(false, (viewModel.uiState.value as ProjectsUiState.List).showSettingsButton)
        repository.projects.value = emptyList()
        runCurrent()
        assertEquals(false, (viewModel.uiState.value as ProjectsUiState.Empty).showSettingsButton)
    }

    @Test
    fun settingsFlagTurnsOn_showsSettings() = runTest {
        flags[FeatureFlag.SETTINGS] = false
        val viewModel = started()
        assertEquals(false, (viewModel.uiState.value as ProjectsUiState.Empty).showSettingsButton)
        flags[FeatureFlag.SETTINGS] = true
        runCurrent()
        assertEquals(true, (viewModel.uiState.value as ProjectsUiState.Empty).showSettingsButton)
    }

    @Test
    fun projectsFlagOff_givesDisabled() = runTest {
        flags[FeatureFlag.PROJECTS] = false
        repository.projects.value = listOf(readableItem(1))
        val viewModel = started()
        assertEquals(ProjectsUiState.Disabled, viewModel.uiState.value)
    }

    @Test
    fun projectsFlagTurnsOffWhileListed_givesDisabled() = runTest {
        repository.projects.value = listOf(readableItem(1))
        val viewModel = started()
        assertTrue(viewModel.uiState.value is ProjectsUiState.List)
        flags[FeatureFlag.PROJECTS] = false
        runCurrent()
        assertEquals(ProjectsUiState.Disabled, viewModel.uiState.value)
    }
}
