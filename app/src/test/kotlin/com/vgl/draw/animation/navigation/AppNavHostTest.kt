package com.vgl.draw.animation.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.feature.loading.navigation.LoadingDestination
import com.vgl.draw.animation.feature.projects.navigation.ProjectsDestination
import com.vgl.draw.animation.feature.sample.navigation.SampleDestination
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * How [AppNavHost] wires its destinations, with fake screens passed through [AppScreens]: the real routes call
 * `hiltViewModel()`, which the shell test does not set up. Only tags, texts and the counters of the test are read;
 * the content of each feature screen has its own tests in its module.
 */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class AppNavHostTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var loadingRuns = 0
    private var disabledCalls = 0
    private val projectsOff = mutableStateOf(false)
    private val lessonsEnabled = mutableStateOf(false)

    private val fakeScreens = AppScreens(
        loading = { onFinished ->
            composable<LoadingDestination> {
                LaunchedEffect(Unit) {
                    loadingRuns++
                    onFinished()
                }
            }
        },
        projects = { onFeatureDisabled ->
            composable<ProjectsDestination> {
                Box(Modifier.fillMaxSize().testTag(PROJECTS))
                if (projectsOff.value) {
                    LaunchedEffect(Unit) {
                        disabledCalls++
                        onFeatureDisabled()
                    }
                }
            }
        },
        sample = {
            composable<SampleDestination> { Box(Modifier.fillMaxSize().testTag(SAMPLE)) }
        },
    )

    private fun show() {
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                AppNavHost(lessonsEnabled = lessonsEnabled.value, screens = fakeScreens)
            }
        }
        composeRule.waitForIdle()
    }

    private fun exists(tag: String): Boolean = composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()

    private fun bounds(tag: String): Rect = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInWindow

    private fun windowBottom(): Float = composeRule.onRoot().fetchSemanticsNode().boundsInWindow.bottom

    private fun assertNear(message: String, expected: Float, actual: Float) {
        assertTrue("$message: expected $expected, was $actual", abs(expected - actual) <= 1f)
    }

    @Test
    fun loadingFinished_opensProjects() {
        show()
        assertTrue(exists(PROJECTS))
        assertFalse(exists(SAMPLE))
        assertEquals(1, loadingRuns)
    }

    @Test
    fun backOnProjects_finishesActivity() {
        show()
        // Control: the list is on screen before Back.
        assertTrue(exists(PROJECTS))
        assertFalse(composeRule.activity.isFinishing)
        composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeRule.waitForIdle()
        assertTrue(composeRule.activity.isFinishing)
    }

    @Test
    fun recreate_keepsProjectsScreen() {
        val restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            AppTheme(darkTheme = false) { AppNavHost(screens = fakeScreens) }
        }
        composeRule.waitForIdle()
        assertTrue(exists(PROJECTS))
        assertEquals(1, loadingRuns)

        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.waitForIdle()
        assertTrue(exists(PROJECTS))
        assertEquals(1, loadingRuns)
    }

    @Test
    fun projectsFlagOff_opensSample() {
        projectsOff.value = true
        show()
        assertTrue(exists(SAMPLE))
        assertFalse(exists(PROJECTS))
        assertEquals(1, disabledCalls)
    }

    @Test
    fun projectsFlagOnAfterDisabled_staysOnSample() {
        projectsOff.value = true
        show()
        assertEquals(1, disabledCalls)
        assertTrue(exists(SAMPLE))

        // The flag turns on later: the user stays on the sample screen until the next start (plan T-009, question 7).
        projectsOff.value = false
        composeRule.waitForIdle()
        assertTrue(exists(SAMPLE))
        assertFalse(exists(PROJECTS))
    }

    @Test
    fun lessonsEnabled_showsBarOnProjects() {
        lessonsEnabled.value = true
        show()
        assertTrue(exists(PROJECTS))
        assertTrue(exists(APP_NAVIGATION_BAR_TAG))

        lessonsEnabled.value = false
        composeRule.waitForIdle()
        assertFalse(exists(APP_NAVIGATION_BAR_TAG))

        // On the sample screen, no bar even with lessons on.
        lessonsEnabled.value = true
        projectsOff.value = true
        composeRule.waitForIdle()
        assertTrue(exists(SAMPLE))
        assertFalse(exists(APP_NAVIGATION_BAR_TAG))
    }

    @Test
    fun lessonsTab_showsComingSoon() {
        lessonsEnabled.value = true
        show()
        composeRule.mainClock.autoAdvance = false
        // Control: nothing shows before the tap.
        composeRule.onAllNodesWithText(COMING_SOON).assertCountEquals(0)
        composeRule.onNodeWithText("Lessons").performClick()
        composeRule.mainClock.advanceTimeBy(SNACKBAR_WAIT_MILLIS)
        composeRule.onNodeWithText(COMING_SOON).assertExists()
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun barShown_contentEndsAtBarTop() {
        lessonsEnabled.value = true
        show()
        dispatchNavigationBarInsets(composeRule, TEST_NAVIGATION_BAR_PX)
        val content = bounds(PROJECTS)
        val bar = bounds(APP_NAVIGATION_BAR_TAG)
        assertNear("content bottom at bar top", bar.top, content.bottom)
        assertNear("bar bottom at window bottom", windowBottom(), bar.bottom)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun barHidden_contentEndsAboveSystemBar() {
        // Control of barShown_contentEndsAtBarTop: without the bar the content keeps the system bar inset.
        lessonsEnabled.value = false
        show()
        dispatchNavigationBarInsets(composeRule, TEST_NAVIGATION_BAR_PX)
        assertFalse(exists(APP_NAVIGATION_BAR_TAG))
        assertNear("content bottom", windowBottom() - TEST_NAVIGATION_BAR_PX, bounds(PROJECTS).bottom)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun lessonsFlagTurnsOff_barDisappearsAndContentExtends() {
        lessonsEnabled.value = true
        show()
        dispatchNavigationBarInsets(composeRule, TEST_NAVIGATION_BAR_PX)
        // Control: before the change, the content ends at the bar.
        assertNear("content bottom at bar top", bounds(APP_NAVIGATION_BAR_TAG).top, bounds(PROJECTS).bottom)

        lessonsEnabled.value = false
        composeRule.waitForIdle()
        assertFalse(exists(APP_NAVIGATION_BAR_TAG))
        assertNear("content bottom", windowBottom() - TEST_NAVIGATION_BAR_PX, bounds(PROJECTS).bottom)
    }

    private companion object {
        const val PROJECTS = "projects_screen"
        const val SAMPLE = "sample_screen"
        const val COMING_SOON = "Coming soon"
        const val SNACKBAR_WAIT_MILLIS = 500L
    }
}
