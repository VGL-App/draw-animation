package com.vgl.draw.animation.feature.export.encode

import java.io.ByteArrayOutputStream
import kotlin.random.Random
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every GIF written here is decoded by the JDK's reader ([GifTestReader]), not by code of the encoder. */
class GifEncoderTest {
    @Test
    fun threeFrames_roundTripThroughImageIo() {
        val bytes = encode(64, 48, transparent = false) {
            addFrame(IntArray(64 * 48) { RED }, 8)
            addFrame(IntArray(64 * 48) { GREEN }, 25)
            addFrame(IntArray(64 * 48) { BLUE }, 9)
        }

        val gif = GifTestReader.read(bytes)

        assertEquals("GIF89a", String(bytes, 0, 6, Charsets.US_ASCII))
        assertEquals(0x3B, bytes.last().toInt() and 0xFF)
        assertEquals(3, gif.frames.size)
        assertEquals(listOf(8, 25, 9), gif.frames.map { it.delayTime })
        assertEquals(listOf(RED, GREEN, BLUE), gif.frames.map { it.argb(5, 5) })
        assertEquals(64, gif.frames[0].image.width)
        assertEquals(48, gif.frames[0].image.height)
        assertEquals(0, gif.loopCount)
    }

    @Test
    fun noiseFrame_exactPalette_everyPixelRoundTrips() {
        val random = Random(44)
        val colours = IntArray(200) { (0xFF shl 24) or (it shl 16) or random.nextInt(0, 0x10000) }
        val argb = IntArray(300 * 200) { colours[random.nextInt(colours.size)] }

        val image = GifTestReader.read(encode(300, 200, transparent = false) { addFrame(argb, 10) }).frames.single()

        for (y in 0 until 200) {
            for (x in 0 until 300) {
                val expected = argb[y * 300 + x]
                val actual = image.argb(x, y)
                if (expected != actual) {
                    throw AssertionError("pixel ($x, $y): expected ${hex(expected)}, got ${hex(actual)}")
                }
            }
        }
    }

    @Test
    fun largeUniformFrame_roundTrips() {
        val width = 1080
        val height = 1920
        val bytes = encode(width, height, transparent = false) { addFrame(IntArray(width * height) { BLUE }, 8) }

        val image = GifTestReader.read(bytes).frames.single().image

        assertEquals(width, image.width)
        assertEquals(height, image.height)
        val row = IntArray(width)
        for (y in 0 until height) {
            image.getRGB(0, y, width, 1, row, 0, width)
            assertTrue("row $y", row.all { it == BLUE })
        }
    }

    @Test
    fun onePixelFrame_roundTrips() {
        val gif = GifTestReader.read(encode(1, 1, transparent = false) { addFrame(intArrayOf(YELLOW), 5) })

        assertEquals(1, gif.frames.size)
        assertEquals(YELLOW, gif.frames[0].argb(0, 0))
        assertEquals(5, gif.frames[0].delayTime)
    }

    @Test
    fun addFrame_rejectsWrongSizeOrDelay() {
        val encoder = GifEncoder(ByteArrayOutputStream(), 4, 4, transparent = false)

        assertThrows(IllegalArgumentException::class.java) { encoder.addFrame(IntArray(15) { RED }, 8) }
        assertThrows(IllegalArgumentException::class.java) { encoder.addFrame(IntArray(16) { RED }, -1) }
        assertThrows(IllegalArgumentException::class.java) { encoder.addFrame(IntArray(16) { RED }, 65_536) }
    }

    @Test
    fun endCode_usesDecoderWidth() {
        // 255 distinct indices: 254 table entries are added, so the encoder's next code ends at 258 + 254 = 512.
        assertEquals(10, endCodeWidth(distinctPixels(255)))
        // Control: one index fewer, the next code ends at 511 and the end code stays 9 bits wide.
        assertEquals(9, endCodeWidth(distinctPixels(254)))
    }

