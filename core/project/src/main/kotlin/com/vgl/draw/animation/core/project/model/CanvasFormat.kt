package com.vgl.draw.animation.core.project.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class AspectRatio {
    @SerialName("9:16")
    PORTRAIT_9_16,

    @SerialName("16:9")
    LANDSCAPE_16_9,

    @SerialName("1:1")
    SQUARE_1_1,
}

@Serializable
enum class Resolution {
    @SerialName("720p")
    P720,

    @SerialName("1080p")
    P1080,
}

/** Frames per second offered by the app. Stored as the plain number, for example `12`. */
@Serializable(with = FrameRateSerializer::class)
enum class FrameRate(val fps: Int) {
    FPS_6(6),
    FPS_8(8),
    FPS_12(12),
    FPS_15(15),
    FPS_24(24),
    FPS_30(30),
    ;

    companion object {
        fun fromFpsOrNull(fps: Int): FrameRate? = entries.firstOrNull { it.fps == fps }
    }
}

internal object FrameRateSerializer : KSerializer<FrameRate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.vgl.draw.animation.core.project.FrameRate", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: FrameRate) = encoder.encodeInt(value.fps)

    override fun deserialize(decoder: Decoder): FrameRate {
        val fps = decoder.decodeInt()
        return FrameRate.fromFpsOrNull(fps) ?: throw SerializationException("Unsupported frame rate $fps")
    }
}

/** Size of the drawing canvas in pixels. */
data class CanvasSize(val width: Int, val height: Int)

/** The short side is 720 or 1080 pixels; the long side follows the aspect ratio. */
fun canvasSize(aspectRatio: AspectRatio, resolution: Resolution): CanvasSize = TODO()
