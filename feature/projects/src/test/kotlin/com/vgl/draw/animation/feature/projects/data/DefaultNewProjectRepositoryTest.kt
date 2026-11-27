package com.vgl.draw.animation.feature.projects.data

import androidx.test.core.app.ApplicationProvider
import com.vgl.draw.animation.core.data.settings.DeviceSettingsRepository
import com.vgl.draw.animation.core.data.settings.DeviceSettingsValues
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.render.ProjectThumbnails
import com.vgl.draw.animation.core.project.render.ThumbnailResult
import com.vgl.draw.animation.core.project.repository.NewBackground
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.feature.projects.create.FakeNewProjectRepository
import com.vgl.draw.animation.feature.projects.testProjectId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * [DefaultNewProjectRepository] over a project repository, thumbnails and device settings in memory. Robolectric for
 * the strings of the default name and the first layer (English). The IO dispatcher is a [StandardTestDispatcher] on
 * the scheduler of `runTest` itself (L-001).
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "en")
class DefaultNewProjectRepositoryTest {
    private val projects = CreatingProjectRepository()
    private val thumbnails = CountingThumbnails()
    private val settings = MemorySettings()

    private fun TestScope.repository() = DefaultNewProjectRepository(
        projectRepository = projects,
        thumbnails = thumbnails,
        settings = settings,
        photoPreparer = PhotoPreparer(ApplicationProvider.getApplicationContext(), RecordingAppLogger()),
        context = ApplicationProvider.getApplicationContext(),
        ioDispatcher = StandardTestDispatcher(testScheduler),
    )

    private fun request(background: NewBackground = NewBackground.Solid(BackgroundColor.WHITE)) = NewProject(
        name = "Ball",
        aspectRatio = AspectRatio.PORTRAIT_9_16,
        resolution = Resolution.P1080,
        frameRate = FrameRate.FPS_12,
        background = background,
        firstLayerName = "Layer 1",
    )

    @Test
    fun defaultName_isCreatedCountPlusOne() = runTest {
        projects.count.value = 4
        assertEquals("My animation 5", repository().defaults().defaultName)
        projects.count.value = 0
        assertEquals("My animation 1", repository().defaults().defaultName)
    }

    @Test
    fun firstLayerName_isLayer1() = runTest {
        assertEquals("Layer 1", repository().defaults().firstLayerName)
    }

    @Test
    fun createdCount_readsTheCounter() = runTest {
        projects.count.value = 3
        assertEquals(3, repository().createdCount())
    }

    @Test
    fun unknownSettings_fallBackTo916And12() = runTest {
        settings.aspectRatio.value = "4:5"
        settings.frameRate.value = 7
        val defaults = repository().defaults()
        assertEquals(AspectRatio.PORTRAIT_9_16, defaults.aspectRatio)
        assertEquals(FrameRate.FPS_12, defaults.frameRate)

        // Control: known values are used as they are.
        settings.aspectRatio.value = "1:1"
        settings.frameRate.value = 30
        val known = repository().defaults()
        assertEquals(AspectRatio.SQUARE_1_1, known.aspectRatio)
        assertEquals(FrameRate.FPS_30, known.frameRate)
    }

    @Test
    fun allSettingsValues_map() = runTest {
        val ratios = mapOf(
            DeviceSettingsValues.ASPECT_RATIO_16_9 to AspectRatio.LANDSCAPE_16_9,
            DeviceSettingsValues.ASPECT_RATIO_9_16 to AspectRatio.PORTRAIT_9_16,
            DeviceSettingsValues.ASPECT_RATIO_1_1 to AspectRatio.SQUARE_1_1,
        )
        val rates = mapOf(
            6 to FrameRate.FPS_6,
            8 to FrameRate.FPS_8,
            12 to FrameRate.FPS_12,
            15 to FrameRate.FPS_15,
            24 to FrameRate.FPS_24,
            30 to FrameRate.FPS_30,
        )
        assertEquals(ratios.keys, DeviceSettingsValues.ASPECT_RATIOS.toSet())
        assertEquals(rates.keys, DeviceSettingsValues.FRAME_RATES.toSet())
        for (value in DeviceSettingsValues.ASPECT_RATIOS) {
            settings.aspectRatio.value = value
            assertEquals(value, ratios.getValue(value), repository().defaults().aspectRatio)
        }
        for (value in DeviceSettingsValues.FRAME_RATES) {
            settings.frameRate.value = value
            assertEquals("$value", rates.getValue(value), repository().defaults().frameRate)
        }
    }

