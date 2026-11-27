package com.vgl.draw.animation.feature.projects.create

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.FakeFeatureFlags
import com.vgl.draw.animation.feature.projects.NoAnalytics
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * [NewProjectRoute] with a real [NewProjectViewModel] over fakes, in a [ComponentActivity] so that the activity can be
 * stopped and built again. The view model comes from the activity's store, as `hiltViewModel()` does, so it survives
 * a recreation. The photo picker goes through a fake [ActivityResultRegistry]; the interstitial through a fake
 * `createAd` that counts its calls and keeps `onFinished`. [order] receives, in order: "hold" and "release" (the
 * fake ad suppression), "launch" (the registry), "prepare:<uri>" (the repository) and "log:<message>" (the view
 * model's log), so the tests read the order of the calls around the picker.
 */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")
class NewProjectRouteTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val order = mutableListOf<String>()
    private val repository = FakeNewProjectRepository().apply { order = this@NewProjectRouteTest.order }
    private val flags = FakeFeatureFlags()
    private val logger = OrderLogger(order)
    private val registry = FakeRegistry()
    private val registryOwner = object : ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry = registry
    }

    private var closeCalls = 0
    private val opened = mutableListOf<ProjectId>()
    private var openProject = true
    private var defaultCreateAd = false
    private var adCalls = 0
    private var heldFinish: (() -> Unit)? = null

    private val fakeCreateAd: @Composable (() -> Unit) -> () -> Unit = { onFinished ->
        val current = rememberUpdatedState(onFinished)
        remember {
            {
                adCalls++
                heldFinish = { current.value() }
            }
        }
    }

    @Composable
    private fun Content() {
        val viewModel = viewModel { NewProjectViewModel(repository, flags, NoAnalytics(), logger) }
        val onOpen: ((ProjectId) -> Unit)? = if (openProject) ({ opened += it }) else null
        CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
            AppTheme(darkTheme = false) {
                if (defaultCreateAd) {
                    NewProjectRoute(
                        viewModel = viewModel,
                        onClose = { closeCalls++ },
                        onOpenProject = onOpen,
                        onBeforePhotoPicker = { order += "hold" },
                        onPhotoPickerResult = { order += "release" },
                        newProjectAd = { Box(it) },
                    )
                } else {
                    NewProjectRoute(
                        viewModel = viewModel,
                        onClose = { closeCalls++ },
                        onOpenProject = onOpen,
                        createAd = fakeCreateAd,
                        onBeforePhotoPicker = { order += "hold" },
                        onPhotoPickerResult = { order += "release" },
                        newProjectAd = { Box(it) },
                    )
                }
            }
        }
    }

    /**
     * The one content lambda of the test, set on the activity at start and after each recreation, as the app sets one
     * content in `onCreate`. The keys of `rememberSaveable` follow where the content lambda is written: two
     * different lambdas (or the rule's `setContent`, which wraps the content) would not find the saved state.
     */
    private val content: @Composable () -> Unit = { Content() }

    private fun show() {
        composeRule.activityRule.scenario.onActivity { it.setContent(content = content) }
        composeRule.waitForIdle()
        order.clear()
    }

    /**
     * Builds the activity again, then sets the content again, as the app does in `onCreate`. [beforeContent] runs
     * after the old content left and before the new one is set.
     */
    private fun recreate(beforeContent: () -> Unit = {}) {
        composeRule.activityRule.scenario.recreate()
        beforeContent()
        composeRule.activityRule.scenario.onActivity { it.setContent(content = content) }
        composeRule.waitForIdle()
    }

    private fun tapCreate() {
        composeRule.onNodeWithTag(NewProjectTags.CREATE).performClick()
        composeRule.waitForIdle()
    }

    private fun tapPhoto() {
        composeRule.onNodeWithTag(NewProjectTags.BG_PHOTO).performScrollTo().performClick()
        composeRule.waitForIdle()
    }

    private fun back() {
        composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeRule.waitForIdle()
    }

    private fun hasText(text: String): Boolean = composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    /** The message shows a short time after [action]; the clock is stopped so it does not hide by itself. */
    private fun assertMessageAfter(text: String, action: () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        assertTrue("no message before", !hasText(text))
        action()
        composeRule.mainClock.advanceTimeBy(SNACKBAR_WAIT_MILLIS)
        assertTrue("message \"$text\" shown", hasText(text))
    }

    @Test
    fun photoNotUsable_showsMessage() {
        repository.photo = null
        registry.result = Uri.parse("content://broken")
        show()
        assertMessageAfter("Couldn't use this photo. Try another one.") {
            composeRule.onNodeWithTag(NewProjectTags.BG_PHOTO).performScrollTo().performClick()
        }
    }

    @Test
    fun photoButton_launchesPhotoPicker() {
        registry.result = Uri.parse("content://media/1")
        show()
        tapPhoto()
        assertEquals(1, registry.inputs.size)
        val request = registry.inputs.single() as PickVisualMediaRequest
        assertEquals(ActivityResultContracts.PickVisualMedia.ImageOnly, request.mediaType)
        val action = registry.intents.single().action
        assertTrue(
            "action $action",
            action == MediaStore.ACTION_PICK_IMAGES ||
                action == Intent.ACTION_OPEN_DOCUMENT ||
                action == ACTION_SYSTEM_FALLBACK_PICK_IMAGES,
        )
        assertTrue("no permission request: $action", action?.contains("PERMISSION") != true)
        assertEquals(listOf("content://media/1"), repository.photoUris)

        // Closing the picker gives the view model nothing to prepare.
        registry.result = null
        tapPhoto()
        assertEquals(2, registry.inputs.size)
        assertEquals(listOf("content://media/1"), repository.photoUris)
    }

    @Test
    fun photoPicker_holdsBeforeLaunchReleasesFirstInCallback() {
        registry.result = Uri.parse("content://media/2")
        repository.photo = preparedPhoto(mockk(relaxed = true))
        show()
        tapPhoto()
        assertEquals(
            listOf("hold", "launch", "release", "log:Photo picked", "prepare:content://media/2"),
            order.filterNot { it.startsWith("log:State") },
        )

        order.clear()
        registry.result = null
        tapPhoto()
        assertEquals(listOf("hold", "launch", "release", "log:Photo picker closed"), order)
    }

    @Test
    fun photoPickerLaunchFails_releasesHold() {
        registry.fail = true
        show()
        tapPhoto()
        assertEquals(listOf("hold", "launch", "release", "log:Photo picker unavailable"), order)
        assertEquals(emptyList<String>(), repository.photoUris)
        assertEquals(1, order.count { it == "log:Photo picker unavailable" })
    }

    @Test
    fun created_noEditorDestination_closes() {
        openProject = false
        repository.count = 0
        show()
        assertEquals(0, closeCalls)
        tapCreate()
        assertEquals(1, closeCalls)
        assertEquals(1, repository.created.size)
    }

    @Test
    fun created_opensProject() {
        repository.count = 0
        show()
        tapCreate()
        assertEquals(listOf(repository.createdId), opened)
        assertEquals(0, closeCalls)
    }

    @Test
    fun firstProject_noAd_opensAtOnce() {
        repository.count = 0
        show()
        tapCreate()
        assertEquals(0, adCalls)
        assertEquals(listOf(repository.createdId), opened)
    }

    @Test
    fun secondProject_showsAdThenOpens() {
        repository.count = 1
        show()
        tapCreate()
        assertEquals(1, adCalls)
        assertEquals(emptyList<ProjectId>(), opened)

        composeRule.runOnUiThread { heldFinish!!.invoke() }
        composeRule.waitForIdle()
        assertEquals(listOf(repository.createdId), opened)

        // A second onFinished opens nothing more.
        composeRule.runOnUiThread { heldFinish!!.invoke() }
        composeRule.waitForIdle()
        assertEquals(listOf(repository.createdId), opened)
    }

    @Test
    fun createFinishesInBackground_noAdOnReturn() {
        repository.count = 3
        val gate = CompletableDeferred<Unit>()
        repository.createGate = gate
        show()
        tapCreate()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        gate.complete(Unit)
        composeRule.waitForIdle()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeRule.waitForIdle()
        assertEquals(0, adCalls)
        assertEquals(listOf(repository.createdId), opened)
    }

    @Test
    fun defaultCreateAd_noController_opensAtOnce() {
        defaultCreateAd = true
        repository.count = 5
        show()
        tapCreate()
        assertEquals(listOf(repository.createdId), opened)
    }

    @Test
    fun noSpace_showsMessage() {
        repository.createResult = ProjectResult.Failure(ProjectFailure.NO_SPACE)
        show()
        assertMessageAfter("Not enough space on your phone. Free up some space and try again.") {
            composeRule.onNodeWithTag(NewProjectTags.CREATE).performClick()
        }
        assertEquals(emptyList<ProjectId>(), opened)
    }

    @Test
    fun writeFailed_showsMessage() {
        repository.createResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        show()
        assertMessageAfter("Couldn't create the project. Try again.") {
            composeRule.onNodeWithTag(NewProjectTags.CREATE).performClick()
        }
    }

    @Test
    fun backWhileCreating_ignored() {
        val gate = CompletableDeferred<Unit>()
        repository.createGate = gate
        show()
        tapCreate()
        back()
        assertEquals(0, closeCalls)
        composeRule.onNodeWithTag(NewProjectTags.CLOSE).assertIsNotEnabled()

        repository.createResult = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
        gate.complete(Unit)
        composeRule.waitForIdle()
        back()
        assertEquals(1, closeCalls)
    }

    @Test
    fun backAfterCreated_noSecondClose() {
        openProject = false
        repository.count = 0
        show()
        tapCreate()
        assertEquals(1, closeCalls)
        back()
        assertEquals(1, closeCalls)
    }

    @Test
    fun backBeforeCreate_closes() {
        // Control of the two tests above: Back on an idle screen closes it once.
        show()
        back()
        assertEquals(1, closeCalls)
        assertEquals(0, repository.created.size)
    }

    @Test
    fun recreatedWhileAdPending_opensOnce() {
        repository.count = 2
        show()
        tapCreate()
        assertEquals(1, adCalls)
        assertEquals(emptyList<ProjectId>(), opened)
        val oldFinish = heldFinish!!

        recreate()
        assertEquals(listOf(repository.createdId), opened)
        assertEquals(1, adCalls)

        composeRule.runOnUiThread { oldFinish() }
        composeRule.waitForIdle()
        assertEquals(listOf(repository.createdId), opened)
    }

    @Test
    fun recreatedWhileCreating_writtenAfterOldScreenLeft_opensOnce() {
        repository.count = 2
        val gate = CompletableDeferred<Unit>()
        repository.createGate = gate
        show()
        tapCreate()

        // The write ends after the old content left and before the new one is set: ProjectCreated waits in the
        // view model's channel for the new screen.
        recreate(
            beforeContent = {
                gate.complete(Unit)
                shadowOf(Looper.getMainLooper()).idle()
            },
        )
        assertEquals(1, repository.created.size)
        // No project was pending when the new screen started: it gets ProjectCreated while resumed, so create_inter
        // shows once (plan T-011, section 3.1), and nothing opens before the ad closed.
        assertEquals(1, adCalls)
        assertEquals(emptyList<ProjectId>(), opened)

        composeRule.runOnUiThread { heldFinish!!.invoke() }
        composeRule.waitForIdle()
        assertEquals(listOf(repository.createdId), opened)
        assertEquals(1, adCalls)
    }

    @Test
    fun editorOff_showsComingSoon() {
        flags[FeatureFlag.EDITOR] = false
        show()
        assertMessageAfter("Coming soon") { composeRule.onNodeWithTag(NewProjectTags.CREATE).performClick() }
        assertEquals(0, repository.created.size)
    }

    @Test
    fun projectsFlagOff_callsOnClose() {
        show()
        assertEquals(0, closeCalls)
        flags[FeatureFlag.PROJECTS] = false
        composeRule.waitForIdle()
        assertEquals(1, closeCalls)
    }

    @Test
    fun recreate_keepsChoices() {
        show()
        composeRule.onNodeWithTag(NewProjectTags.NAME_FIELD).performTextReplacement("Ball")
        composeRule.onNodeWithTag(NewProjectTags.RATIO_16_9).performClick()
        composeRule.onNodeWithTag(NewProjectTags.SIZE_720P).performClick()
        composeRule.onNodeWithTag(NewProjectTags.BG_BLUE).performScrollTo().performClick()
        composeRule.waitForIdle()

        recreate()
        composeRule.onNodeWithTag(NewProjectTags.RATIO_16_9).assertIsSelected()
        composeRule.onNodeWithTag(NewProjectTags.SIZE_720P).assertIsSelected()
        composeRule.onNodeWithTag(NewProjectTags.BG_BLUE).performScrollTo().assertIsSelected()
        composeRule.onNodeWithTag(NewProjectTags.NAME_FIELD).performScrollTo().assertTextContains("Ball")
    }

    /** Records each launch; answers at once with [result] (null: the picker was closed), or throws when [fail]. */
    private inner class FakeRegistry : ActivityResultRegistry() {
        val inputs = mutableListOf<Any?>()
        val intents = mutableListOf<Intent>()
        var result: Uri? = null
        var fail = false

        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?,
        ) {
            order += "launch"
            inputs += input
            intents += contract.createIntent(ApplicationProvider.getApplicationContext(), input)
            if (fail) throw ActivityNotFoundException("no picker")
            val uri = result
            if (uri != null) {
                dispatchResult(requestCode, Activity.RESULT_OK, Intent().setData(uri))
            } else {
                dispatchResult(requestCode, Activity.RESULT_CANCELED, null)
            }
        }
    }

    /** Puts each logged message in [order] as "log:<message>". */
    private class OrderLogger(private val order: MutableList<String>) : AppLogger {
        override fun debug(tag: String, message: String) = Unit

        override fun info(tag: String, message: String) {
            order += "log:$message"
        }

        override fun warn(tag: String, message: String, error: Throwable?) {
            order += "log:$message"
        }

        override fun error(tag: String, message: String, error: Throwable?) {
            order += "log:$message"
        }
    }

    private companion object {
        const val SNACKBAR_WAIT_MILLIS = 500L

        // The picker of Google Play services on devices without the system one (androidx.activity).
        const val ACTION_SYSTEM_FALLBACK_PICK_IMAGES = "androidx.activity.result.contract.action.PICK_IMAGES"
    }
}
