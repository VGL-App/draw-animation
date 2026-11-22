package com.vgl.draw.animation.core.project.backup

import android.net.Uri
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectNames
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.repository.isOutOfSpace
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectFormat
import com.vgl.draw.animation.core.project.storage.ProjectImport
import com.vgl.draw.animation.core.project.storage.ProjectImportCommitException
import com.vgl.draw.animation.core.project.storage.ProjectReadResult
import com.vgl.draw.animation.core.project.storage.ProjectStore
import com.vgl.draw.animation.core.project.storage.referencedFiles
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * [ProjectBackup] on a [ProjectStore], see [BackupFormat] for the file. [newId] gives the id of each restored project.
 *
 * Restoring streams every entry straight into a staging folder of the store ([ProjectStore.beginImport]): memory
 * only holds one 64 KB chunk, and nothing reaches the project list before the whole file was read up to `end.json`.
 * Errors are sorted by where they happen, not by exception type: any failure reading the source is NOT_A_BACKUP,
 * any failure writing the staging folder is NO_SPACE or WRITE_FAILED.
 *
 * Logs never hold a project name, id, uri or exception message: only the failure kind and the exception class.
 */
internal class DefaultProjectBackup(
    private val store: ProjectStore,
    private val repository: ProjectRepository,
    private val streams: BackupStreams,
    private val clock: () -> Long,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
    private val newId: () -> ProjectId = { ProjectId.random() },
) : ProjectBackup {
    override suspend fun export(target: Uri, ids: List<ProjectId>): BackupExportResult = withContext(ioDispatcher) {
        exportProjects(target, readUserProjects(ids))
    }

    override suspend fun exportAll(target: Uri): BackupExportResult = withContext(ioDispatcher) {
        val projects = readUserProjects(store.ids())
            .sortedWith(compareByDescending<Project> { it.updatedAtMillis }.thenBy { it.id.value })
        exportProjects(target, projects)
    }

    override suspend fun restore(source: Uri, onProgress: suspend (RestoreProgress) -> Unit): RestoreResult =
        withContext(ioDispatcher) {
            try {
                restoreFrom(source, onProgress)
            } catch (e: CancellationException) {
                logger.info(TAG, "Restore cancelled")
                throw e
            }
        }

    /** Readable user projects among [ids], in the same order; lessons and unreadable projects are left out. */
    private suspend fun readUserProjects(ids: List<ProjectId>): List<Project> = ids.mapNotNull { id ->
        (store.read(id) as? ProjectReadResult.Ok)?.project?.takeIf { it.isUserProject }
    }

    private suspend fun exportProjects(target: Uri, projects: List<Project>): BackupExportResult {
        if (projects.isEmpty()) return exportFailure(BackupExportFailure.NOTHING_TO_EXPORT, null)
        // Checked before opening the target, so a project the format cannot hold leaves the chosen file untouched.
        val encoded = projects.map { project ->
            if (BackupFormat.limitViolation(project) != null) return exportFailure(BackupExportFailure.TOO_LARGE, null)
            val bytes = ProjectFormat.encode(project).toByteArray(Charsets.UTF_8)
            if (bytes.size > BackupFormat.MAX_PROJECT_JSON_BYTES) {
                return exportFailure(BackupExportFailure.TOO_LARGE, null)
            }
            bytes
        }
        val raw = try {
            openTarget(target)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return exportFailure(writeFailureOf(e), e)
        }
        val detachable = DetachableOutputStream(BufferedOutputStream(raw, CHUNK_BYTES))
        val zip = ZipOutputStream(detachable)
        return try {
            writeEntries(zip, projects, encoded)
            // finish() writes the central directory: only a complete file gets one.
            zip.finish()
            zip.close()
            logger.info(TAG, "Export done: ${projects.size} projects")
            BackupExportResult.Success(projects.size)
        } catch (e: Throwable) {
            abandon(raw, detachable, zip)
            when (e) {
                is CancellationException -> throw e
                is TooLarge -> exportFailure(BackupExportFailure.TOO_LARGE, null)
                is Exception -> exportFailure(writeFailureOf(e), e)
                else -> throw e
            }
        }
    }

    /**
     * `"wt"` truncates a file the user chose to overwrite. Some providers refuse it; `"w"` may then leave the tail of
     * the old file after the new central directory, which a restore never reads (it stops after `end.json`).
     */
    private fun openTarget(target: Uri): OutputStream {
        try {
            return streams.openWrite(target, MODE_WRITE_TRUNCATE)
        } catch (e: Exception) {
            val refusedMode = e is IllegalArgumentException ||
                e is UnsupportedOperationException ||
                e is FileNotFoundException
            if (!refusedMode) throw e
        }
        return streams.openWrite(target, MODE_WRITE)
    }

    private suspend fun writeEntries(zip: ZipOutputStream, projects: List<Project>, encoded: List<ByteArray>) {
        var totalBytes = 0L
        fun entry(name: String, bytes: ByteArray) {
            totalBytes += bytes.size
            if (totalBytes > BackupFormat.MAX_TOTAL_BYTES) throw TooLarge()
            zip.putNextEntry(ZipEntry(name))
            zip.write(bytes)
            zip.closeEntry()
        }

        val manifest = Manifest(
            app = BackupFormat.APP_ID,
            backupFormatVersion = BackupFormat.CURRENT_BACKUP_FORMAT_VERSION,
            projects = projects.map { ManifestProject(it.name, it.frames.size) },
        )
        entry(BackupFormat.MANIFEST_ENTRY, BackupFormat.encodeManifest(manifest).toByteArray(Charsets.UTF_8))
        val fileCounts = mutableListOf<Int>()
        projects.forEachIndexed { index, project ->
            entry(BackupFormat.projectEntry(index, BackupFormat.PROJECT_JSON), encoded[index])
            var count = 0
            for (file in project.referencedFiles()) {
                currentCoroutineContext().ensureActive()
                val bytes = store.readFile(project.id, file) ?: continue
                if (bytes.size > BackupFormat.maxBytesFor(file)) throw TooLarge()
                entry(BackupFormat.projectEntry(index, file.relativePath), bytes)
                count++
            }
            fileCounts += count
        }
        currentCoroutineContext().ensureActive()
        val end = BackupEnd(fileCounts.map { BackupEndProject(it) })
        entry(BackupFormat.END_ENTRY, BackupFormat.encodeEnd(end).toByteArray(Charsets.UTF_8))
    }

    /**
     * After a failure: nothing more reaches the target, so the file there has neither `end.json` nor a central
     * directory and can never be restored. Closing the ZIP stream still frees its Deflater.
     */
    private fun abandon(raw: OutputStream, detachable: DetachableOutputStream, zip: ZipOutputStream) {
        detachable.detach()
        try {
            raw.close()
        } catch (e: Exception) {
            logger.warn(TAG, "Backup file not closed (${e::class.simpleName})")
        }
        runCatching { zip.close() }
    }

    private fun writeFailureOf(e: Exception): BackupExportFailure =
        if (isOutOfSpace(e)) BackupExportFailure.NO_SPACE else BackupExportFailure.WRITE_FAILED

    private fun exportFailure(reason: BackupExportFailure, e: Exception?): BackupExportResult.Failure {
        logger.warn(TAG, "Export failed: $reason${e?.let { " (${it::class.simpleName})" }.orEmpty()}")
        return BackupExportResult.Failure(reason)
    }

    private suspend fun restoreFrom(source: Uri, onProgress: suspend (RestoreProgress) -> Unit): RestoreResult {
        val input = try {
            streams.openRead(source)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return restoreFailure(RestoreFailure.NOT_A_BACKUP, e)
        }
        val zip = ZipInputStream(BufferedInputStream(input, CHUNK_BYTES))
        return try {
            RestoreRun(zip, onProgress).run()
        } catch (e: InvalidBackup) {
            restoreFailure(RestoreFailure.NOT_A_BACKUP, e.cause)
        } catch (e: NewerBackup) {
            restoreFailure(RestoreFailure.NEWER_VERSION, null)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Writing the staging folder failed (store errors), never reading the source (see readSource).
            val outOfSpace = e is IOException && isOutOfSpace(e)
            restoreFailure(if (outOfSpace) RestoreFailure.NO_SPACE else RestoreFailure.WRITE_FAILED, e)
        } finally {
            runCatching { zip.close() }
        }
    }

    private fun restoreFailure(reason: RestoreFailure, cause: Throwable?): RestoreResult.Failure {
        logger.warn(TAG, "Restore failed: $reason (${cause?.let { it::class.simpleName } ?: "format"})")
        return RestoreResult.Failure(reason)
    }

    /** One restore: reads [zip] entry by entry. Not shared between restores, so two restores can run at once. */
    private inner class RestoreRun(
        private val zip: ZipInputStream,
        private val onProgress: suspend (RestoreProgress) -> Unit,
    ) {
        private val buffer = ByteArray(CHUNK_BYTES)
        private var totalBytes = 0L
        private var doneFrames = 0
        private var framesBefore = 0
        private lateinit var summary: BackupSummary

        suspend fun run(): RestoreResult {
            val manifest = readManifest()
            val names = manifest.projects.map { normalizeName(it.name) }
            summary = BackupSummary(
                projectCount = manifest.projects.size,
                projectName = names.singleOrNull(),
                totalFrames = manifest.projects.sumOf { it.frameCount },
            )
            currentCoroutineContext().ensureActive()
            onProgress(RestoreProgress(summary, 0))
            currentCoroutineContext().ensureActive()

            val import = store.beginImport()
            try {
                val now = clock()
                readProjects(manifest, import, now)
                currentCoroutineContext().ensureActive()
                val ids = try {
                    withContext(NonCancellable) { import.commit() }
                } catch (e: ProjectImportCommitException) {
                    withContext(NonCancellable) { removeMoved(e.moved) }
                    val reason = if (isOutOfSpace(e)) RestoreFailure.NO_SPACE else RestoreFailure.WRITE_FAILED
                    return restoreFailure(reason, e)
                }
                repository.refresh()
                logger.info(TAG, "Restore done: ${ids.size} projects")
                return RestoreResult.Success(ids)
            } finally {
                withContext(NonCancellable) { import.discard() }
            }
        }

        private suspend fun readManifest(): Manifest {
            val first = readSource { zip.nextEntry } ?: throw InvalidBackup()
            if (first.name != BackupFormat.MANIFEST_ENTRY) throw InvalidBackup()
            val text = readEntryBytes(BackupFormat.MAX_MANIFEST_BYTES).decodeToString()
            return when (val result = BackupFormat.decodeManifest(text)) {
                is ManifestResult.Ok -> result.manifest
                ManifestResult.NewerVersion -> throw NewerBackup()
                ManifestResult.Invalid -> throw InvalidBackup()
            }
        }

        /** Reads every project entry up to `end.json`, staging the user projects; returns their new ids. */
        private suspend fun readProjects(manifest: Manifest, import: ProjectImport, now: Long): List<ProjectId> {
            val restored = mutableListOf<ProjectId>()
            val fileCounts = mutableListOf<Int>()
            var current: ProjectInFile? = null
            while (true) {
                currentCoroutineContext().ensureActive()
                // No entry before end.json: the file was cut, maybe right between two entries.
                val entry = readSource { zip.nextEntry } ?: throw InvalidBackup()
                val name = entry.name
                if (!isSafeEntryName(name)) throw InvalidBackup()
                if (name == BackupFormat.END_ENTRY) {
                    val last = current ?: throw InvalidBackup()
                    if (last.index != manifest.projects.lastIndex) throw InvalidBackup()
                    val end = BackupFormat.decodeEnd(readEntryBytes(BackupFormat.MAX_END_BYTES).decodeToString())
                        ?: throw InvalidBackup()
                    if (end.projects.map { it.fileCount } != fileCounts + last.fileCount) throw InvalidBackup()
                    finish(last, import, now, restored)
                    // Nothing after end.json is read: with mode "w" it may be the tail of an older file.
                    break
                }
                val (index, rest) = parseProjectEntry(name) ?: throw InvalidBackup()
                val open = current
                if (open != null && index == open.index) {
                    if (rest == BackupFormat.PROJECT_JSON || !open.seen.add(rest)) throw InvalidBackup()
                    readFileEntry(open, rest, import)
                    open.fileCount++
                } else if (index == (open?.index ?: -1) + 1 && index < manifest.projects.size) {
                    if (rest != BackupFormat.PROJECT_JSON) throw InvalidBackup()
                    if (open != null) {
                        fileCounts += open.fileCount
                        finish(open, import, now, restored)
                    }
                    current = readProjectJson(index, manifest.projects[index])
                } else {
                    throw InvalidBackup()
                }
            }
            if (restored.isEmpty()) throw InvalidBackup()
            return restored
        }

        private suspend fun readProjectJson(index: Int, listed: ManifestProject): ProjectInFile {
            val text = readEntryBytes(BackupFormat.MAX_PROJECT_JSON_BYTES).decodeToString()
            val project = when (val result = ProjectFormat.decode(text)) {
                is ProjectReadResult.Ok -> result.project
                is ProjectReadResult.NewerVersion -> throw NewerBackup()
                else -> throw InvalidBackup()
            }
            // Before building the file table: ProjectFormat has no limit on the reference image count.
            if (project.frames.size != listed.frameCount) throw InvalidBackup()
            if (BackupFormat.limitViolation(project) != null) throw InvalidBackup()
            val name = normalizeName(project.name)
            val files = try {
                project.referencedFiles().associateBy { it.relativePath }
            } catch (e: IllegalArgumentException) {
                throw InvalidBackup(e)
            }
            return ProjectInFile(
                index = index,
                project = project,
                name = name,
                newId = if (project.isUserProject) newId() else null,
                files = files,
                frameIndex = project.frames.withIndex().associate { it.value.id to it.index },
            )
        }

        /**
         * A file of the current project is written to the staging folder; an entry not in its file table (never used
         * as a path) or of a lesson is read past.
         */
        private suspend fun readFileEntry(open: ProjectInFile, rest: String, import: ProjectImport) {
            val file = open.files[rest]
            val id = open.newId
            if (file == null || id == null) {
                drain(file?.let { BackupFormat.maxBytesFor(it) } ?: BackupFormat.MAX_UNKNOWN_ENTRY_BYTES) { _, _ -> }
            } else {
                import.writeFile(id, file) { out ->
                    drain(BackupFormat.maxBytesFor(file)) { bytes, count -> out.write(bytes, 0, count) }
                }
            }
            // Meeting a cell of a frame means every frame before it is done.
            if (file is ProjectFile.Cell) advance(framesBefore + open.frameIndex.getValue(file.frameId))
        }

        private suspend fun finish(
            project: ProjectInFile,
            import: ProjectImport,
            now: Long,
            restored: MutableList<ProjectId>,
        ) {
            val id = project.newId
            if (id != null) {
                // Written last, after every file: a staged project without it is never moved into place.
                import.write(
                    project.project.copy(id = id, name = project.name, updatedAtMillis = now - project.index),
                )
                restored += id
            }
            // A lesson is skipped, but its frames are in totalFrames, so they count as done too.
            framesBefore += project.project.frames.size
            advance(framesBefore)
        }

        private suspend fun advance(frames: Int) {
            if (frames <= doneFrames) return
            doneFrames = frames
            onProgress(RestoreProgress(summary, frames))
            currentCoroutineContext().ensureActive()
        }

        private suspend fun readEntryBytes(cap: Long): ByteArray {
            val out = ByteArrayOutputStream()
            drain(cap) { bytes, count -> out.write(bytes, 0, count) }
            return out.toByteArray()
        }

        /**
         * Reads the current entry to its end in 64 KB chunks: ZipInputStream checks the CRC only at the end of an
         * entry. Stops as soon as the entry passes [cap] or the file passes the total limit.
         */
        private suspend fun drain(cap: Long, sink: (ByteArray, Int) -> Unit) {
            var entryBytes = 0L
            while (true) {
                val count = readSource { zip.read(buffer) }
                if (count < 0) return
                entryBytes += count
                totalBytes += count
                if (entryBytes > cap || totalBytes > BackupFormat.MAX_TOTAL_BYTES) throw InvalidBackup()
                sink(buffer, count)
                currentCoroutineContext().ensureActive()
            }
        }
    }

    /** A project of the file being read; [newId] is null for a lesson, which is not restored. */
    private class ProjectInFile(
        val index: Int,
        val project: Project,
        val name: String,
        val newId: ProjectId?,
        val files: Map<String, ProjectFile>,
        val frameIndex: Map<FrameId, Int>,
    ) {
        val seen = HashSet<String>()
        var fileCount = 0
    }

    private fun normalizeName(name: String): String = try {
        ProjectNames.normalize(name)
    } catch (e: IllegalArgumentException) {
        throw InvalidBackup(e)
    }

    /**
     * Every read of the source goes through here: any failure (a damaged or cut file, a dropped network connection,
     * a provider error) means the file cannot be restored.
     */
    private inline fun <T> readSource(block: () -> T): T = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        throw InvalidBackup(e)
    }

    /**
     * Refused on every Android version: Android 14 and later already throw for such names when targeting SDK 34 and
     * above, so older devices and unit tests behave the same.
     */
    private fun isSafeEntryName(name: String): Boolean = !name.startsWith("/") &&
        !name.startsWith("\\") &&
        '\\' !in name &&
        name.split('/').none { it == ".." }

    /** `projects/<i>/<rest>` with a canonical index, or null. */
    private fun parseProjectEntry(name: String): Pair<Int, String>? {
        if (!name.startsWith(BackupFormat.PROJECTS_DIR)) return null
        val path = name.removePrefix(BackupFormat.PROJECTS_DIR)
        val slash = path.indexOf('/')
        if (slash <= 0 || slash == path.lastIndex) return null
        val indexText = path.substring(0, slash)
        val index = indexText.toIntOrNull()?.takeIf { it >= 0 && it.toString() == indexText } ?: return null
        return index to path.substring(slash + 1)
    }

    private suspend fun removeMoved(ids: List<ProjectId>) {
        for (id in ids) {
            try {
                store.delete(id)
            } catch (e: IOException) {
                logger.warn(TAG, "Restored project not removed (${e::class.simpleName})")
            }
        }
    }

    private companion object {
        const val TAG = "Backup"
        const val CHUNK_BYTES = 64 * 1024
        const val MODE_WRITE_TRUNCATE = "wt"
        const val MODE_WRITE = "w"
    }
}

/** The source is not a valid backup: NOT_A_BACKUP. Not an IOException, so it is never taken for a write error. */
private class InvalidBackup(cause: Throwable? = null) : Exception(cause)

/** The file, or a project in it, was written by a newer app: NEWER_VERSION. */
private class NewerBackup : Exception()

/** A project or file breaks a limit of the format while writing: TOO_LARGE. */
private class TooLarge : Exception()

/**
 * Passes writes through until [detach]; afterwards drops every write, flush and close. After a failure the ZIP stream
 * is closed (to free its Deflater) without its central directory reaching the target.
 */
private class DetachableOutputStream(private val out: OutputStream) : OutputStream() {
    @Volatile
    private var detached = false

    fun detach() {
        detached = true
    }

    override fun write(b: Int) {
        if (!detached) out.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (!detached) out.write(b, off, len)
    }

    override fun flush() {
        if (!detached) out.flush()
    }

    override fun close() {
        if (!detached) out.close()
    }
}
