package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectReadResult
import com.vgl.draw.animation.core.project.storage.ProjectStore
import java.io.ByteArrayOutputStream
import java.io.IOException

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
    override suspend fun update(id: ProjectId): ThumbnailResult {
        val project = (store.read(id) as? ProjectReadResult.Ok)?.project ?: return ThumbnailResult.PROJECT_UNREADABLE
        return try {
            val bitmap = renderer.render(project, 0, RenderSize.thumbnail(project), includeBackground = true)
            val bytes = try {
                ByteArrayOutputStream().also { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }.toByteArray()
            } finally {
                bitmap.recycle()
            }
            store.writeFile(id, ProjectFile.Thumbnail, bytes)
            ThumbnailResult.WRITTEN
        } catch (e: IOException) {
            // A broken thumbnail must never break leaving the drawing screen.
            // The exception is not logged: its message may hold the file path, which holds the project id.
            logger.warn(TAG, "Thumbnail not written")
            ThumbnailResult.FAILED
        }
    }

    private companion object {
        const val TAG = "ProjectRender"
    }
}
