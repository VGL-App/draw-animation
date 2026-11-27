package com.vgl.draw.animation.feature.projects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [ProjectsRoute] with a real [ProjectsViewModel] over fakes. A null callback means its destination does not exist
 * yet: the route shows "Coming soon" in a snackbar. The snackbar hides by itself, so the clock does not run on its own:
 * the test taps, moves the clock 500 ms and looks for the text (plan T-009, section 4).
 */
@RunWith(AndroidJUnit4::class)
class ProjectsRouteTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val repository = FakeProjectsRepository()
    private val flags = FakeFeatureFlags()
    private var disabledCalls = 0
    private val opened = mutableListOf<ProjectId>()

    /** [privacyOptionsButton] null: the route's own default slot. */
    private fun show(openProject: Boolean = false, privacyOptionsButton: (@Composable (Modifier) -> Unit)? = null) {
        val viewModel = ProjectsViewModel(
            repository = repository,
            featureFlags = flags,
            analytics = NoAnalytics(),
            logger = RecordingAppLogger(),
        )
        val onOpenProject: ((ProjectId) -> Unit)? = if (openProject) ({ opened += it }) else null
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                if (privacyOptionsButton == null) {
                    ProjectsRoute(
                        viewModel = viewModel,
                        onNewProject = null,
                        onOpenProject = onOpenProject,
                        onOpenLessons = null,
                        onOpenSettings = null,
                        onImport = null,
                        onFeatureDisabled = { disabledCalls++ },
                    )
                } else {
                    ProjectsRoute(
                        viewModel = viewModel,
                        onNewProject = null,
                        onOpenProject = onOpenProject,
                        onOpenLessons = null,
                        onOpenSettings = null,
                        onImport = null,
                        onFeatureDisabled = { disabledCalls++ },
                        privacyOptionsButton = privacyOptionsButton,
                    )
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false
    }

    private fun assertSnackbarAfterTap(text: String, tap: () -> Unit) {
        // Control: nothing shows before the tap.
        composeRule.onAllNodesWithText(text).assertCountEquals(0)
        tap()
        composeRule.mainClock.advanceTimeBy(SNACKBAR_WAIT_MILLIS)
        composeRule.onNodeWithText(text).assertExists()
    }

    @Test
    fun newAnimation_withoutDestination_showsComingSoon() {
        show()
        assertSnackbarAfterTap(COMING_SOON) { composeRule.onNodeWithTag(ProjectsTestTags.NEW).performClick() }
    }

    @Test
    fun lesson_withoutDestination_showsComingSoon() {
        show()
        assertSnackbarAfterTap(COMING_SOON) {
            composeRule.onNodeWithTag(ProjectsTestTags.LESSON).performScrollTo().performClick()
        }
    }

    @Test
    fun settings_withoutDestination_showsComingSoon() {
        show()
        assertSnackbarAfterTap(COMING_SOON) { composeRule.onNodeWithTag(ProjectsTestTags.SETTINGS).performClick() }
    }

    @Test
    fun import_withoutDestination_showsComingSoon() {
        show()
        assertSnackbarAfterTap(COMING_SOON) { composeRule.onNodeWithTag(ProjectsTestTags.IMPORT).performClick() }
    }

    @Test
    fun openProject_withoutDestination_showsComingSoon() {
        repository.projects.value = listOf(readableItem(1))
        show()
        assertSnackbarAfterTap(COMING_SOON) {
            composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(1))).performClick()
        }
    }

    @Test
    fun openProject_withDestination_opensIt() {
        repository.projects.value = listOf(readableItem(1))
        show(openProject = true)
        composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(1))).performClick()
        composeRule.mainClock.advanceTimeBy(SNACKBAR_WAIT_MILLIS)
        assertEquals(listOf(testProjectId(1)), opened)
        composeRule.onAllNodesWithText(COMING_SOON).assertCountEquals(0)
    }

    @Test
    fun unreadable_showsCannotOpen() {
        repository.projects.value = listOf(unreadableItem(2, name = "Broken"))
        show(openProject = true)
        assertSnackbarAfterTap("Can't open this project.") {
            composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(2))).performClick()
        }
        assertEquals(emptyList<ProjectId>(), opened)
    }

    @Test
    fun twoMessagesInARow_secondReplacesFirst() {
        repository.projects.value = listOf(unreadableItem(2, name = "Broken"), readableItem(3))
        show()
        assertSnackbarAfterTap(COMING_SOON) {
            composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(3))).performClick()
        }

        composeRule.mainClock.advanceTimeBy(SNACKBAR_WAIT_MILLIS)
        composeRule.onNodeWithTag(ProjectsTestTags.card(testProjectId(2))).performClick()
        composeRule.mainClock.advanceTimeBy(SNACKBAR_WAIT_MILLIS)
        composeRule.onNodeWithText("Can't open this project.").assertExists()
        composeRule.onAllNodesWithText(COMING_SOON).assertCountEquals(0)
    }

    @Test
    fun disabled_callsOnFeatureDisabledOnce() {
        show()
        composeRule.mainClock.autoAdvance = true
        // Control: the flag is on, nothing is called.
        composeRule.onNodeWithTag(ProjectsTestTags.NEW).assertExists()
        assertEquals(0, disabledCalls)

        flags[FeatureFlag.PROJECTS] = false
        composeRule.waitForIdle()
        assertEquals(1, disabledCalls)

        // Disabled a second time (the flag went on and off again): still one call.
        flags[FeatureFlag.PROJECTS] = true
        composeRule.waitForIdle()
        flags[FeatureFlag.PROJECTS] = false
        composeRule.waitForIdle()
        assertEquals(1, disabledCalls)
    }

    @Test
    fun settingsFlag_flipsBetweenSettingsAndPrivacy() {
        flags[FeatureFlag.SETTINGS] = false
        show(privacyOptionsButton = { FakePrivacyButton(modifier = it) })
        composeRule.mainClock.autoAdvance = true
        composeRule.onNodeWithTag(ProjectsTestTags.PRIVACY).assertExists()
        composeRule.onAllNodesWithTag(ProjectsTestTags.SETTINGS).assertCountEquals(0)

        flags[FeatureFlag.SETTINGS] = true
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.SETTINGS).assertExists()
        composeRule.onAllNodesWithTag(ProjectsTestTags.PRIVACY).assertCountEquals(0)

        flags[FeatureFlag.SETTINGS] = false
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ProjectsTestTags.PRIVACY).assertExists()
        composeRule.onAllNodesWithTag(ProjectsTestTags.SETTINGS).assertCountEquals(0)
    }

    // Guard: PrivacyOptionsButton, the default slot, draws nothing without an ads controller.
    @Test
    fun settingsFlagOff_defaultSlot_noButtonWithoutAds() {
        flags[FeatureFlag.SETTINGS] = false
        show()
        composeRule.onAllNodesWithText("Privacy settings").assertCountEquals(0)
        composeRule.onAllNodesWithTag(ProjectsTestTags.SETTINGS).assertCountEquals(0)
        composeRule.onNodeWithTag(ProjectsTestTags.IMPORT).assertExists()
    }

    private companion object {
        const val COMING_SOON = "Coming soon"
        const val SNACKBAR_WAIT_MILLIS = 500L
    }
}
