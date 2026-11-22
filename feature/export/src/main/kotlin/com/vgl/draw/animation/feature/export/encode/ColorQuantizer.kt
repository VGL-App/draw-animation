package com.vgl.draw.animation.feature.export.encode

/** A frame reduced to at most 256 colors: [palette] holds 256 RGB values, [indices] one palette index per pixel. */
internal class QuantizedFrame(val palette: IntArray, val indices: ByteArray)

/**
 * Reduces a frame to a GIF palette. Pixels are non-premultiplied ARGB, as `Bitmap.getPixels` gives them.
 *
 * In transparent mode a pixel with alpha below 128 takes [TRANSPARENT_INDEX] and every other pixel counts as opaque;
 * otherwise alpha is ignored. When the frame has no more colors than fit, the palette is exactly those colors
 * (drawings with solid strokes); otherwise median cut over a 5-5-5 bit histogram. No dithering, so flat areas stay
 * flat.
 */
internal object ColorQuantizer {
    const val PALETTE_SIZE: Int = 256
    const val TRANSPARENT_INDEX: Int = 255
    private const val ALPHA_THRESHOLD = 128

    fun quantize(argb: IntArray, transparent: Boolean): QuantizedFrame {
        val limit = if (transparent) PALETTE_SIZE - 1 else PALETTE_SIZE
        return exactPalette(argb, transparent, limit) ?: medianCut(argb, transparent, limit)
    }

    private fun isTransparent(pixel: Int, transparent: Boolean): Boolean =
        transparent && (pixel ushr 24) < ALPHA_THRESHOLD

    /** The frame's own colors, or null as soon as there are more than [limit]. */
    private fun exactPalette(argb: IntArray, transparent: Boolean, limit: Int): QuantizedFrame? {
        // Open addressing; a stored key always has bit 24 set, so 0 marks an empty slot.
        val keys = IntArray(EXACT_TABLE_SIZE)
        val values = IntArray(EXACT_TABLE_SIZE)
        val palette = IntArray(PALETTE_SIZE)
        val indices = ByteArray(argb.size)
        var count = 0
        var lastKey = 0
        var lastIndex = 0
        for (i in argb.indices) {
            val pixel = argb[i]
            if (isTransparent(pixel, transparent)) {
                indices[i] = TRANSPARENT_INDEX.toByte()
                continue
            }
            val key = (pixel and RGB_MASK) or KEY_FLAG
            if (key != lastKey) {
                var slot = (key * HASH_MULTIPLIER) ushr (32 - EXACT_TABLE_BITS)
                while (true) {
                    val stored = keys[slot]
                    if (stored == key) {
                        lastIndex = values[slot]
                        break
                    }
                    if (stored == 0) {
                        if (count == limit) return null
                        keys[slot] = key
                        values[slot] = count
                        palette[count] = pixel and RGB_MASK
                        lastIndex = count
                        count++
                        break
                    }
                    slot = (slot + 1) and (EXACT_TABLE_SIZE - 1)
                }
                lastKey = key
            }
            indices[i] = lastIndex.toByte()
        }
        return QuantizedFrame(palette, indices)
    }

    private fun medianCut(argb: IntArray, transparent: Boolean, limit: Int): QuantizedFrame {
        val histogram = Histogram()
        for (pixel in argb) {
            if (!isTransparent(pixel, transparent)) histogram.add(pixel)
        }
        val boxes = mutableListOf<Box>()
        Box(0, CELL_MAX, 0, CELL_MAX, 0, CELL_MAX).shrink(histogram)?.let { boxes += it }
        while (boxes.size < limit) {
            var widest = -1
            for (i in boxes.indices) {
                if (boxes[i].spread > 0 && (widest < 0 || boxes[i].spread > boxes[widest].spread)) widest = i
            }
            if (widest < 0) break
            val (first, second) = boxes[widest].split(histogram)
            boxes[widest] = first
            boxes += second
        }

        val palette = IntArray(PALETTE_SIZE)
        val cellToIndex = ByteArray(CELL_COUNT)
        boxes.forEachIndexed { index, box ->
            palette[index] = box.averageColor(histogram)
            box.forEachCell { cell -> cellToIndex[cell] = index.toByte() }
        }
        val indices = ByteArray(argb.size)
        for (i in argb.indices) {
            val pixel = argb[i]
            indices[i] =
                if (isTransparent(pixel, transparent)) TRANSPARENT_INDEX.toByte() else cellToIndex[cellOf(pixel)]
        }
        return QuantizedFrame(palette, indices)
    }