    @Test
    fun transparent_alphaThresholdAndDisposal() {
        val pixels = intArrayOf(0x7F336699, 0x80336699.toInt(), 0x00000000, 0xFF112233.toInt())

        val transparent = GifTestReader.read(encode(4, 1, transparent = true) { addFrame(pixels, 8) }).frames[0]

        assertEquals(0, alpha(transparent.argb(0, 0)))
        assertEquals(0xFF336699.toInt(), transparent.argb(1, 0))
        assertEquals(0, alpha(transparent.argb(2, 0)))
        assertEquals(0xFF112233.toInt(), transparent.argb(3, 0))
        assertTrue(transparent.transparentColorFlag)
        assertEquals("restoreToBackgroundColor", transparent.disposalMethod)

        val opaque = GifTestReader.read(encode(4, 1, transparent = false) { addFrame(pixels, 8) }).frames[0]

        assertFalse(opaque.transparentColorFlag)
        assertEquals("doNotDispose", opaque.disposalMethod)
        assertArrayEquals(IntArray(4) { 255 }, IntArray(4) { alpha(opaque.argb(it, 0)) })
    }

    private fun encode(width: Int, height: Int, transparent: Boolean, frames: GifEncoder.() -> Unit): ByteArray {
        val out = ByteArrayOutputStream()
        GifEncoder(out, width, height, transparent).apply {
            frames()
            finish()
        }
        return out.toByteArray()
    }

    private fun distinctPixels(count: Int): IntArray = IntArray(count) { (0xFF shl 24) or (it shl 8) or 0x40 }

    /** Encodes a [pixels.size] × 1 frame and returns the code width at which the LZW end code was read. */
    private fun endCodeWidth(pixels: IntArray): Int {
        val bytes = encode(pixels.size, 1, transparent = false) { addFrame(pixels, 8) }
        val (minCodeSize, data) = firstImageData(bytes)
        assertEquals(8, minCodeSize)
        return GifLzwCodeReader(data, minCodeSize).endCodeWidth()
    }

    private fun hex(argb: Int): String = "#%08X".format(argb)
}

/** Finds the LZW minimum code size and the joined data sub-blocks of the first image, per the GIF89a spec. */
private fun firstImageData(bytes: ByteArray): Pair<Int, ByteArray> {
    fun u8(i: Int) = bytes[i].toInt() and 0xFF
    var pos = 6
    val screenFlags = u8(pos + 4)
    pos += 7
    if (screenFlags and 0x80 != 0) pos += 3 * (1 shl ((screenFlags and 7) + 1))
    while (true) {
        when (u8(pos)) {
            0x21 -> {
                pos += 2
                while (u8(pos) != 0) pos += u8(pos) + 1
                pos += 1
            }
            0x2C -> {
                val imageFlags = u8(pos + 9)
                pos += 10
                if (imageFlags and 0x80 != 0) pos += 3 * (1 shl ((imageFlags and 7) + 1))
                val minCodeSize = u8(pos)
                pos += 1
                val data = ByteArrayOutputStream()
                while (u8(pos) != 0) {
                    data.write(bytes, pos + 1, u8(pos))
                    pos += u8(pos) + 1
                }
                return minCodeSize to data.toByteArray()
            }
            else -> throw AssertionError("Unexpected block 0x%02X at $pos".format(u8(pos)))
        }
    }
}

/** Reads LZW codes least significant bit first, growing the code width the way a GIF decoder does. */
private class GifLzwCodeReader(private val data: ByteArray, private val minCodeSize: Int) {
    private var bitPos = 0

    private fun read(width: Int): Int {
        var code = 0
        for (i in 0 until width) {
            val byte = data[(bitPos + i) / 8].toInt() and 0xFF
            code = code or (((byte shr ((bitPos + i) % 8)) and 1) shl i)
        }
        bitPos += width
        return code
    }

    fun endCodeWidth(): Int {
        val clear = 1 shl minCodeSize
        val end = clear + 1
        var width = minCodeSize + 1
        var tableSize = clear + 2
        var first = true
        while (bitPos + width <= data.size * 8) {
            val code = read(width)
            when {
                code == clear -> {
                    width = minCodeSize + 1
                    tableSize = clear + 2
                    first = true
                }
                code == end -> return width
                else -> {
                    // The decoder adds one entry for every code after the first one following a clear code.
                    if (!first) tableSize++
                    first = false
                    if (tableSize == (1 shl width) && width < 12) width++
                }
            }
        }
        throw AssertionError("No end code in the LZW data")
    }
}
