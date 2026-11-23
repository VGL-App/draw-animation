package com.vgl.draw.animation.navigation

import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import com.vgl.draw.animation.feature.loading.navigation.LoadingDestination
import com.vgl.draw.animation.feature.projects.navigation.ProjectsDestination
import com.vgl.draw.animation.feature.sample.navigation.SampleDestination
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationBarTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsNavigationBar_onlyWhenLessonsEnabledOnTopLevelRoute() {
        val projects = ProjectsDestination::class.qualifiedName
        val loading = LoadingDestination::class.qualifiedName
        val sample = SampleDestination::class.qualifiedName
        val expected = mapOf(
            Triple(projects, true, "projects, lessons on") to true,
            Triple(loading, true, "loading, lessons on") to false,
            Triple(sample, true, "sample, lessons on") to false,
            Triple(null, true, "no entry, lessons on") to false,
            Triple(projects, false, "projects, lessons off") to false,
            Triple(loading, false, "loading, lessons off") to false,
            Triple(sample, false, "sample, lessons off") to false,
            Triple(null, false, "no entry, lessons off") to false,
        )
        expected.forEach { (case, shows) ->
            assertEquals(case.third, shows, showsNavigationBar(case.first, lessonsEnabled = case.second))
        }
    }

    @Test
    fun bar_showsSelectedTabAndReportsTaps() {
        val tapped = mutableListOf<TopLevelTab>()
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                AppNavigationBar(selected = TopLevelTab.Projects, onSelect = { tapped += it })
            }
        }
        composeRule.onNodeWithTag(APP_NAVIGATION_BAR_TAG).assertExists()
        composeRule.onNodeWithText("Projects").assertIsSelected()
        composeRule.onNodeWithText("Lessons").assertIsNotSelected()
        composeRule.onNodeWithText("Lessons").performClick()
        assertEquals(listOf(TopLevelTab.Lessons), tapped)
    }
}
