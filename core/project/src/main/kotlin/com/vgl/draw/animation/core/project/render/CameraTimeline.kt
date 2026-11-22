package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.model.Project

/** The scene where it was drawn: no pan, no zoom, no rotation. */
val CAMERA_ORIGIN: CameraKey = CameraKey(panX = 0f, panY = 0f, zoom = 1f, rotationDegrees = 0f)

/**
 * The virtual camera of frame [frameIndex].
 *
 * Camera off, or no key at all: [CAMERA_ORIGIN]. A frame with a key uses it. Before the first key: [CAMERA_ORIGIN];
 * after the last key: the last key. Between two keys `a < i < b`: linear interpolation of the four values by the start
 * time of the frames (sum of `hold` of the frames before), so the scene moves evenly while playing frames held 2× to
 * 4×. Rotation interpolates straight in degrees, not along the shortest way (0 to 270 goes through 90 and 180).
 */
fun cameraAt(project: Project, frameIndex: Int): CameraKey = TODO()
