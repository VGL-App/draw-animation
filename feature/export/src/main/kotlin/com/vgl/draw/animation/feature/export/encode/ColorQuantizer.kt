package com.vgl.draw.animation.feature.export.encode

internal class QuantizedFrame(val palette: IntArray, val indices: ByteArray)

internal object ColorQuantizer {
    const val TRANSPARENT_INDEX: Int = 255

    fun quantize(argb: IntArray, transparent: Boolean): QuantizedFrame = TODO()
}
