package com.vgl.draw.animation.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The bottom navigation bar of the app shell (design projects, BottomNav), Projects selected. */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")
class AppNavigationBarScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun light() {
        composeRule.setContent {
            AppTheme(darkTheme = false) { AppNavigationBar(selected = TopLevelTab.Projects, onSelect = {}) }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/AppNavigationBar_light.png")
    }

    @Test
    fun dark() {
        composeRule.setContent {
            AppTheme(darkTheme = true) { AppNavigationBar(selected = TopLevelTab.Projects, onSelect = {}) }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/AppNavigationBar_dark.png")
    }
}
