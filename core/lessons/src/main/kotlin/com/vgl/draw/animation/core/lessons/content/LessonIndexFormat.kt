package com.vgl.draw.animation.core.lessons.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

internal sealed interface IndexDecodeResult {
    data class Ok(val index: LessonIndex) : IndexDecodeResult

    /** [reason] names the broken rule and field, never the content of the file (ADR-0019). */
    data class Invalid(val reason: String) : IndexDecodeResult
}

/** Format version of index.json and lesson.json this app reads. */
internal const val CONTENT_FORMAT_VERSION = 1

/** A group or lesson id: lower-case words joined by single dashes, at most [MAX_ID_LENGTH] characters. */
private val ID_PATTERN = Regex("[a-z0-9]+(-[a-z0-9]+)*")
private const val MAX_ID_LENGTH = 40

internal fun isContentId(value: String): Boolean = value.length <= MAX_ID_LENGTH && ID_PATTERN.matches(value)

/**
 * Content from the server may gain new fields that an older app still reads; an incompatible change raises
 * `formatVersion`.
 */
internal val contentJson = Json { ignoreUnknownKeys = true }

/**
 * Reads `formatVersion` of a JSON object before decoding it, so a newer format is reported as such instead of as a
 * missing field. Returns null when [text] is not a JSON object with an integer `formatVersion`. Never throws.
 */
internal fun formatVersionOf(text: String): Int? {
    val root: JsonElement = try {
        Json.parseToJsonElement(text)
    } catch (e: IllegalArgumentException) {
        // SerializationException is an IllegalArgumentException.
        return null
    }
    return ((root as? JsonObject)?.get("formatVersion") as? JsonPrimitive)?.intOrNull
}

/** Reads `index.json` (plan T-048, section 3.1). Never throws. */
internal object LessonIndexFormat {
    fun decode(text: String, version: String): IndexDecodeResult {
        val formatVersion = formatVersionOf(text) ?: return invalid(version, "not a JSON object with formatVersion")
        if (formatVersion > CONTENT_FORMAT_VERSION) return invalid(version, "newer format $formatVersion")
        if (formatVersion != CONTENT_FORMAT_VERSION) return invalid(version, "unknown format $formatVersion")
        val json = try {
            contentJson.decodeFromString<IndexJson>(text)
        } catch (e: IllegalArgumentException) {
            // SerializationException is an IllegalArgumentException. Its message quotes the file: keep it out.
            return invalid(version, "missing or wrong field")
        }
        val error = check(json)
        return if (error != null) invalid(version, error) else IndexDecodeResult.Ok(json.toIndex())
    }

    private fun invalid(version: String, reason: String) = IndexDecodeResult.Invalid("index $version: $reason")

    /** The first broken rule, or null. */
    private fun check(json: IndexJson): String? {
        if (json.groups.isEmpty()) return "no group"
        val groupIds = json.groups.map { it.id }
        if (groupIds.any { !isContentId(it) }) return "group id breaks the id rule"
        if (groupIds.toSet().size != groupIds.size) return "duplicate group id"
        if (json.groups.any { it.title.isBlank() }) return "blank group title"
        val lessonIds = json.lessons.map { it.id }
        if (lessonIds.any { !isContentId(it) }) return "lesson id breaks the id rule"
        if (lessonIds.toSet().size != lessonIds.size) return "duplicate lesson id"
        for (lesson in json.lessons) {
            val error = checkLesson(lesson, groupIds)
            if (error != null) return "lesson ${lesson.id}: $error"
        }
        val emptyGroup = json.groups.firstOrNull { group -> json.lessons.none { it.groupId == group.id } }
        if (emptyGroup != null) return "group ${emptyGroup.id} has no lesson"
        return null
    }

    private fun checkLesson(lesson: LessonEntryJson, groupIds: List<String>): String? = when {
        lesson.title.isBlank() -> "blank title"
        lesson.groupId !in groupIds -> "unknown group"
        lesson.minutes !in 1..MAX_MINUTES -> "minutes out of range"
        lesson.frameCount !in 1..MAX_FRAMES -> "frameCount out of range"
        lesson.stepCount !in 1..MAX_STEPS -> "stepCount out of range"
        lesson.thumb != "${lesson.id}/thumb.webp" -> "thumb is not <id>/thumb.webp"
        lesson.pack != "${lesson.id}/lesson.zip" -> "pack is not <id>/lesson.zip"
        else -> null
    }

    private fun IndexJson.toIndex() = LessonIndex(
        groups = groups.map { LessonGroup(it.id, it.title) },
        lessons = lessons.map {
            LessonEntry(
                id = it.id,
                groupId = it.groupId,
                title = it.title,
                minutes = it.minutes,
                frameCount = it.frameCount,
                stepCount = it.stepCount,
                thumb = it.thumb,
                pack = it.pack,
            )
        },
    )

    private const val MAX_MINUTES = 120
    private const val MAX_FRAMES = 1000

    /** Also the limit of a lesson attempt in the progress store. */
    const val MAX_STEPS = 50
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
