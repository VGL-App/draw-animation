package com.vgl.draw.animation.core.project.repository

import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.ProjectKind
import com.vgl.draw.animation.core.project.model.Resolution
import kotlinx.coroutines.flow.Flow

/**
 * The user's projects, for every feature: the list, creating, saving, renaming, duplicating, deleting, and how many
 * projects were ever created. Write errors come back as [ProjectResult.Failure], never as exceptions; a create or a
 * duplicate that fails part way leaves no half-written project. Names are checked with [ProjectNames]: a blank name is
 * a programming error ([IllegalArgumentException]).
 */
interface ProjectRepository {
    /**
     * User projects (no lessons), most recently updated first; unreadable ones last, by id. Re-emitted after every
     * change made here and after [refresh].
     */
    val projects: Flow<List<ProjectListItem>>

    /** Projects ever created with [create] and kind Regular. Never decreases. 0 when nothing stored or unreadable. */
    val createdCount: Flow<Int>

    /** Writes a new project (one frame, one layer). Only kind Regular increases [createdCount]. */
    suspend fun create(request: NewProject): ProjectResult<Project>

    /** Writes `project.json` with `updatedAtMillis` set to now. Image files are written by the caller beforehand. */
    suspend fun save(project: Project): ProjectResult<Project>

    /** Changes the name only; `updatedAtMillis` is kept, so the project does not move in the list. */
    suspend fun rename(id: ProjectId, name: String): ProjectResult<Project>

    /** Copies the project and every file it references under a new id, named "<name> copy" (see [ProjectNames]). */
    suspend fun duplicate(id: ProjectId): ProjectResult<Project>

    /** Deletes the project folder. A missing project is a success. Does not decrease [createdCount]. */
    suspend fun delete(id: ProjectId): ProjectResult<Unit>

    /** Reloads the list after another writer (restore, cells written by the editor) changed projects. */
    fun refresh()
}

/** One entry of [ProjectRepository.projects]. */
sealed interface ProjectListItem {
    val id: ProjectId

    data class Readable(val project: Project) : ProjectListItem {
        override val id: ProjectId get() = project.id
    }

    /** A project whose `project.json` cannot be read. [name] is kept when still readable; never deleted by the app. */
    data class Unreadable(override val id: ProjectId, val name: String?, val newerVersion: Boolean) : ProjectListItem
}

/** What [ProjectRepository.create] needs. [name] must not be blank: the caller puts the default name instead. */
data class NewProject(
    val name: String,
    val aspectRatio: AspectRatio,
    val resolution: Resolution,
    val frameRate: FrameRate,
    val background: NewBackground,
    val firstLayerName: String,
    val kind: ProjectKind = ProjectKind.Regular,
)

sealed interface NewBackground {
    data class Solid(val color: BackgroundColor) : NewBackground

    /** Bytes already rotated and scaled by the caller; [extension] like "jpg", checked as in `ProjectFile`. */
    class Photo(val bytes: ByteArray, val extension: String) : NewBackground
}

sealed interface ProjectResult<out T> {
    data class Success<T>(val value: T) : ProjectResult<T>

    data class Failure(val reason: ProjectFailure) : ProjectResult<Nothing>
}

enum class ProjectFailure {
    /** The device has no space left; nothing half-written was kept. */
    NO_SPACE,

    /** Any other write error; nothing half-written was kept. */
    WRITE_FAILED,

    /** No project with this id. */
    NOT_FOUND,

    /** The project's `project.json` cannot be read. */
    UNREADABLE,
}
