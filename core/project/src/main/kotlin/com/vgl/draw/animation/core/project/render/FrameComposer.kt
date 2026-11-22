package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.Canvas
import com.vgl.draw.animation.core.project.model.CameraKey

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
        TODO()
    }

    /** Fills the canvas with [argb]. Not through the camera: a full fill looks the same under any camera. */
    internal fun drawSolid(canvas: Canvas, argb: Int) {
        TODO()
    }

    /** Fills with [gapArgb], then draws [photo] through [camera] covering the frame, cropped at the center. */
    internal fun drawPhoto(canvas: Canvas, width: Int, height: Int, photo: Bitmap, gapArgb: Int, camera: CameraKey) {
        TODO()
    }

    /** Draws [cell] through [camera] scaled to the whole frame (cells are stored at the project's canvas size). */
    internal fun drawLayer(canvas: Canvas, width: Int, height: Int, cell: Bitmap, camera: CameraKey) {
        TODO()
    }
}
