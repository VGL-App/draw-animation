package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.max

/**
 * Decodes a "Photo" background for a [width] × [height] frame. The drawing screen decodes with it too, so it shows the
 * background exactly like an export. The image is not rotated by EXIF: the New project screen stores it already upright
 * and scaled down.
 */
object BackgroundDecoder {
    /**
     * The image, decoded at the smallest power-of-two reduction that still covers the frame (a 4000 × 1000 image for a
     * 100 × 100 frame decodes to 500 × 125), or null when [bytes] are not an image.
     */
    fun decode(bytes: ByteArray, width: Int, height: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, width, height)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    /** Largest power of two such that the reduced image still covers the frame. */
    internal fun sampleSize(srcWidth: Int, srcHeight: Int, width: Int, height: Int): Int {
        val scale = max(width.toDouble() / srcWidth, height.toDouble() / srcHeight)
        var sample = 1
        while (sample * 2 * scale <= 1.0) sample *= 2
        return sample
    }
}
