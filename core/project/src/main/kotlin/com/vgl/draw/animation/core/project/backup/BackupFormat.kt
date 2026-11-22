package com.vgl.draw.animation.core.project.backup

import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.storage.ProjectFile
import kotlinx.serialization.Serializable

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

    /** Largest allowed size of [file], in bytes. */
    fun maxBytesFor(file: ProjectFile): Long = TODO()

    /** The first limit of the format [project] breaks, or null when it fits. */
    fun limitViolation(project: Project): String? = TODO()

    fun encodeManifest(manifest: Manifest): String = TODO()

    /** Never throws. */
    fun decodeManifest(text: String): ManifestResult = TODO()

    fun encodeEnd(end: BackupEnd): String = TODO()

    /** Null when [text] is not a valid `end.json`. Never throws. */
    fun decodeEnd(text: String): BackupEnd? = TODO()
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
