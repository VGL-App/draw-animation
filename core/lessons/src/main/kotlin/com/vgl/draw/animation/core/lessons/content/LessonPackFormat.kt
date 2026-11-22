package com.vgl.draw.animation.core.lessons.content

import java.io.File
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal sealed interface PackDecodeResult {
    data class Ok(val pack: LessonPack) : PackDecodeResult

    /** [reason] names the broken rule and field, never the content of the file (ADR-0019). */
    data class Invalid(val reason: String) : PackDecodeResult
}

/** Reads `lesson.json` of an extracted pack in [dir] and checks the images it needs (plan T-048, section 3.1). */
internal object LessonPackFormat {
    fun decode(text: String, expectedId: String, version: String, dir: File): PackDecodeResult = TODO()
}

@Serializable
internal data class PackJson(
    val formatVersion: Int,
    val id: String,
    val aspectRatio: String,
    val resolution: String,
    val frameRate: Int,
    val background: String,
    val layers: List<LayerJson>,
    val frames: List<FrameJson>,
    val steps: List<StepJson>,
)

@Serializable
internal data class LayerJson(val name: String)

@Serializable
internal data class FrameJson(val hold: Int)

@Serializable
internal data class StepJson(
    val title: String,
    val instruction: String,
    val frame: Int,
    val layer: Int,
    @SerialName("guide") val hasGuide: Boolean,
    val demoFrameCount: Int,
)
