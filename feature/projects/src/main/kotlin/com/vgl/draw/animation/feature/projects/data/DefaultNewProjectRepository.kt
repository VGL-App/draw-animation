package com.vgl.draw.animation.feature.projects.data

import android.content.Context
import com.vgl.draw.animation.core.data.settings.DeviceSettingsRepository
import com.vgl.draw.animation.core.data.settings.DeviceSettingsValues
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.render.ProjectThumbnails
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.R
import com.vgl.draw.animation.feature.projects.di.ProjectsIoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * The Create screen's data: the Settings defaults (T-005) turned into project types, the projects counter (T-002),
 * creating through [ProjectRepository] and drawing the new project's thumbnail (T-004), preparing a picked photo.
 * Does not read the Settings flag: the view model decides.
 */
internal class DefaultNewProjectRepository @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val thumbnails: ProjectThumbnails,
    private val settings: DeviceSettingsRepository,
    private val photoPreparer: PhotoPreparer,
    @ApplicationContext private val context: Context,
    @ProjectsIoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : NewProjectRepository {
    override suspend fun defaults(): NewProjectDefaults {
        val created = projectRepository.createdCount.first()
        return NewProjectDefaults(
            aspectRatio = aspectRatioOf(settings.defaultAspectRatio.first()),
            frameRate = frameRateOf(settings.defaultFrameRate.first()),
            defaultName = context.getString(R.string.new_project_default_name, created + 1),
            firstLayerName = context.getString(R.string.new_project_first_layer_name),
        )
    }

    override suspend fun createdCount(): Int = projectRepository.createdCount.first()

    // ProjectRepository turns every write error into a Failure; the thumbnail never throws, and its result does not
    // change the result of creating (the list draws a missing thumbnail by itself).
    override suspend fun create(request: NewProject): ProjectResult<Project> {
        val result = projectRepository.create(request)
        if (result is ProjectResult.Success) thumbnails.update(result.value.id)
        return result
    }

    override suspend fun preparePhoto(uri: String): PreparedPhoto? =
        withContext(ioDispatcher) { photoPreparer.prepare(uri) }
}

/** A stored Settings value as an [AspectRatio]; an unknown value gives the Settings default. */
internal fun aspectRatioOf(value: String): AspectRatio = when (value) {
    DeviceSettingsValues.ASPECT_RATIO_9_16 -> AspectRatio.PORTRAIT_9_16
    DeviceSettingsValues.ASPECT_RATIO_16_9 -> AspectRatio.LANDSCAPE_16_9
    DeviceSettingsValues.ASPECT_RATIO_1_1 -> AspectRatio.SQUARE_1_1
    else -> aspectRatioOf(DeviceSettingsValues.DEFAULT_ASPECT_RATIO)
}

/** A stored Settings value as a [FrameRate]; an unknown value gives the Settings default. */
internal fun frameRateOf(value: Int): FrameRate = FrameRate.fromFpsOrNull(value)
    ?: requireNotNull(FrameRate.fromFpsOrNull(DeviceSettingsValues.DEFAULT_FRAME_RATE))
