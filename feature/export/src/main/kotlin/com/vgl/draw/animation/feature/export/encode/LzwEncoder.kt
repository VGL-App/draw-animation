package com.vgl.draw.animation.feature.export.encode

import java.io.OutputStream

/**
 * LZW compression in the GIF variant, with a minimum code size of 8 (256 palette indices). Writes the compressed
 * stream as data sub-blocks of at most 255 bytes, ending with the block terminator. The caller writes the minimum
 * code size byte before.
 */
internal object LzwEncoder {
    const val MIN_CODE_SIZE: Int = 8
    private const val CLEAR = 1 shl MIN_CODE_SIZE
    private const val END = CLEAR + 1
    private const val FIRST_FREE = CLEAR + 2
    private const val MAX_CODES = 4096
    private const val MAX_CODE_SIZE = 12
    private const val HASH_SIZE = 5003
    private const val EMPTY = -1

    fun encode(indices: ByteArray, out: OutputStream) {
        val writer = BlockBitWriter(out)
        val keys = IntArray(HASH_SIZE) { EMPTY }
        val codes = IntArray(HASH_SIZE)
        var codeSize = MIN_CODE_SIZE + 1
        var nextCode = FIRST_FREE
        writer.write(CLEAR, codeSize)
        if (indices.isEmpty()) {
            writer.write(END, codeSize)
            writer.finish()
            return
        }
        var prefix = indices[0].toInt() and 0xFF
        for (i in 1 until indices.size) {
            val index = indices[i].toInt() and 0xFF
            val key = (prefix shl 8) or index
            var slot = ((key * HASH_MULTIPLIER) ushr 1) % HASH_SIZE
            var found = EMPTY
            while (keys[slot] != EMPTY) {
                if (keys[slot] == key) {
                    found = codes[slot]
                    break
                }
                slot = if (slot == HASH_SIZE - 1) 0 else slot + 1
            }
            if (found != EMPTY) {
                prefix = found
                continue
            }
            writer.write(prefix, codeSize)
            if (nextCode < MAX_CODES) {
                keys[slot] = key
                codes[slot] = nextCode
                nextCode++
                // The decoder adds each entry one code later, so it widens when the next code passes 1 shl codeSize.
                if (nextCode == (1 shl codeSize) + 1 && codeSize < MAX_CODE_SIZE) codeSize++
            }
            if (nextCode == MAX_CODES) {
                writer.write(CLEAR, codeSize)
                keys.fill(EMPTY)
                codeSize = MIN_CODE_SIZE + 1
                nextCode = FIRST_FREE
            }
            prefix = index
        }
        writer.write(prefix, codeSize)
        // Reading that last code makes the decoder add one more entry, which may widen the end code.
        if (nextCode == (1 shl codeSize) && codeSize < MAX_CODE_SIZE) codeSize++
        writer.write(END, codeSize)
        writer.finish()
    }

    private const val HASH_MULTIPLIER = -0x61c88647

    /** Packs codes least significant bit first into sub-blocks of at most 255 bytes. */
    private class BlockBitWriter(private val out: OutputStream) {
        private val block = ByteArray(255)
        private var blockSize = 0
        private var bits = 0
        private var bitCount = 0

        fun write(code: Int, size: Int) {
            bits = bits or (code shl bitCount)
            bitCount += size
            while (bitCount >= 8) {
                putByte(bits and 0xFF)
                bits = bits ushr 8
                bitCount -= 8
            }
        }

        fun finish() {
            if (bitCount > 0) putByte(bits and 0xFF)
            bits = 0
            bitCount = 0
            flushBlock()
            out.write(0)
        }

        private fun putByte(value: Int) {
            block[blockSize++] = value.toByte()
            if (blockSize == block.size) flushBlock()
        }

        private fun flushBlock() {
            if (blockSize == 0) return
            out.write(blockSize)
            out.write(block, 0, blockSize)
            blockSize = 0
        }
    }
}
