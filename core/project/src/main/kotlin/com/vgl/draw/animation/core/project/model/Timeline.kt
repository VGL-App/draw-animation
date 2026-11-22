package com.vgl.draw.animation.core.project.model

import kotlinx.serialization.Serializable

/** A layer. A project's layers are ordered bottom to top. The default name comes from the caller's strings. */
@Serializable
data class Layer(val id: LayerId, val name: String, val visible: Boolean)

/**
 * A frame, held for [hold] frames (1 to 4) during playback.
 * [cameraKey] is the virtual camera key set on this frame, if any; how frames without a key are drawn is a rendering
 * rule, not part of the format.
 */
@Serializable
data class Frame(val id: FrameId, val hold: Int = 1, val cameraKey: CameraKey? = null)

/**
 * A virtual camera key. [panX] and [panY] are fractions of the canvas width and height, so they hold at any
 * resolution; [zoom] is a factor (1.2 means 120%).
 */
@Serializable
data class CameraKey(val panX: Float, val panY: Float, val zoom: Float, val rotationDegrees: Float)

/** The project's "Camera on" switch. */
@Serializable
data class CameraSettings(val enabled: Boolean = false)