    /** Pixel count and the sums of the 8-bit channels of each 5-5-5 bit cell. */
    private class Histogram {
        val count = IntArray(CELL_COUNT)
        val sumR = LongArray(CELL_COUNT)
        val sumG = LongArray(CELL_COUNT)
        val sumB = LongArray(CELL_COUNT)

        fun add(pixel: Int) {
            val cell = cellOf(pixel)
            count[cell]++
            sumR[cell] += ((pixel shr 16) and 0xFF).toLong()
            sumG[cell] += ((pixel shr 8) and 0xFF).toLong()
            sumB[cell] += (pixel and 0xFF).toLong()
        }
    }

    /** Inclusive ranges of 5-bit cells on each channel. */
    private class Box(val r0: Int, val r1: Int, val g0: Int, val g1: Int, val b0: Int, val b1: Int) {
        val spread: Int = maxOf(r1 - r0, g1 - g0, b1 - b0)

        inline fun forEachCell(action: (Int) -> Unit) {
            for (r in r0..r1) {
                for (g in g0..g1) {
                    for (b in b0..b1) action(cell(r, g, b))
                }
            }
        }

        /** The smallest box holding every occupied cell of this one, or null when none is occupied. */
        fun shrink(histogram: Histogram): Box? {
            var minR = CELL_MAX + 1
            var maxR = -1
            var minG = CELL_MAX + 1
            var maxG = -1
            var minB = CELL_MAX + 1
            var maxB = -1
            for (r in r0..r1) {
                for (g in g0..g1) {
                    for (b in b0..b1) {
                        if (histogram.count[cell(r, g, b)] == 0) continue
                        minR = minOf(minR, r)
                        maxR = maxOf(maxR, r)
                        minG = minOf(minG, g)
                        maxG = maxOf(maxG, g)
                        minB = minOf(minB, b)
                        maxB = maxOf(maxB, b)
                    }
                }
            }
            return if (maxR < 0) null else Box(minR, maxR, minG, maxG, minB, maxB)
        }

        /** Splits at the weighted median of the widest channel; both halves hold pixels since the box is shrunk. */
        fun split(histogram: Histogram): Pair<Box, Box> {
            val channel = when (spread) {
                r1 - r0 -> 0
                g1 - g0 -> 1
                else -> 2
            }
            val lo = intArrayOf(r0, g0, b0)[channel]
            val hi = intArrayOf(r1, g1, b1)[channel]
            val slices = LongArray(CELL_MAX + 1)
            for (r in r0..r1) {
                for (g in g0..g1) {
                    for (b in b0..b1) {
                        val position = when (channel) {
                            0 -> r
                            1 -> g
                            else -> b
                        }
                        slices[position] += histogram.count[cell(r, g, b)].toLong()
                    }
                }
            }
            val total = slices.sum()
            var cut = lo
            var cumulative = slices[lo]
            while (cut < hi - 1 && cumulative * 2 < total) {
                cut++
                cumulative += slices[cut]
            }
            val first = when (channel) {
                0 -> Box(r0, cut, g0, g1, b0, b1)
                1 -> Box(r0, r1, g0, cut, b0, b1)
                else -> Box(r0, r1, g0, g1, b0, cut)
            }
            val second = when (channel) {
                0 -> Box(cut + 1, r1, g0, g1, b0, b1)
                1 -> Box(r0, r1, cut + 1, g1, b0, b1)
                else -> Box(r0, r1, g0, g1, cut + 1, b1)
            }
            return requireNotNull(first.shrink(histogram)) to requireNotNull(second.shrink(histogram))
        }

        /** Weighted average of the real colors in the box. */
        fun averageColor(histogram: Histogram): Int {
            var n = 0L
            var r = 0L
            var g = 0L
            var b = 0L
            forEachCell { cell ->
                n += histogram.count[cell]
                r += histogram.sumR[cell]
                g += histogram.sumG[cell]
                b += histogram.sumB[cell]
            }
            fun mean(sum: Long) = ((sum + n / 2) / n).toInt()
            return (mean(r) shl 16) or (mean(g) shl 8) or mean(b)
        }
    }

    private const val RGB_MASK = 0xFFFFFF
    private const val KEY_FLAG = 0x1000000
    private const val EXACT_TABLE_BITS = 10
    private const val EXACT_TABLE_SIZE = 1 shl EXACT_TABLE_BITS
    private const val HASH_MULTIPLIER = -0x61c88647
    private const val CELL_MAX = 31
    private const val CELL_COUNT = 32 * 32 * 32

    private fun cell(r: Int, g: Int, b: Int): Int = (r shl 10) or (g shl 5) or b

    private fun cellOf(pixel: Int): Int =
        (((pixel shr 19) and 0x1F) shl 10) or (((pixel shr 11) and 0x1F) shl 5) or ((pixel shr 3) and 0x1F)
}
