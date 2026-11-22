package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.designsystem.theme.ProjectBackgroundColors
import com.vgl.draw.animation.core.project.model.BackgroundColor
import org.junit.Assert.assertEquals
import org.junit.Test

class BackgroundPaletteTest {
    @Test
    fun default_usesDesignSystemColors() {
        val palette = BackgroundPalette.Default
        assertEquals(ProjectBackgroundColors.WHITE, palette.colorOf(BackgroundColor.WHITE))
        assertEquals(ProjectBackgroundColors.DARK, palette.colorOf(BackgroundColor.DARK))
        assertEquals(ProjectBackgroundColors.BLUE, palette.colorOf(BackgroundColor.BLUE))
        assertEquals(
            "The three background colors must differ",
            3,
            BackgroundColor.entries.map { palette.colorOf(it) }.toSet().size,
        )
    }
}
