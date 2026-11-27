package com.vgl.draw.animation.feature.projects.create

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.analytics.AnalyticsEvent
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.model.canvasSize
import com.vgl.draw.animation.core.project.repository.NewBackground
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.FakeFeatureFlags
import com.vgl.draw.animation.feature.projects.MainDispatcherRule
import com.vgl.draw.animation.feature.projects.NoAnalytics
import com.vgl.draw.animation.feature.projects.data.NewProjectDefaults
import com.vgl.draw.animation.feature.projects.data.PreparedPhoto
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests of [NewProjectViewModel]. Effects are collected in `backgroundScope` and waited for with `runCurrent()`
 * (L-005). The repository is a [FakeNewProjectRepository]; flags start on.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewProjectViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeNewProjectRepository()
    private val flags = FakeFeatureFlags()
    private val analytics = NoAnalytics()
    private val logger = RecordingAppLogger()
    private val effects = mutableListOf<NewProjectEffect>()

    private fun TestScope.viewModel(): NewProjectViewModel {
        val viewModel = NewProjectViewModel(repository, flags, analytics, logger)
        backgroundScope.launch { viewModel.effects.collect { effects += it } }
        runCurrent()
        return viewModel
    }

    private fun NewProjectViewModel.ready(): NewProjectUiState.Ready = uiState.value as NewProjectUiState.Ready

    private fun image(): ImageBitmap = mockk(relaxed = true)

    private fun defaults(aspectRatio: AspectRatio, frameRate: FrameRate) = NewProjectDefaults(
        aspectRatio = aspectRatio,
        frameRate = frameRate,
        defaultName = FakeNewProjectRepository.DEFAULT_NAME,
        firstLayerName = FakeNewProjectRepository.FIRST_LAYER,
    )

    private fun createdEffects() = effects.filterIsInstance<NewProjectEffect.ProjectCreated>()

    private fun createEvents() = analytics.events.filter { it.name == "project_create" }

    @Test
    fun defaults_followSettings() = runTest {
        repository.defaults = defaults(AspectRatio.LANDSCAPE_16_9, FrameRate.FPS_24)
        val state = viewModel().ready()
        assertEquals(AspectRatio.LANDSCAPE_16_9, state.aspectRatio)
        assertEquals(FrameRate.FPS_24, state.frameRate)
        assertEquals(Resolution.P1080, state.resolution)
        assertEquals(BackgroundChoice.Solid(BackgroundColor.WHITE), state.background)
        assertEquals("My animation 5", state.name)
        assertEquals("My animation 5", state.defaultName)
        assertFalse(state.creating)
        assertFalse(state.photoLoading)
        assertNull(state.error)

        // Control: other stored values give those values, not fixed ones.
        repository.defaults = defaults(AspectRatio.SQUARE_1_1, FrameRate.FPS_6)
        val other = viewModel().ready()
        assertEquals(AspectRatio.SQUARE_1_1, other.aspectRatio)
        assertEquals(FrameRate.FPS_6, other.frameRate)
    }

    @Test
    fun settingsFlagOff_uses916And12() = runTest {
        repository.defaults = defaults(AspectRatio.LANDSCAPE_16_9, FrameRate.FPS_24)
        flags[FeatureFlag.SETTINGS] = false
        val state = viewModel().ready()
        assertEquals(AspectRatio.PORTRAIT_9_16, state.aspectRatio)
        assertEquals(FrameRate.FPS_12, state.frameRate)
    }

    @Test
    fun settingsFlagTurnsOnLater_keepsChoices() = runTest {
        repository.defaults = defaults(AspectRatio.LANDSCAPE_16_9, FrameRate.FPS_24)
        flags[FeatureFlag.SETTINGS] = false
        val viewModel = viewModel()
        assertEquals(AspectRatio.PORTRAIT_9_16, viewModel.ready().aspectRatio)

        flags[FeatureFlag.SETTINGS] = true
        runCurrent()
        assertEquals(AspectRatio.PORTRAIT_9_16, viewModel.ready().aspectRatio)
        assertEquals(FrameRate.FPS_12, viewModel.ready().frameRate)
    }

    @Test
    fun create_withDefaults_createsOnceWithDefaults() = runTest {
        val viewModel = viewModel()
        viewModel.onCreate()
        runCurrent()
        assertEquals(
            listOf(
                NewProject(
                    name = "My animation 5",
                    aspectRatio = AspectRatio.PORTRAIT_9_16,
                    resolution = Resolution.P1080,
                    frameRate = FrameRate.FPS_12,
                    background = NewBackground.Solid(BackgroundColor.WHITE),
                    firstLayerName = FakeNewProjectRepository.FIRST_LAYER,
                ),
            ),
            repository.created,
        )
        assertEquals(listOf(repository.createdId), createdEffects().map { it.id })
    }

    @Test
    fun create_passesChosenFormat() = runTest {
        val viewModel = viewModel()
        viewModel.onAspectRatio(AspectRatio.LANDSCAPE_16_9)
        viewModel.onResolution(Resolution.P720)
        viewModel.onFrameRate(FrameRate.FPS_30)
        viewModel.onCreate()
        runCurrent()
        val request = repository.created.single()
        assertEquals(AspectRatio.LANDSCAPE_16_9, request.aspectRatio)
        assertEquals(Resolution.P720, request.resolution)
        assertEquals(FrameRate.FPS_30, request.frameRate)
        val size = canvasSize(request.aspectRatio, request.resolution)
        assertEquals(1280, size.width)
        assertEquals(720, size.height)
    }

    @Test
    fun blankName_usesDefaultName() = runTest {
        val viewModel = viewModel()
        viewModel.onNameChange("   ")
        assertEquals("   ", viewModel.ready().name)
        viewModel.onCreate()
        runCurrent()
        assertEquals("My animation 5", repository.created.single().name)
    }

    @Test
    fun typedName_isUsed() = runTest {
        // Control of blankName_usesDefaultName: a typed name is what is created.
        val viewModel = viewModel()
        viewModel.onNameChange("Ball")
        viewModel.onCreate()
        runCurrent()
        assertEquals("Ball", repository.created.single().name)
    }

    @Test
    fun name_keepsAtMost40CodePoints() = runTest {
        val viewModel = viewModel()
        val long = "a".repeat(45)
        viewModel.onNameChange(long)
        assertEquals("a".repeat(40), viewModel.ready().name)

        val withEmoji = "b".repeat(39) + "😀"
        viewModel.onNameChange(withEmoji)
        assertEquals(withEmoji, viewModel.ready().name)
        assertEquals(40, viewModel.ready().name.codePointCount(0, viewModel.ready().name.length))

        // One more code point after the emoji is cut, the emoji stays whole.
        viewModel.onNameChange(withEmoji + "c")
        assertEquals(withEmoji, viewModel.ready().name)
    }

    @Test
    fun photoPicked_setsPhotoBackground() = runTest {
        val preview = image()
        val bytes = byteArrayOf(9, 8, 7)
        repository.photo = preparedPhoto(preview, bytes)
        val viewModel = viewModel()
        viewModel.onPhotoPicked("content://x")
        runCurrent()
        assertEquals(listOf("content://x"), repository.photoUris)
        val state = viewModel.ready()
        assertEquals(BackgroundChoice.Photo, state.background)
        assertNotNull(state.photoPreview)
        assertFalse(state.photoLoading)

        viewModel.onCreate()
        runCurrent()
        val background = repository.created.single().background as NewBackground.Photo
        assertArrayEquals(bytes, background.bytes)
        assertEquals("jpg", background.extension)
    }

    @Test
    fun photoPickerCancelled_keepsPrevious() = runTest {
        repository.photo = preparedPhoto(image())
        val viewModel = viewModel()
        viewModel.onSolidBackground(BackgroundColor.BLUE)
        assertEquals(BackgroundChoice.Solid(BackgroundColor.BLUE), viewModel.ready().background)
        viewModel.onPhotoPicked(null)
        runCurrent()
        assertEquals(BackgroundChoice.Solid(BackgroundColor.BLUE), viewModel.ready().background)
        assertEquals(emptyList<String>(), repository.photoUris)
    }

    @Test
    fun photoNotUsable_keepsPreviousAndTells() = runTest {
        repository.photo = null
        val viewModel = viewModel()
        viewModel.onSolidBackground(BackgroundColor.DARK)
        viewModel.onPhotoPicked("content://broken")
        runCurrent()
        val state = viewModel.ready()
        assertEquals(BackgroundChoice.Solid(BackgroundColor.DARK), state.background)
        assertFalse(state.photoLoading)
        assertNull(state.photoPreview)
        assertEquals(listOf<NewProjectEffect>(NewProjectEffect.ShowPhotoFailed), effects)
    }

    @Test
    fun colorPickedWhilePhotoLoading_dropsPhoto() = runTest {
        val gate = CompletableDeferred<PreparedPhoto?>()
        repository.photoGate = gate
        val viewModel = viewModel()
        viewModel.onPhotoPicked("content://x")
        runCurrent()
        assertTrue("photo is being prepared", viewModel.ready().photoLoading)

        viewModel.onSolidBackground(BackgroundColor.BLUE)
        assertFalse(viewModel.ready().photoLoading)
        assertEquals(BackgroundChoice.Solid(BackgroundColor.BLUE), viewModel.ready().background)

        gate.complete(preparedPhoto(image()))
        runCurrent()
        assertEquals(BackgroundChoice.Solid(BackgroundColor.BLUE), viewModel.ready().background)
        assertNull(viewModel.ready().photoPreview)

        viewModel.onCreate()
        runCurrent()
        assertEquals(NewBackground.Solid(BackgroundColor.BLUE), repository.created.single().background)
    }

    @Test
    fun firstProject_interstitialNotAllowed() = runTest {
        repository.count = 0
        val first = viewModel()
        first.onCreate()
        runCurrent()
        assertEquals(listOf(false), createdEffects().map { it.interstitialAllowed })

        effects.clear()
        repository.count = 1
        val second = viewModel()
        second.onCreate()
        runCurrent()
        assertEquals(listOf(true), createdEffects().map { it.interstitialAllowed })

        // Read when tapping, not when opening: 0 at open, 3 at the tap.
        effects.clear()
        repository.count = 0
        val third = viewModel()
        repository.count = 3
        third.onCreate()
        runCurrent()
        assertEquals(listOf(true), createdEffects().map { it.interstitialAllowed })
    }

    @Test
    fun create_logsProjectCreate_defaults() = runTest {
        val viewModel = viewModel()
        viewModel.onCreate()
        runCurrent()
        assertEquals(
            listOf(
                AnalyticsEvent(
                    "project_create",
                    mapOf(
                        "ratio" to "9:16",
                        "size" to "1080p",
                        "fps" to "12",
                        "background" to "white",
                        "is_default" to "true",
                    ),
                ),
            ),
            analytics.events,
        )
    }

    @Test
    fun create_logsProjectCreate_changed() = runTest {
        repository.photo = preparedPhoto(image())
        val viewModel = viewModel()
        viewModel.onNameChange(SECRET_NAME)
        viewModel.onAspectRatio(AspectRatio.LANDSCAPE_16_9)
        viewModel.onResolution(Resolution.P720)
        viewModel.onFrameRate(FrameRate.FPS_30)
        viewModel.onPhotoPicked("content://x")
        runCurrent()
        viewModel.onCreate()
        runCurrent()
        val event = createEvents().single()
        assertEquals(
            mapOf(
                "ratio" to "16:9",
                "size" to "720p",
                "fps" to "30",
                "background" to "photo",
                "is_default" to "false",
            ),
            event.params,
        )
        assertNoName(event)
    }

    @Test
    fun colors_logTheirNames() = runTest {
        // The other two colors of the background parameter (spec section 6).
        val dark = viewModel()
        dark.onSolidBackground(BackgroundColor.DARK)
        dark.onCreate()
        runCurrent()
        val blue = viewModel()
        blue.onSolidBackground(BackgroundColor.BLUE)
        blue.onCreate()
        runCurrent()
        assertEquals(listOf("dark", "blue"), createEvents().map { it.params["background"] })
        assertEquals(listOf("false", "false"), createEvents().map { it.params["is_default"] })
    }

    @Test
    fun changedThenBack_isDefaultTrue() = runTest {
        val viewModel = viewModel()
        viewModel.onAspectRatio(AspectRatio.SQUARE_1_1)
        viewModel.onResolution(Resolution.P720)
        viewModel.onFrameRate(FrameRate.FPS_8)
        viewModel.onSolidBackground(BackgroundColor.BLUE)
        viewModel.onAspectRatio(AspectRatio.PORTRAIT_9_16)
        viewModel.onResolution(Resolution.P1080)
        viewModel.onFrameRate(FrameRate.FPS_12)
        viewModel.onSolidBackground(BackgroundColor.WHITE)
        viewModel.onCreate()
        runCurrent()
        assertEquals("true", createEvents().single().params["is_default"])
    }

    @Test
    fun nameChangedOnly_isDefaultTrue() = runTest {
        val viewModel = viewModel()
        viewModel.onNameChange(SECRET_NAME)
        viewModel.onCreate()
        runCurrent()
        val event = createEvents().single()
        assertEquals("true", event.params["is_default"])
        assertNoName(event)
    }

    @Test
    fun createFailed_logsNothing() = runTest {
        repository.createResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        val viewModel = viewModel()
        viewModel.onNameChange(SECRET_NAME)
        viewModel.onCreate()
        runCurrent()
        assertEquals(1, repository.created.size)
        assertEquals(emptyList<AnalyticsEvent>(), analytics.events)
        assertTrue(logger.entries.none { SECRET_NAME in it.message })
    }

    @Test
    fun noSpace_keepsChoicesAndShowsError() = runTest {
        repository.createResult = ProjectResult.Failure(ProjectFailure.NO_SPACE)
        val viewModel = viewModel()
        viewModel.onAspectRatio(AspectRatio.LANDSCAPE_16_9)
        viewModel.onResolution(Resolution.P720)
        viewModel.onCreate()
        runCurrent()
        val state = viewModel.ready()
        assertEquals(NewProjectError.NO_SPACE, state.error)
        assertFalse(state.creating)
        assertEquals(AspectRatio.LANDSCAPE_16_9, state.aspectRatio)
        assertEquals(Resolution.P720, state.resolution)
        assertEquals(emptyList<NewProjectEffect.ProjectCreated>(), createdEffects())
        assertEquals(emptyList<AnalyticsEvent>(), analytics.events)

        viewModel.onErrorShown()
        assertNull(viewModel.ready().error)
        viewModel.onCreate()
        runCurrent()
        assertEquals(2, repository.created.size)
    }

    @Test
    fun writeFailed_showsGenericError() = runTest {
        repository.createResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        val viewModel = viewModel()
        viewModel.onCreate()
        runCurrent()
        assertEquals(NewProjectError.WRITE_FAILED, viewModel.ready().error)
        assertFalse(viewModel.ready().creating)
        assertTrue(logger.entries.any { it.message == "Create failed: WRITE_FAILED" })
    }

    @Test
    fun doubleCreate_createsOnce() = runTest {
        val gate = CompletableDeferred<Unit>()
        repository.createGate = gate
        val viewModel = viewModel()
        viewModel.onCreate()
        runCurrent()
        assertTrue(viewModel.ready().creating)
        viewModel.onCreate()
        runCurrent()
        gate.complete(Unit)
        runCurrent()
        assertEquals(1, repository.created.size)
        assertEquals(1, createdEffects().size)
        assertEquals(1, createEvents().size)
        // Created: the screen leaves; a later tap creates nothing more.
        assertTrue(viewModel.ready().creating)
        viewModel.onCreate()
        runCurrent()
        assertEquals(1, repository.created.size)
    }

    @Test
    fun createAgainAfterFailure_createsTwice() = runTest {
        // Control of doubleCreate_createsOnce: after a failure, a second tap writes again.
        repository.createResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        val viewModel = viewModel()
        viewModel.onCreate()
        runCurrent()
        viewModel.onCreate()
        runCurrent()
        assertEquals(2, repository.created.size)
    }

    @Test
    fun editorOff_comingSoonNoCreate() = runTest {
        flags[FeatureFlag.EDITOR] = false
        val viewModel = viewModel()
        viewModel.onCreate()
        runCurrent()
        assertEquals(0, repository.created.size)
        assertEquals(listOf<NewProjectEffect>(NewProjectEffect.ShowComingSoon), effects)
        assertEquals(emptyList<AnalyticsEvent>(), analytics.events)
        assertFalse(viewModel.ready().creating)

        // The gate opens again with the flag.
        flags[FeatureFlag.EDITOR] = true
        runCurrent()
        viewModel.onCreate()
        runCurrent()
        assertEquals(1, repository.created.size)
    }

    @Test
    fun editorTurnsOffAfterOpen_blocks() = runTest {
        val viewModel = viewModel()
        flags[FeatureFlag.EDITOR] = false
        runCurrent()
        viewModel.onCreate()
        runCurrent()
        assertEquals(0, repository.created.size)
        assertEquals(listOf<NewProjectEffect>(NewProjectEffect.ShowComingSoon), effects)
    }

    @Test
    fun projectsFlagOff_closes() = runTest {
        viewModel()
        // Control: nothing while the flag is on.
        assertEquals(emptyList<NewProjectEffect>(), effects)
        flags[FeatureFlag.PROJECTS] = false
        runCurrent()
        assertEquals(listOf<NewProjectEffect>(NewProjectEffect.Close), effects)
        flags[FeatureFlag.PROJECTS] = true
        runCurrent()
        flags[FeatureFlag.PROJECTS] = false
        runCurrent()
        assertEquals(listOf<NewProjectEffect>(NewProjectEffect.Close), effects)
    }

    private fun assertNoName(event: AnalyticsEvent) {
        assertTrue(event.params.toString(), event.params.values.none { SECRET_NAME in it })
        assertTrue(event.params.toString(), event.params.keys.none { it == "name" })
    }

    private companion object {
        const val SECRET_NAME = "Secret name"
    }
}
