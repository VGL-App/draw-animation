package com.vgl.draw.animation.core.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A sound placed in the project. The audio file is always copied into the project as
 * `sounds/<id>.<fileExtension>`, so an effect from an expired pack still plays and exports.
 * [startFrameId] null means the sound plays from the start of the project.
 */
@Serializable
data class SoundClip(
    val id: SoundClipId,
    val type: SoundType,
    val title: String,
    val fileExtension: String,
    val startFrameId: FrameId?,
    val durationMillis: Long,
    val effectSource: EffectSource?,
)

@Serializable
enum class SoundType {
    @SerialName("music")
    MUSIC,

    @SerialName("voice")
    VOICE,

    @SerialName("effect")
    EFFECT,
}

/** Where an effect came from; only used to show its name. */
@Serializable
data class EffectSource(val packId: String, val soundId: String)
