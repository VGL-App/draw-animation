package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import com.vgl.draw.animation.core.project.model.CameraKey
import kotlin.math.max

/** What is drawn under the layers of a frame. */
sealed interface FrameBackground {
    /** Fills the whole frame with [argb]. */
    data class Solid(val argb: Int) : FrameBackground

    /** [photo] covers the frame, cropped at the center, and moves with the camera; uncovered parts show [gapArgb]. */
    data class Photo(val photo: Bitmap, val gapArgb: Int) : FrameBackground
}

/**
 * Draws a frame from images already in memory. The drawing screen shows frames through [compose]; the renderer that
 * reads the project store draws through the same steps, one cell at a time, so both give the same pixels.
 */
object FrameComposer {
    /** Draws [background] (null: transparent) then [layers] bottom to top (null: empty cell) through [camera]. */
    fun compose(
        canvas: Canvas,
        width: Int,
        height: Int,
        background: FrameBackground?,
        layers: List<Bitmap?>,
        camera: CameraKey,
    ) {
        when (background) {
            is FrameBackground.Solid -> drawSolid(canvas, background.argb)
            is FrameBackground.Photo -> drawPhoto(canvas, width, height, background.photo, background.gapArgb, camera)
            null -> Unit
        }
        layers.forEach { cell -> if (cell != null) drawLayer(canvas, width, height, cell, camera) }
    }

    /** Fills the canvas with [argb]. Not through the camera: a full fill looks the same under any camera. */
    internal fun drawSolid(canvas: Canvas, argb: Int) {
        canvas.drawColor(argb)
    }

    /** Fills with [gapArgb], then draws [photo] through [camera] covering the frame, cropped at the center. */
    internal fun drawPhoto(canvas: Canvas, width: Int, height: Int, photo: Bitmap, gapArgb: Int, camera: CameraKey) {
        canvas.drawColor(gapArgb)
        val scale = max(width.toFloat() / photo.width, height.toFloat() / photo.height)
        val cover = Matrix().apply {
            postScale(scale, scale)
            postTranslate((width - photo.width * scale) / 2f, (height - photo.height * scale) / 2f)
        }
        withCamera(canvas, width, height, camera) {
            // The crop is the frame itself: parts of the photo outside it stay hidden when the camera zooms out.
            canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawBitmap(photo, cover, Paint(Paint.FILTER_BITMAP_FLAG))
        }
    }

    /** Draws [cell] through [camera] scaled to the whole frame (cells are stored at the project's canvas size). */
    internal fun drawLayer(canvas: Canvas, width: Int, height: Int, cell: Bitmap, camera: CameraKey) {
        withCamera(canvas, width, height, camera) {
            canvas.drawBitmap(
                cell,
                null,
                RectF(0f, 0f, width.toFloat(), height.toFloat()),
                Paint(Paint.FILTER_BITMAP_FLAG),
            )
        }
    }

    private inline fun withCamera(canvas: Canvas, width: Int, height: Int, camera: CameraKey, draw: () -> Unit) {
        val saved = canvas.save()
        try {
            canvas.concat(cameraMatrix(camera, width, height))
            draw()
        } finally {
            canvas.restoreToCount(saved)
        }
    }
}
