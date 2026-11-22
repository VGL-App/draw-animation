package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.storage.ProjectStore

/** Keeps `thumbnail.png` of a project: its first frame, all visible layers, over the project background. */
interface ProjectThumbnails {
    /** Draws the thumbnail again from the current cells and writes it over the old one. Never throws. */
    suspend fun update(id: ProjectId): ThumbnailResult
}

enum class ThumbnailResult {
    WRITTEN,

    /** The project could not be read (missing, corrupt, newer format): nothing was written, the old file is kept. */
    PROJECT_UNREADABLE,

    /** Reading a project file or writing the thumbnail failed. */
    FAILED,
}

internal class DefaultProjectThumbnails(
    private val store: ProjectStore,
    private val renderer: FrameRenderer,
    private val logger: AppLogger,
) : ProjectThumbnails {
    override suspend fun update(id: ProjectId): ThumbnailResult = TODO()
}
