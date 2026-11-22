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
    fun decode(text: String, expectedId: String, version: String, dir: File): PackDecodeResult {
        val formatVersion = formatVersionOf(text) ?: return invalid("not a JSON object with formatVersion")
        if (formatVersion > CONTENT_FORMAT_VERSION) return invalid("newer format $formatVersion")
        if (formatVersion != CONTENT_FORMAT_VERSION) return invalid("unknown format $formatVersion")
        val json = try {
            contentJson.decodeFromString<PackJson>(text)
        } catch (e: IllegalArgumentException) {
            // SerializationException is an IllegalArgumentException. Its message quotes the file: keep it out.
            return invalid("missing or wrong field")
        }
        val error = check(json, expectedId, dir)
        return if (error != null) invalid(error) else PackDecodeResult.Ok(json.toPack(version, dir))
    }

    private fun invalid(reason: String) = PackDecodeResult.Invalid("lesson.json: $reason")

    /** The first broken rule, or null. */
    private fun check(json: PackJson, expectedId: String, dir: File): String? = when {
        json.id != expectedId -> "id is not the lesson asked"
        json.aspectRatio !in ASPECT_RATIOS -> "aspectRatio not allowed"
        json.resolution !in RESOLUTIONS -> "resolution not allowed"
        json.frameRate !in FRAME_RATES -> "frameRate not allowed"
        json.background !in BACKGROUNDS -> "background not allowed"
        json.layers.size !in 1..MAX_LAYERS -> "layer count out of range"
        json.layers.any {
            it.name.isBlank() || it.name.length > MAX_LAYER_NAME_LENGTH
        } -> "layer name blank or too long"
        json.frames.isEmpty() -> "no frame"
        json.frames.any { it.hold !in 1..MAX_HOLD } -> "hold out of range"
        json.steps.isEmpty() -> "no step"
        else -> json.steps.withIndex().firstNotNullOfOrNull { (index, step) ->
            checkStep(step, index, json, dir)?.let { "step $index: $it" }
        }
    }

    private fun checkStep(step: StepJson, index: Int, json: PackJson, dir: File): String? = when {
        step.title.isBlank() -> "blank title"
        step.instruction.isBlank() -> "blank instruction"
        step.frame !in json.frames.indices -> "frame out of range"
        step.layer !in json.layers.indices -> "layer out of range"
        step.demoFrameCount !in 0..MAX_DEMO_FRAMES -> "demoFrameCount out of range"
        step.hasGuide && !File(dir, "guides/$index.png").isFile -> "guide image missing"
        (0 until step.demoFrameCount).any { !File(dir, "demo/$index/$it.png").isFile } -> "demo image missing"
        else -> null
    }

    private fun PackJson.toPack(version: String, dir: File) = LessonPack(
        lessonId = id,
        contentVersion = version,
        aspectRatio = aspectRatio,
        resolution = resolution,
        frameRate = frameRate,
        background = background,
        layers = layers.map { LessonLayer(it.name) },
        frames = frames.map { LessonFrame(it.hold) },
        steps = steps.map { LessonStep(it.title, it.instruction, it.frame, it.layer, it.hasGuide, it.demoFrameCount) },
        dir = dir,
    )

    // Format values of :core:project (plan T-001), kept as raw strings and numbers here.
    private val ASPECT_RATIOS = setOf("9:16", "16:9", "1:1")
    private val RESOLUTIONS = setOf("720p", "1080p")
    private val FRAME_RATES = setOf(6, 8, 12, 15, 24, 30)
    private val BACKGROUNDS = setOf("white", "dark", "blue")

    /** Layers 6 to 10 of a project need an unlock (T-001): a lesson stays within the free ones. */
    private const val MAX_LAYERS = 5
    private const val MAX_LAYER_NAME_LENGTH = 40
    private const val MAX_HOLD = 4
    private const val MAX_DEMO_FRAMES = 120
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
