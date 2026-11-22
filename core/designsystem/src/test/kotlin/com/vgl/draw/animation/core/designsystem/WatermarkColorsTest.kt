package com.vgl.draw.animation.core.designsystem

import com.vgl.draw.animation.core.designsystem.theme.WatermarkColors
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The Frameloop watermark approved in the export design (white content on a 35% black pill). It is part of exported
 * files, so there is one value per color, not a light and a dark one.
 */
class WatermarkColorsTest {
    @Test
    fun values() {
        assertEquals(0xFFFFFFFF.toInt(), WatermarkColors.CONTENT)
        assertEquals(0x59000000, WatermarkColors.PILL)
    }
}
