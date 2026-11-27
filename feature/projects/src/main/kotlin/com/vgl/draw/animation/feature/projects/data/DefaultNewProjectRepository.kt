package com.vgl.draw.animation.feature.projects.data

import android.content.Context
import com.vgl.draw.animation.core.data.settings.DeviceSettingsRepository
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.render.ProjectThumbnails
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.di.ProjectsIoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

internal class DefaultNewProjectRepository @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val thumbnails: ProjectThumbnails,
    private val settings: DeviceSettingsRepository,
    private val photoPreparer: PhotoPreparer,
    @ApplicationContext private val context: Context,
    @ProjectsIoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : NewProjectRepository {
    // Test-first skeleton (T-011): filled in by the code commit.
    override suspend fun defaults(): NewProjectDefaults =
        NewProjectDefaults(AspectRatio.PORTRAIT_9_16, FrameRate.FPS_12, "", "")

    override suspend fun createdCount(): Int = 0

    override suspend fun create(request: NewProject): ProjectResult<Project> =
        ProjectResult.Failure(ProjectFailure.WRITE_FAILED)

    override suspend fun preparePhoto(uri: String): PreparedPhoto? = null
}
