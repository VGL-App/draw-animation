package com.vgl.draw.animation.core.lessons.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal sealed interface IndexDecodeResult {
    data class Ok(val index: LessonIndex) : IndexDecodeResult

    /** [reason] names the broken rule and field, never the content of the file (ADR-0019). */
    data class Invalid(val reason: String) : IndexDecodeResult
}

/** Reads `index.json` (plan T-048, section 3.1). Never throws. */
internal object LessonIndexFormat {
    fun decode(text: String, version: String): IndexDecodeResult = TODO()
}

@Serializable
internal data class IndexJson(val formatVersion: Int, val groups: List<GroupJson>, val lessons: List<LessonEntryJson>)

@Serializable
internal data class GroupJson(val id: String, val title: String)

@Serializable
internal data class LessonEntryJson(
    val id: String,
    @SerialName("group") val groupId: String,
    val title: String,
    val minutes: Int,
    val frameCount: Int,
    val stepCount: Int,
    val thumb: String,
    val pack: String,
)
