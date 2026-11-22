package com.vgl.draw.animation.core.designsystem

import com.vgl.draw.animation.core.designsystem.theme.ProjectBackgroundColors
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The project background colors approved with plan T-004 (question 1). They are part of exported files, so there is
 * one value per color, not a light and a dark one.
 */
class ProjectBackgroundColorsTest {
    @Test
    fun values_matchApprovedColors() {
        assertEquals(0xFFFFFFFF.toInt(), ProjectBackgroundColors.WHITE)
        assertEquals(0xFF22262E.toInt(), ProjectBackgroundColors.DARK)
        assertEquals(0xFF2F6FE0.toInt(), ProjectBackgroundColors.BLUE)
    }
}
