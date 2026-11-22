package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectKind
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

/**
 * Reads and writes `project.json`: `{"formatVersion": N, "project": {...}}`.
 *
 * Versioning rule: every change to the stored description (adding, removing or renaming a field, or changing what a
 * field means) increments [CURRENT_FORMAT_VERSION], adds a step to [migrate] and adds a sample file
 * `project-v<N>.json` to the tests. Existing sample files are never edited. Changing the format later is blocking
 * point 9 of `docs/risk-scoring.md`.
 *
 * Decoding never throws: a broken file gives [ProjectReadResult.Corrupt], a file from a newer app gives
 * [ProjectReadResult.NewerVersion], both with the project name when it is still readable.
 */
internal object ProjectFormat {
    const val CURRENT_FORMAT_VERSION = 1

    private const val MAX_LAYERS = 10
    private const val MIN_HOLD = 1
    private const val MAX_HOLD = 4
    private const val MAX_PERCENT = 100
    private const val FORMAT_VERSION_KEY = "formatVersion"

    private val json = Json {
        encodeDefaults = true
        explicitNulls = true
        ignoreUnknownKeys = false
        classDiscriminator = "type"
    }

    /** The first rule [project] breaks, or null when it is valid. Shared by writing and reading. */
    fun validate(project: Project): String? {
        val frameIds = project.frames.map { it.id }
        val onion = project.onionSkin
        val reference = project.reference
        val background = project.background
        val kind = project.kind
        return when {
            project.frames.isEmpty() -> "no frame"
            project.layers.isEmpty() -> "no layer"
            project.layers.size > MAX_LAYERS -> "more than $MAX_LAYERS layers"
            frameIds.toSet().size != frameIds.size -> "duplicate frame id"
            project.layers.map { it.id }.toSet().size != project.layers.size -> "duplicate layer id"
            project.sounds.map { it.id }.toSet().size != project.sounds.size -> "duplicate sound id"
            project.frames.any { it.hold !in MIN_HOLD..MAX_HOLD } -> "hold out of range"
            project.frames.any { frame -> frame.cameraKey?.let { !isValid(it) } ?: false } -> "invalid camera key"
            onion.opacityPercent !in 0..MAX_PERCENT -> "onion skin opacity out of range"
            onion.framesBefore < 0 || onion.framesAfter < 0 -> "negative onion skin frame count"
            project.sounds.any { it.durationMillis < 0 } -> "negative sound duration"
            project.sounds.any { it.startFrameId != null && it.startFrameId !in frameIds } -> "sound starts at no frame"
            project.sounds.any { !isValidFileExtension(it.fileExtension) } -> "invalid sound file extension"
            kind is ProjectKind.Lesson && kind.step < 0 -> "negative lesson step"
            background is Background.Photo && !isValidFileExtension(background.fileExtension) ->
                "invalid background file extension"
            reference != null && reference.frameCount < 1 -> "reference without image"
            reference != null && reference.opacityPercent !in 0..MAX_PERCENT -> "reference opacity out of range"
            reference != null && !isValidFileExtension(reference.fileExtension) -> "invalid reference file extension"
            else -> null
        }
    }

    private fun isValid(key: CameraKey): Boolean = key.panX.isFinite() &&
        key.panY.isFinite() &&
        key.zoom.isFinite() &&
        key.rotationDegrees.isFinite() &&
        key.zoom > 0f

    /** Throws [IllegalArgumentException] when [project] breaks a rule of [validate]: a programming error. */
    fun encode(project: Project): String {
        val problem = validate(project)
        require(problem == null) { "Invalid project: $problem" }
        return json.encodeToString(StoredProject.serializer(), StoredProject(CURRENT_FORMAT_VERSION, project))
    }

    fun decode(text: String): ProjectReadResult {
        val root = try {
            json.parseToJsonElement(text) as? JsonObject
        } catch (e: SerializationException) {
            null
        } ?: return ProjectReadResult.Corrupt(name = null, reason = CorruptReason.INVALID_JSON)

        val name = readName(root)
        val versionElement = root[FORMAT_VERSION_KEY] as? JsonPrimitive
        val version = versionElement?.takeIf { !it.isString }?.intOrNull
            ?: return ProjectReadResult.Corrupt(name, CorruptReason.MISSING_VERSION)
        if (version > CURRENT_FORMAT_VERSION) return ProjectReadResult.NewerVersion(version, name)
        val current = if (version < CURRENT_FORMAT_VERSION) {
            migrate(version, root) ?: return ProjectReadResult.Corrupt(name, CorruptReason.UNKNOWN_VERSION)
        } else {
            root
        }

        val project = try {
            json.decodeFromJsonElement(StoredProject.serializer(), current).project
        } catch (e: IllegalArgumentException) {
            // SerializationException is an IllegalArgumentException, as is a failed id check.
            return ProjectReadResult.Corrupt(name, CorruptReason.INVALID_CONTENT)
        }
        if (validate(project) != null) return ProjectReadResult.Corrupt(name, CorruptReason.INVALID_CONTENT)
        return ProjectReadResult.Ok(project)
    }

    /**
     * Steps of the migration chain, by the version they start from: the step for N turns a version N file into a
     * version N + 1 file, including its `formatVersion`. Version 1 is the first version, so there is no step yet.
     */
    private val migrations: Map<Int, (JsonObject) -> JsonObject> = emptyMap()

    /** Turns a file of an older [version] into the current format, or returns null when a step is missing. */
    private fun migrate(version: Int, root: JsonObject): JsonObject? {
        var current = root
        for (from in version until CURRENT_FORMAT_VERSION) {
            val step = migrations[from] ?: return null
            current = step(current)
        }
        return current
    }

    /** `project.name` when it is a non-empty string. */
    private fun readName(root: JsonObject): String? {
        val name = (root["project"] as? JsonObject)?.get("name") as? JsonPrimitive ?: return null
        if (!name.isString) return null
        return name.contentOrNull?.takeIf { it.isNotEmpty() }
    }
}

/** The whole content of `project.json`. */
@Serializable
private data class StoredProject(val formatVersion: Int, val project: Project)
