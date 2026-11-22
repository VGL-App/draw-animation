package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId

/**
 * Projects saved on the device, one folder per project:
 *
 * ```
 * projects/<projectId>/
 *   project.json                      the description, with "formatVersion"
 *   thumbnail.png
 *   background.<ext>                  "Photo" background
 *   cells/<frameId>/<layerId>.png     one layer in one frame; no file means an empty cell
 *   sounds/<clipId>.<ext>
 *   reference/<index>.<ext>           reference images, index from 0
 * projects/.deleting-<projectId>/     a project being deleted, never listed
 * ```
 *
 * Write order callers must follow, also when creating a project: write the files first, `project.json` next, and
 * delete files no longer used last. A failure part way (for example a full disk) then leaves either the previous
 * project or a folder without `project.json`, which [ids] does not list. A creator calls [delete] when creating fails;
 * folders left by a creator that stopped (the app was killed) are removed later by [deleteIncomplete].
 * Files not referenced by the project (cells of a deleted frame, a removed sound) are ignored when reading.
 *
 * Every file is written atomically, so a reader always sees a whole version. Writes to the same project are
 * serialized; reads take no lock. Images and sounds are encoded by the caller: the store only handles bytes.
 */
interface ProjectStore {
    /** Projects that have a `project.json`, in no particular order. Empty when nothing was saved yet. */
    suspend fun ids(): List<ProjectId>

    /** Never throws: a missing project is [ProjectReadResult.NotFound], an unreadable one a `Corrupt` result. */
    suspend fun read(id: ProjectId): ProjectReadResult

    /**
     * Writes `project.json`. Throws [IllegalArgumentException] for an invalid project without touching the disk, and
     * [java.io.IOException] when writing fails. Does not change `updatedAtMillis`: the caller sets it.
     */
    suspend fun write(project: Project)

    /** The file's bytes, or null when there is no such file. Throws [java.io.IOException] when reading fails. */
    suspend fun readFile(id: ProjectId, file: ProjectFile): ByteArray?

    /** Writes the file atomically. Throws [java.io.IOException] when writing fails. */
    suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray)

    /** Does nothing when there is no such file. Throws [java.io.IOException] when the file cannot be deleted. */
    suspend fun deleteFile(id: ProjectId, file: ProjectFile)

    /**
     * Deletes the whole project folder. The folder is renamed to `.deleting-<id>` first, so the project leaves [ids]
     * even if deleting stops part way; leftovers are cleaned on the next call. Does nothing when there is no such
     * project. Throws [java.io.IOException] when the folder cannot be renamed or deleted.
     */
    suspend fun delete(id: ProjectId)

    /**
     * Deletes every project folder whose name is a valid id but which has no `project.json`: a create or a duplicate
     * that stopped part way. Folders whose name starts with `.` are never touched. Returns how many folders were
     * deleted. Throws [java.io.IOException] when a folder cannot be deleted. Callers must not run it while a project
     * is being created or duplicated.
     */
    suspend fun deleteIncomplete(): Int
}
