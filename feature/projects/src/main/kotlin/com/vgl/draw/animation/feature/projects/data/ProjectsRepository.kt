package com.vgl.draw.animation.feature.projects.data

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.core.project.repository.ProjectResult
import kotlinx.coroutines.flow.Flow

/**
 * What the project list reads and changes: the projects, one thumbnail at a time, and the menu actions (rename,
 * duplicate, delete). Keeps no image in memory.
 */
interface ProjectsRepository {
    /** User projects, most recently updated first, unreadable ones last (from `ProjectRepository.projects`). */
    val items: Flow<List<ProjectListItem>>

    /**
     * The project's `thumbnail.png`, decoded at the smallest power-of-two scale that keeps it at least [maxWidthPx]
     * wide. A missing file of a readable project is drawn once first. Null when there is no image or it cannot be
     * read or decoded. Never throws an [java.io.IOException].
     */
    suspend fun thumbnail(id: ProjectId, maxWidthPx: Int): ImageBitmap?

    /** Renames the project (`ProjectRepository.rename`). [name] must not be blank. */
    suspend fun rename(id: ProjectId, name: String): ProjectResult<Project>

    /** Copies the project; the copy comes first in [items] (`ProjectRepository.duplicate`). */
    suspend fun duplicate(id: ProjectId): ProjectResult<Project>

    /** Deletes the project and its files (`ProjectRepository.delete`). */
    suspend fun delete(id: ProjectId): ProjectResult<Unit>
}
