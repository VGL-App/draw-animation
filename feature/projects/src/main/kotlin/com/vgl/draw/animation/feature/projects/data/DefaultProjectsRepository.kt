package com.vgl.draw.animation.feature.projects.data

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.render.ProjectThumbnails
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectReadResult
import com.vgl.draw.animation.core.project.storage.ProjectStore
import com.vgl.draw.animation.feature.projects.di.ProjectsIoDispatcher
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class DefaultProjectsRepository @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val store: ProjectStore,
    private val thumbnails: ProjectThumbnails,
    @ProjectsIoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ProjectsRepository {
    override val items: Flow<List<ProjectListItem>> get() = projectRepository.projects

    override suspend fun thumbnail(id: ProjectId, maxWidthPx: Int): ImageBitmap? = withContext(ioDispatcher) {
        try {
            val bytes = store.readFile(id, ProjectFile.Thumbnail) ?: drawMissing(id) ?: return@withContext null
            decode(bytes, maxWidthPx)
        } catch (e: IOException) {
            null
        }
    }

    /** A project just created has no thumbnail until the drawing screen is left: draw it once, then read it again. */
    private suspend fun drawMissing(id: ProjectId): ByteArray? {
        if (store.read(id) !is ProjectReadResult.Ok) return null
        thumbnails.update(id)
        return store.readFile(id, ProjectFile.Thumbnail)
    }

    private fun decode(bytes: ByteArray, maxWidthPx: Int): ImageBitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize(bounds.outWidth, maxWidthPx) }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)?.asImageBitmap()
    }

    /** The largest power of two that keeps the decoded width at least [maxWidthPx]. */
    private fun sampleSize(width: Int, maxWidthPx: Int): Int {
        var sample = 1
        while (width / (sample * 2) >= maxWidthPx.coerceAtLeast(1)) sample *= 2
        return sample
    }
}
