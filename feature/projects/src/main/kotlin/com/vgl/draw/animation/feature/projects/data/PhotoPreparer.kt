package com.vgl.draw.animation.feature.projects.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import com.vgl.draw.animation.core.logging.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Turns a photo picked for the background into JPEG bytes ready to be copied into a project (spec projects, KB-3):
 * turned upright by its EXIF orientation, scaled down to at most [PHOTO_MAX_SIDE] pixels on the long side, transparent
 * areas white. Blocking: the caller runs it on an IO dispatcher. Never logs the uri or the file name.
 */
internal class PhotoPreparer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AppLogger,
) {
    /** Null when the photo cannot be opened, is not an image the device can decode, or does not fit in memory. */
    fun prepare(uri: String): PreparedPhoto? {
        val source = Uri.parse(uri)
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            open(source).use { BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return notUsable("not an image")
            val orientation = orientationOrDefault(logger) {
                open(source).use {
                    ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                }
            }
            val options = BitmapFactory.Options().apply {
                inSampleSize = photoSampleSize(bounds.outWidth, bounds.outHeight)
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val decoded = open(source).use { BitmapFactory.decodeStream(it, null, options) }
                ?: return notUsable("not decoded")
            val upright = try {
                draw(decoded, exifTransform(orientation))
            } finally {
                decoded.recycle()
            }
            try {
                val bytes = ByteArrayOutputStream().also {
                    upright.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it)
                }.toByteArray()
                PreparedPhoto(bytes = bytes, extension = PHOTO_EXTENSION, preview = preview(upright).asImageBitmap())
            } finally {
                upright.recycle()
            }
        } catch (e: IOException) {
            notUsable(e.javaClass.simpleName)
        } catch (e: SecurityException) {
            notUsable(e.javaClass.simpleName)
        } catch (e: OutOfMemoryError) {
            notUsable(e.javaClass.simpleName)
        }
    }

    private fun open(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri) ?: throw FileNotFoundException("no stream")

    /** Mirrors, rotates and scales [source] onto a white bitmap of the target size. */
    private fun draw(source: Bitmap, transform: PhotoTransform): Bitmap {
        val matrix = Matrix()
        if (transform.mirrorHorizontally) matrix.postScale(-1f, 1f)
        matrix.postRotate(transform.rotationDegrees.toFloat())
        val turned = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat()).also { matrix.mapRect(it) }
        matrix.postTranslate(-turned.left, -turned.top)
        val (width, height) = photoTargetSize(turned.width().roundToInt(), turned.height().roundToInt())
        matrix.postScale(width / turned.width(), height / turned.height())
        val target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(target).apply {
            drawColor(Color.WHITE)
            drawBitmap(source, matrix, Paint(Paint.FILTER_BITMAP_FLAG))
        }
        return target
    }

    /** A square cut from the middle, [PREVIEW_DP] wide at the screen density, so the round button stays sharp. */
    private fun preview(photo: Bitmap): Bitmap {
        val side = (PREVIEW_DP * context.resources.displayMetrics.density).roundToInt().coerceAtLeast(1)
        val crop = min(photo.width, photo.height)
        val left = (photo.width - crop) / 2
        val top = (photo.height - crop) / 2
        val preview = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888)
        val matrix = Matrix().apply {
            postTranslate(-left.toFloat(), -top.toFloat())
            postScale(side / crop.toFloat(), side / crop.toFloat())
        }
        Canvas(preview).drawBitmap(photo, matrix, Paint(Paint.FILTER_BITMAP_FLAG))
        return preview
    }

    private fun notUsable(reason: String): PreparedPhoto? {
        logger.warn(LOG_TAG, "Photo not usable ($reason)")
        return null
    }
}

/** How to turn a decoded photo upright: mirror first (left to right), then rotate clockwise. */
internal data class PhotoTransform(val rotationDegrees: Int, val mirrorHorizontally: Boolean)

/** The smallest power of two that keeps the long side, once decoded, at most [DECODE_MAX_SIDE] (about 50 MB). */
internal fun photoSampleSize(width: Int, height: Int): Int {
    val long = max(width, height)
    var sample = 1
    while (long / sample > DECODE_MAX_SIDE) sample *= 2
    return sample
}

/** The size once turned upright: the long side brought down to exactly [PHOTO_MAX_SIDE], never scaled up. */
internal fun photoTargetSize(width: Int, height: Int): Pair<Int, Int> {
    val long = max(width, height)
    if (long <= PHOTO_MAX_SIDE) return width to height
    val scale = PHOTO_MAX_SIDE.toDouble() / long
    return if (width >= height) {
        PHOTO_MAX_SIDE to (height * scale).roundToInt().coerceAtLeast(1)
    } else {
        (width * scale).roundToInt().coerceAtLeast(1) to PHOTO_MAX_SIDE
    }
}

/** The eight EXIF orientations; any other value keeps the photo as it is. */
internal fun exifTransform(orientation: Int): PhotoTransform = when (orientation) {
    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> PhotoTransform(0, true)
    ExifInterface.ORIENTATION_ROTATE_180 -> PhotoTransform(180, false)
    ExifInterface.ORIENTATION_FLIP_VERTICAL -> PhotoTransform(180, true)
    ExifInterface.ORIENTATION_TRANSPOSE -> PhotoTransform(270, true)
    ExifInterface.ORIENTATION_ROTATE_90 -> PhotoTransform(90, false)
    ExifInterface.ORIENTATION_TRANSVERSE -> PhotoTransform(90, true)
    ExifInterface.ORIENTATION_ROTATE_270 -> PhotoTransform(270, false)
    else -> PhotoTransform(0, false)
}

/** [read]'s orientation; 1 (upright) when it cannot be read: the photo is still used, only not turned. */
internal fun orientationOrDefault(logger: AppLogger, read: () -> Int): Int = try {
    read()
} catch (e: IOException) {
    logger.warn(LOG_TAG, "Photo EXIF not readable (${e.javaClass.simpleName})")
    ExifInterface.ORIENTATION_NORMAL
} catch (e: RuntimeException) {
    logger.warn(LOG_TAG, "Photo EXIF not readable (${e.javaClass.simpleName})")
    ExifInterface.ORIENTATION_NORMAL
}

private const val LOG_TAG = "NewProject"
private const val DECODE_MAX_SIDE = 4096
private const val PHOTO_MAX_SIDE = 3840
private const val JPEG_QUALITY = 90
private const val PHOTO_EXTENSION = "jpg"
private const val PREVIEW_DP = 48
