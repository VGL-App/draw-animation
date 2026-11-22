package com.vgl.draw.animation.core.project.backup

import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.storage.ProjectFile
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

/**
 * The `.frameloop` file: a ZIP file whose entries come in exactly this order:
 *
 * ```
 * manifest.json                    first entry
 * projects/<i>/project.json        project i (from 0), the text of ProjectFormat.encode
 * projects/<i>/<relativePath>      the files of project i (ProjectFile.relativePath), after its project.json
 * end.json                         last entry, required, right after the last entry of the last project
 * ```
 *
 * `end.json` holds the number of file entries of each project: a file cut right after a whole entry still reads
 * without error in `ZipInputStream`, so without it a cut file would restore with files missing.
 *
 * Versioning rule, as for `ProjectFormat`: any change to the ZIP layout, `manifest.json` or `end.json` increments
 * [CURRENT_BACKUP_FORMAT_VERSION] and adds a sample file `backup-v<N>.frameloop` to the tests; existing sample files
 * are never edited. The `project.json` inside follows `ProjectFormat` and its migrations.
 *
 * The limits are shared by writing and reading, so every file the app writes can be restored. A file cap is the size
 * a `ByteArray` surely fits in memory on a low-end minSdk 26 device, because restored files are later read whole.
 */
internal object BackupFormat {
    const val CURRENT_BACKUP_FORMAT_VERSION = 1
    const val APP_ID = "frameloop"

    const val MANIFEST_ENTRY = "manifest.json"
    const val END_ENTRY = "end.json"
    const val PROJECTS_DIR = "projects/"
    const val PROJECT_JSON = "project.json"

    const val MAX_MANIFEST_BYTES = 1L shl 20
    const val MAX_END_BYTES = 1L shl 20
    const val MAX_PROJECT_JSON_BYTES = 32L shl 20

    /** An entry not in the project's file table, read past and dropped. */
    const val MAX_UNKNOWN_ENTRY_BYTES = 16L shl 20

    /** Uncompressed bytes of every entry of a file. */
    const val MAX_TOTAL_BYTES = 4L shl 30

    const val MAX_PROJECTS = 10_000
    const val MAX_FRAMES_PER_PROJECT = 100_000
    const val MAX_CELLS_PER_PROJECT = 1_000_000
    const val MAX_SOUNDS = 1_000
    const val MAX_REFERENCE_IMAGES = 10_000

    /** Name of the entry of [relativePath] in project [index]. */
    fun projectEntry(index: Int, relativePath: String): String = "$PROJECTS_DIR$index/$relativePath"

    private const val THUMBNAIL_MAX_BYTES = 8L shl 20
    private const val CELL_MAX_BYTES = 32L shl 20
    private const val MEDIA_MAX_BYTES = 64L shl 20

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    /** Largest allowed size of [file], in bytes. */
    fun maxBytesFor(file: ProjectFile): Long = when (file) {
        ProjectFile.Thumbnail -> THUMBNAIL_MAX_BYTES
        is ProjectFile.Cell -> CELL_MAX_BYTES
        is ProjectFile.Background, is ProjectFile.ReferenceImage, is ProjectFile.Sound -> MEDIA_MAX_BYTES
    }

    /** The first limit of the format [project] breaks, or null when it fits. */
    fun limitViolation(project: Project): String? = when {
        project.frames.size > MAX_FRAMES_PER_PROJECT -> "more than $MAX_FRAMES_PER_PROJECT frames"
        project.layers.size.toLong() * project.frames.size > MAX_CELLS_PER_PROJECT ->
            "more than $MAX_CELLS_PER_PROJECT cells"
        project.sounds.size > MAX_SOUNDS -> "more than $MAX_SOUNDS sounds"
        (project.reference?.frameCount ?: 0) > MAX_REFERENCE_IMAGES ->
            "more than $MAX_REFERENCE_IMAGES reference images"
        else -> null
    }

    fun encodeManifest(manifest: Manifest): String = json.encodeToString(Manifest.serializer(), manifest)

    /**
     * Checks `app` and the version before the rest, so a file of a newer format is reported as such even when its
     * fields changed. Never throws.
     */
    fun decodeManifest(text: String): ManifestResult {
        val root = try {
            json.parseToJsonElement(text) as? JsonObject
        } catch (e: IllegalArgumentException) {
            null
        } ?: return ManifestResult.Invalid
        val app = (root["app"] as? JsonPrimitive)?.takeIf { it.isString }?.content
        if (app != APP_ID) return ManifestResult.Invalid
        val version = (root["backupFormatVersion"] as? JsonPrimitive)?.takeIf { !it.isString }?.intOrNull
            ?: return ManifestResult.Invalid
        if (version > CURRENT_BACKUP_FORMAT_VERSION) return ManifestResult.NewerVersion
        if (version < 1) return ManifestResult.Invalid
        val manifest = try {
            json.decodeFromJsonElement(Manifest.serializer(), root)
        } catch (e: IllegalArgumentException) {
            // SerializationException is an IllegalArgumentException.
            return ManifestResult.Invalid
        }
        val projects = manifest.projects
        val valid = projects.isNotEmpty() &&
            projects.size <= MAX_PROJECTS &&
            projects.all { it.frameCount in 1..MAX_FRAMES_PER_PROJECT } &&
            projects.sumOf { it.frameCount.toLong() } <= Int.MAX_VALUE
        return if (valid) ManifestResult.Ok(manifest) else ManifestResult.Invalid
    }

    fun encodeEnd(end: BackupEnd): String = json.encodeToString(BackupEnd.serializer(), end)

    /** Null when [text] is not a valid `end.json`. Never throws. */
    fun decodeEnd(text: String): BackupEnd? = try {
        json.decodeFromString(BackupEnd.serializer(), text)
    } catch (e: IllegalArgumentException) {
        null
    }
}

/** `manifest.json`: what a restore dialog needs before reading any project. */
@Serializable
internal data class Manifest(val app: String, val backupFormatVersion: Int, val projects: List<ManifestProject>)

@Serializable
internal data class ManifestProject(val name: String, val frameCount: Int)

/** `end.json`: one element per project, in file order. */
@Serializable
internal data class BackupEnd(val projects: List<BackupEndProject>)

/** [fileCount]: entries of the project other than its `project.json`. */
@Serializable
internal data class BackupEndProject(val fileCount: Int)

internal sealed interface ManifestResult {
    data class Ok(val manifest: Manifest) : ManifestResult

    data object NewerVersion : ManifestResult

    data object Invalid : ManifestResult
}
