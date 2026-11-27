package com.vgl.draw.animation.feature.projects.create

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.repository.NewBackground
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.data.NewProjectDefaults
import com.vgl.draw.animation.feature.projects.data.NewProjectRepository
import com.vgl.draw.animation.feature.projects.data.PreparedPhoto
import com.vgl.draw.animation.feature.projects.testProjectId
import kotlinx.coroutines.CompletableDeferred

/**
 * A [NewProjectRepository] in memory. [defaults] is what the screen opens with; [count] is what `createdCount`
 * returns at the moment it is called. Each `create` is recorded in [created]; [createResult] is returned when set,
 * else a success with [createdId]. [createGate] holds `create` until it completes. `preparePhoto` records its uri in
 * [photoUris], waits on [photoGate] when set, then returns [photo] (null: the image cannot be used). [order], when
 * set, also receives "prepare:<uri>" for each `preparePhoto`, for the tests of the order of calls.
 */
class FakeNewProjectRepository(
    var defaults: NewProjectDefaults = NewProjectDefaults(
        aspectRatio = AspectRatio.PORTRAIT_9_16,
        frameRate = FrameRate.FPS_12,
        defaultName = DEFAULT_NAME,
        firstLayerName = FIRST_LAYER,
    ),
) : NewProjectRepository {
    var count = 4
    val created = mutableListOf<NewProject>()
    var createResult: ProjectResult<Project>? = null
    var createGate: CompletableDeferred<Unit>? = null
    var createdId: ProjectId = testProjectId(CREATED_ID)
    val photoUris = mutableListOf<String>()
    var photo: PreparedPhoto? = null
    var photoGate: CompletableDeferred<PreparedPhoto?>? = null
    var order: MutableList<String>? = null

    override suspend fun defaults(): NewProjectDefaults = defaults

    override suspend fun createdCount(): Int = count

    override suspend fun create(request: NewProject): ProjectResult<Project> {
        created += request
        createGate?.await()
        createResult?.let { return it }
        return ProjectResult.Success(projectOf(createdId, request))
    }

    override suspend fun preparePhoto(uri: String): PreparedPhoto? {
        photoUris += uri
        order?.add("prepare:$uri")
        photoGate?.let { return it.await() }
        return photo
    }

    companion object {
        const val DEFAULT_NAME = "My animation 5"
        const val FIRST_LAYER = "Layer 1"
        const val CREATED_ID = 77

        fun projectOf(id: ProjectId, request: NewProject): Project = Project.create(
            id = id,
            name = request.name,
            aspectRatio = request.aspectRatio,
            resolution = request.resolution,
            frameRate = request.frameRate,
            background = when (val background = request.background) {
                is NewBackground.Solid -> Background.Solid(background.color)
                is NewBackground.Photo -> Background.Photo(background.extension)
            },
            firstLayerName = request.firstLayerName,
            nowMillis = 1_000L,
        )
    }
}

/** A prepared photo with [bytes] and the given [preview]. */
fun preparedPhoto(preview: ImageBitmap, bytes: ByteArray = byteArrayOf(1, 2, 3)): PreparedPhoto =
    PreparedPhoto(bytes = bytes, extension = "jpg", preview = preview)

/** The ready state the screen tests start from: the presets of the design. */
fun readyState(
    aspectRatio: AspectRatio = AspectRatio.PORTRAIT_9_16,
    resolution: Resolution = Resolution.P1080,
    frameRate: FrameRate = FrameRate.FPS_12,
    background: BackgroundChoice = BackgroundChoice.Solid(BackgroundColor.WHITE),
    photoPreview: ImageBitmap? = null,
    photoLoading: Boolean = false,
    creating: Boolean = false,
    name: String = FakeNewProjectRepository.DEFAULT_NAME,
): NewProjectUiState.Ready = NewProjectUiState.Ready(
    name = name,
    defaultName = FakeNewProjectRepository.DEFAULT_NAME,
    aspectRatio = aspectRatio,
    resolution = resolution,
    frameRate = frameRate,
    background = background,
    photoPreview = photoPreview,
    photoLoading = photoLoading,
    creating = creating,
    error = null,
)
