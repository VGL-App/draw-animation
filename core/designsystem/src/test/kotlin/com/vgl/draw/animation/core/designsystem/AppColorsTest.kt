package com.vgl.draw.animation.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.designsystem.theme.AppColors
import com.vgl.draw.animation.core.designsystem.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** AppTheme.colors gives the colors without a Material 3 role, per theme, as on the Figma file (ADR-0008). */
@RunWith(AndroidJUnit4::class)
class AppColorsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun lightTheme_givesLightCanvasAndOnionColors() {
        val colors = colorsIn(darkTheme = false)
        assertEquals(Color(0xFFFFFFFF), colors.canvas)
        assertEquals(Color(0xFF1A1C22), colors.onCanvas)
        assertEquals(Color(0xFFFF5A5F), colors.onionPrevious)
        assertEquals(Color(0xFF3D8BFF), colors.onionNext)
    }

    @Test
    fun darkTheme_keepsThePaperLight() {
        val colors = colorsIn(darkTheme = true)
        assertEquals(Color(0xFFFAFAF7), colors.canvas)
        assertEquals(Color(0xFF1A1C22), colors.onCanvas)
    }

    private fun colorsIn(darkTheme: Boolean): AppColors {
        var captured: AppColors? = null
        composeRule.setContent { AppTheme(darkTheme = darkTheme) { captured = AppTheme.colors } }
        composeRule.waitForIdle()
        return checkNotNull(captured)
    }
}
