package com.vgl.draw.animation.core.project.backup

import android.net.Uri
import com.vgl.draw.animation.core.project.model.ProjectId

/**
 * Backs projects up to a `.frameloop` file chosen by the user (a [Uri] from the system file picker) and restores
 * them as new projects. Lesson projects are never backed up. Errors come back as results, never as exceptions.
 */
interface ProjectBackup {
    /** Writes the given user projects (lessons and unreadable ones skipped) to [target]. Never throws IOException. */
    suspend fun export(target: Uri, ids: List<ProjectId>): BackupExportResult

    /** Every readable user project, most recently updated first (the order of ProjectRepository.projects). */
    suspend fun exportAll(target: Uri): BackupExportResult

    /**
     * Restores every project of [source] as new projects. [onProgress] runs on the IO dispatcher, first with
     * doneFrames = 0 once the manifest is read, last with doneFrames = totalFrames; it must be quick and thread safe
     * (for example update a MutableStateFlow). Cancelling the calling coroutine stops the restore and leaves nothing
     * behind. Never throws IOException.
     */
    suspend fun restore(source: Uri, onProgress: suspend (RestoreProgress) -> Unit): RestoreResult
}

sealed interface BackupExportResult {
    data class Success(val projectCount: Int) : BackupExportResult

    data class Failure(val reason: BackupExportFailure) : BackupExportResult
}

/** TOO_LARGE: a project or file breaks a limit of the backup format, so the file could not be restored. */
enum class BackupExportFailure { NO_SPACE, WRITE_FAILED, NOTHING_TO_EXPORT, TOO_LARGE }

/** What a restore dialog shows: [projectName] only when the file holds one project. */
data class BackupSummary(val projectCount: Int, val projectName: String?, val totalFrames: Int)

data class RestoreProgress(val summary: BackupSummary, val doneFrames: Int)

sealed interface RestoreResult {
    /** The new projects, in the order of the file. */
    data class Success(val projectIds: List<ProjectId>) : RestoreResult

    data class Failure(val reason: RestoreFailure) : RestoreResult
}

/**
 * NOT_A_BACKUP: not a backup file, damaged, cut short, or reading it failed. NEWER_VERSION: written by a newer app.
 * NO_SPACE, WRITE_FAILED: writing the restored projects failed.
 */
enum class RestoreFailure { NOT_A_BACKUP, NEWER_VERSION, NO_SPACE, WRITE_FAILED }
