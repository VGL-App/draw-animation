package com.vgl.draw.animation.feature.projects

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.ads.AdPlacement
import com.vgl.draw.animation.core.ads.InContentAds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** The in-content placements of the projects list (MC-13, plan T-010), one setContent per test. */
@RunWith(AndroidJUnit4::class)
class ProjectsInContentAdsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun inContentAds_areTheThreeOfTheSpec() {
        assertEquals(
            listOf(AdPlacement.PROJECTS_EMPTY_NATIVE, AdPlacement.PROJECTS_GRID, AdPlacement.RESTORE_MREC),
            projectsInContentAds(),
        )
        assertFalse(AdPlacement.PROJECTS_BOTTOM in projectsInContentAds())
    }

    @Test
    fun inContentAds_doNotThrow() {
        // Control: the list given to InContentAds is the screen's real list, not an empty one.
        assertTrue(projectsInContentAds().isNotEmpty())
        composeRule.setContent { InContentAds(projectsInContentAds()) { Text("x") } }
        composeRule.onNodeWithText("x").assertExists()
    }

    @Test
    fun inContentAds_rejectsInterstitial() {
        assertThrows(IllegalArgumentException::class.java) {
            composeRule.setContent { InContentAds(listOf(AdPlacement.CREATE_INTER)) {} }
        }
    }
}
