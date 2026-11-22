package com.vgl.draw.animation.feature.export.encode

import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorQuantizerTest {
    @Test
    fun exactPalette_upTo256Colours() {
        val argb = IntArray(1024) { distinctColour(it * 7 % 256) }

        val frame = ColorQuantizer.quantize(argb, transparent = false)

        assertEquals(256, argb.toSet().size)
        assertEquals(256, frame.palette.size)
        argb.forEachIndexed { i, colour ->
            assertEquals("pixel $i", colour and 0xFFFFFF, frame.palette[frame.indices[i].toInt() and 0xFF])
        }
    }

    @Test
    fun transparentMode_255ColoursExact_256Reduced() {
        val exact = IntArray(255) { distinctColour(it) }
        val exactFrame = ColorQuantizer.quantize(exact, transparent = true)
        exact.forEachIndexed { i, colour ->
            val index = exactFrame.indices[i].toInt() and 0xFF
            assertTrue("pixel $i uses the transparent index", index != ColorQuantizer.TRANSPARENT_INDEX)
            assertEquals("pixel $i", colour and 0xFFFFFF, exactFrame.palette[index])
        }

        val many = IntArray(256) { distinctColour(it) }
        val reduced = ColorQuantizer.quantize(many, transparent = true)
        val used = reduced.indices.map { it.toInt() and 0xFF }.toSet()
        assertFalse("an opaque pixel uses the transparent index", ColorQuantizer.TRANSPARENT_INDEX in used)
        assertTrue("at most 255 opaque colours, got ${used.size}", used.size <= 255)
    }

    @Test
    fun manyColours_errorWithinTolerance() {
        val argb = IntArray(64 * 64) { i ->
            val x = i % 64
            val y = i / 64
            (0xFF shl 24) or ((4 * x) shl 16) or ((4 * y) shl 8) or 128
        }
        assertEquals(4096, argb.toSet().size)

        val frame = ColorQuantizer.quantize(argb, transparent = false)

        var worst = 0
        argb.forEachIndexed { i, colour ->
            val mapped = frame.palette[frame.indices[i].toInt() and 0xFF]
            worst = maxOf(
                worst,
                abs(red(colour) - red(mapped)),
                abs(green(colour) - green(mapped)),
                abs(blue(colour) - blue(mapped)),
            )
        }
        assertTrue("largest channel error $worst", worst <= 16)
    }

    @Test
    fun alphaIgnoredInOpaqueMode() {
        val argb = intArrayOf(0x00123456, 0xFF654321.toInt())

        val frame = ColorQuantizer.quantize(argb, transparent = false)

        assertEquals(0x123456, frame.palette[frame.indices[0].toInt() and 0xFF])
        assertEquals(0x654321, frame.palette[frame.indices[1].toInt() and 0xFF])
    }

    private fun distinctColour(i: Int): Int = (0xFF shl 24) or (((i * 37) and 0xFF) shl 16) or (i shl 8) or (255 - i)
}