    @Test
    fun create_success_updatesThumbnail() = runTest {
        val result = repository().create(request())
        val id = (result as ProjectResult.Success).value.id
        assertEquals(listOf(id), thumbnails.updated)
        assertEquals(listOf(request()), projects.created)
    }

    @Test
    fun create_failure_noThumbnail() = runTest {
        projects.result = ProjectResult.Failure(ProjectFailure.NO_SPACE)
        val result = repository().create(request())
        assertEquals(ProjectResult.Failure(ProjectFailure.NO_SPACE), result)
        assertEquals(emptyList<ProjectId>(), thumbnails.updated)
        // Control: the request did reach the project repository.
        assertEquals(1, projects.created.size)
    }

    @Test
    fun create_photo_writesBytesIntoProject() = runTest {
        val bytes = byteArrayOf(4, 5, 6)
        val photo = NewBackground.Photo(bytes, "jpg")
        repository().create(request(photo))
        val received = projects.created.single().background
        assertSame(photo, received)
        assertArrayEquals(bytes, (received as NewBackground.Photo).bytes)
        assertEquals("jpg", received.extension)
    }
}

/** Creates with a fixed id unless [result] is set; records every request. [count] is the counter. */
private class CreatingProjectRepository : ProjectRepository {
    val count = MutableStateFlow(0)
    val created = mutableListOf<NewProject>()
    var result: ProjectResult<Project>? = null

    override val projects: Flow<List<ProjectListItem>> = MutableStateFlow(emptyList())
    override val createdCount: Flow<Int> = count

    override suspend fun create(request: NewProject): ProjectResult<Project> {
        created += request
        result?.let { return it }
        return ProjectResult.Success(FakeNewProjectRepository.projectOf(testProjectId(5), request))
    }

    override suspend fun save(project: Project): ProjectResult<Project> = error("not used")

    override suspend fun rename(id: ProjectId, name: String): ProjectResult<Project> = error("not used")

    override suspend fun duplicate(id: ProjectId): ProjectResult<Project> = error("not used")

    override suspend fun delete(id: ProjectId): ProjectResult<Unit> = error("not used")

    override fun refresh() = Unit
}

private class CountingThumbnails : ProjectThumbnails {
    val updated = mutableListOf<ProjectId>()

    override suspend fun update(id: ProjectId): ThumbnailResult {
        updated += id
        return ThumbnailResult.WRITTEN
    }
}

private class MemorySettings : DeviceSettingsRepository {
    val frameRate = MutableStateFlow(DeviceSettingsValues.DEFAULT_FRAME_RATE)
    val aspectRatio = MutableStateFlow(DeviceSettingsValues.DEFAULT_ASPECT_RATIO)

    override val defaultFrameRate: Flow<Int> = frameRate
    override val defaultAspectRatio: Flow<String> = aspectRatio
    override val stylusPressure: Flow<Boolean> = MutableStateFlow(true)
    override val firstStrokeDrawn: Flow<Boolean> = MutableStateFlow(false)

    override suspend fun setDefaultFrameRate(frameRate: Int) = error("not used")

    override suspend fun setDefaultAspectRatio(aspectRatio: String) = error("not used")

    override suspend fun setStylusPressure(enabled: Boolean) = error("not used")

    override suspend fun markFirstStrokeDrawn(): Boolean = error("not used")
}
