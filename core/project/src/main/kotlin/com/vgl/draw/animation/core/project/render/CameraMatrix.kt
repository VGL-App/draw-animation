package com.vgl.draw.animation.core.project.render

import android.graphics.Matrix
import com.vgl.draw.animation.core.project.model.CameraKey

/**
 * The transform of [key] on a [width] × [height] frame: zoom around the center, rotate around the center (positive is
 * clockwise on screen), then move by `panX * width`, `panY * height` pixels (positive is right and down).
 *
 * A key describes how the scene moves on the frame, not where a camera goes; the camera tool turns the user's
 * gestures into keys with this meaning. The move is a fraction of the output size, so a key gives the same layout at
 * 720p, 1080p and in a thumbnail.
 */
fun cameraMatrix(key: CameraKey, width: Int, height: Int): Matrix {
    val centerX = width / 2f
    val centerY = height / 2f
    return Matrix().apply {
        postScale(key.zoom, key.zoom, centerX, centerY)
        postRotate(key.rotationDegrees, centerX, centerY)
        postTranslate(key.panX * width, key.panY * height)
    }
}
