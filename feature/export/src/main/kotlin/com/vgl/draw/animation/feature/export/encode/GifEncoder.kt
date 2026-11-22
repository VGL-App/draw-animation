package com.vgl.draw.animation.feature.export.encode

import java.io.OutputStream

/**
 * Writes an animated GIF89a that loops forever. Every frame covers the whole image and has its own 256-color palette,
 * since an animation's colors change with the strokes. In [transparent] mode, pixels with alpha below 128 are fully
 * transparent and each frame is cleared before the next one, so a frame never shows through the next.
 * The caller opens and closes [out].
 */
class GifEncoder(
    private val out: OutputStream,
    private val width: Int,
    private val height: Int,
    private val transparent: Boolean,
) {
    init {
        require(width in 1..MAX_SIDE && height in 1..MAX_SIDE) { "Unsupported GIF size" }
        out.write("GIF89a".toByteArray(Charsets.US_ASCII))
        // Logical screen descriptor: no global color table, 8-bit color resolution.
        writeShort(width)
        writeShort(height)
        out.write(0x70)
        out.write(0)
        out.write(0)
        // NETSCAPE2.0 application extension: loop count 0, forever.
        out.write(0x21)
        out.write(0xFF)
        out.write(11)
        out.write("NETSCAPE2.0".toByteArray(Charsets.US_ASCII))
        out.write(3)
        out.write(1)
        writeShort(0)
        out.write(0)
    }

    /** Adds a frame of non-premultiplied ARGB pixels, shown for [delayCentiseconds] hundredths of a second. */
    fun addFrame(argb: IntArray, delayCentiseconds: Int) {
        require(argb.size == width * height) { "Frame has ${argb.size} pixels, expected ${width * height}" }
        require(delayCentiseconds in 0..MAX_SHORT) { "Delay out of range" }
        val frame = ColorQuantizer.quantize(argb, transparent)

        // Graphic control extension.
        out.write(0x21)
        out.write(0xF9)
        out.write(4)
        val disposal = if (transparent) DISPOSE_TO_BACKGROUND else DISPOSE_KEEP
        out.write((disposal shl 2) or (if (transparent) 1 else 0))
        writeShort(delayCentiseconds)
        out.write(if (transparent) ColorQuantizer.TRANSPARENT_INDEX else 0)
        out.write(0)

        // Image descriptor with a local color table of 256 entries.
        out.write(0x2C)
        writeShort(0)
        writeShort(0)
        writeShort(width)
        writeShort(height)
        out.write(0x80 or 7)
        val table = ByteArray(ColorQuantizer.PALETTE_SIZE * 3)
        frame.palette.forEachIndexed { i, rgb ->
            table[i * 3] = (rgb shr 16).toByte()
            table[i * 3 + 1] = (rgb shr 8).toByte()
            table[i * 3 + 2] = rgb.toByte()
        }
        out.write(table)

        out.write(LzwEncoder.MIN_CODE_SIZE)
        LzwEncoder.encode(frame.indices, out)
    }

    /** Writes the trailer. Call once, after the last frame. */
    fun finish() {
        out.write(0x3B)
        out.flush()
    }

    private fun writeShort(value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
    }

    private companion object {
        const val MAX_SHORT = 65_535
        const val MAX_SIDE = MAX_SHORT
        const val DISPOSE_KEEP = 1
        const val DISPOSE_TO_BACKGROUND = 2
    }
}
