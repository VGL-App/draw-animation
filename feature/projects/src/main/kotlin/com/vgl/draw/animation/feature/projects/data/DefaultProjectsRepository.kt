package com.vgl.draw.animation.feature.projects.data

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.render.ProjectThumbnails
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.storage.ProjectStore
import com.vgl.draw.animation.feature.projects.di.ProjectsIoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class DefaultProjectsRepository @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val store: ProjectStore,
    private val thumbnails: ProjectThumbnails,
    @ProjectsIoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ProjectsRepository {
    // Skeleton of step 3 (tests first): replaced by the real implementation in step 4.
    override val items: Flow<List<ProjectListItem>> = flowOf(emptyList())

    override suspend fun thumbnail(id: ProjectId, maxWidthPx: Int): ImageBitmap? = null
}
