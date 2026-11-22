package com.vgl.draw.animation.core.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Onion skin settings. A null color means the theme's color (`onionPrevious`, `onionNext`). */
@Serializable
data class OnionSkinSettings(
    val enabled: Boolean = false,
    val framesBefore: Int = 2,
    val framesAfter: Int = 1,
    val opacityPercent: Int = 45,
    val beforeColorArgb: Int? = null,
    val afterColorArgb: Int? = null,
    val loop: Boolean = false,
)

/**
 * The reference layer drawn under the frames and never exported: one image, or a video split into [frameCount]
 * images at import, stored as `reference/<index>.<fileExtension>`.
 */
@Serializable
data class ReferenceLayer(
    val type: ReferenceType,
    val fileExtension: String,
    val frameCount: Int,
    val opacityPercent: Int = 40,
    val visible: Boolean = true,
)

@Serializable
enum class ReferenceType {
    @SerialName("image")
    IMAGE,

    @SerialName("video")
    VIDEO,
}
