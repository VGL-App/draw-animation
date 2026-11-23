package com.vgl.draw.animation.feature.projects.data

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import kotlinx.coroutines.flow.Flow

/** What the project list reads: the projects, and one thumbnail at a time. Keeps no image in memory. */
interface ProjectsRepository {
    /** User projects, most recently updated first, unreadable ones last (from `ProjectRepository.projects`). */
    val items: Flow<List<ProjectListItem>>

    /**
     * The project's `thumbnail.png`, decoded at the smallest power-of-two scale that keeps it at least [maxWidthPx]
     * wide. A missing file of a readable project is drawn once first. Null when there is no image or it cannot be
     * read or decoded. Never throws an [java.io.IOException].
     */
    suspend fun thumbnail(id: ProjectId, maxWidthPx: Int): ImageBitmap?
}
