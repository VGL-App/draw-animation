package com.vgl.draw.animation.feature.projects.data

import android.graphics.Bitmap
import android.graphics.Color
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.render.ProjectThumbnails
import com.vgl.draw.animation.core.project.render.ThumbnailResult
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectFailure
import com.vgl.draw.animation.core.project.repository.ProjectListItem
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.repository.ProjectResult
import com.vgl.draw.animation.core.project.storage.CorruptReason
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectImport
import com.vgl.draw.animation.core.project.storage.ProjectReadResult
import com.vgl.draw.animation.core.project.storage.ProjectStore
import com.vgl.draw.animation.feature.projects.readableItem
import com.vgl.draw.animation.feature.projects.testProjectId
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * [DefaultProjectsRepository] with a store and thumbnails in memory. NATIVE graphics: the default mode decodes any
 * bytes to a fake bitmap, so broken files and sizes would mean nothing. The IO dispatcher is a
 * [StandardTestDispatcher] on the scheduler of `runTest` itself (L-001).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DefaultProjectsRepositoryTest {
    private val id = testProjectId(1)
    private val store = MemoryStore()
    private val thumbnails = DrawingThumbnails(store)
    private val projects = RecordingProjectRepository()

    private fun TestScope.repository() = DefaultProjectsRepository(
        projectRepository = projects,
        store = store,
        thumbnails = thumbnails,
        ioDispatcher = StandardTestDispatcher(testScheduler),
    )

    @Test
    fun items_comeFromProjectRepository() = runTest {
        val items = listOf(readableItem(1), readableItem(2))
        projects.list.value = items
        assertEquals(items, repository().items.first())
    }

    @Test
    fun thumbnail_readsStoredFile() = runTest {
        store.projects[id] = ProjectReadResult.Ok(readableItem(1).project)
        store.files[id] = png(width = 64, height = 36)
        val image = repository().thumbnail(id, maxWidthPx = 64)
        assertNotNull(image)
        assertEquals(64, image!!.width)
        assertEquals(0, thumbnails.updates)
    }

    @Test
    fun missingThumbnail_updatesOnceThenReads() = runTest {
        store.projects[id] = ProjectReadResult.Ok(readableItem(1).project)
        thumbnails.drawn = png(width = 32, height = 18)
        val image = repository().thumbnail(id, maxWidthPx = 32)
        assertEquals(1, thumbnails.updates)
        assertNotNull(image)
        assertEquals(32, image!!.width)
    }

    @Test
    fun corruptBytes_returnsNull() = runTest {
        store.projects[id] = ProjectReadResult.Ok(readableItem(1).project)
        // Control: a valid file decodes.
        store.files[id] = png(width = 16, height = 9)
        assertNotNull(repository().thumbnail(id, maxWidthPx = 16))

        store.files[id] = byteArrayOf(1, 2, 3, 4, 5)
        assertNull(repository().thumbnail(id, maxWidthPx = 16))

        store.failReads = true
        assertNull(repository().thumbnail(id, maxWidthPx = 16))
    }

    @Test
    fun unreadableProject_doesNotUpdate() = runTest {
        store.projects[id] = ProjectReadResult.Corrupt(name = "Broken", reason = CorruptReason.INVALID_JSON)
        thumbnails.drawn = png(width = 16, height = 9)
        // Control: a readable project without a thumbnail is drawn once.
        val other = testProjectId(2)
        store.projects[other] = ProjectReadResult.Ok(readableItem(2).project)
        assertNotNull(repository().thumbnail(other, maxWidthPx = 16))
        assertEquals(1, thumbnails.updates)

        assertNull(repository().thumbnail(id, maxWidthPx = 16))
        assertEquals(1, thumbnails.updates)
    }

    @Test
    fun thumbnail_downsamplesToRequestedWidth() = runTest {
        store.projects[id] = ProjectReadResult.Ok(readableItem(1).project)
        store.files[id] = png(width = 512, height = 288)
        assertEquals(256, repository().thumbnail(id, maxWidthPx = 200)!!.width)
        // Control: asking for the full width keeps the full width.
        assertEquals(512, repository().thumbnail(id, maxWidthPx = 512)!!.width)
    }

    @Test
    fun rename_delegatesToProjectRepository() = runTest {
        val renamed = readableItem(1, name = "Walk loop").project
        projects.renameResult = ProjectResult.Success(renamed)
        val result = repository().rename(id, "Walk loop")
        assertEquals(listOf(id to "Walk loop"), projects.renameCalls)
        assertEquals(ProjectResult.Success(renamed), result)
        // Control: a failure is returned as it is.
        projects.renameResult = ProjectResult.Failure(ProjectFailure.NO_SPACE)
        assertEquals(ProjectResult.Failure(ProjectFailure.NO_SPACE), repository().rename(id, "Walk loop"))
    }

    @Test
    fun duplicate_delegatesToProjectRepository() = runTest {
        val copy = readableItem(7, name = "Walk cycle copy").project
        projects.duplicateResult = ProjectResult.Success(copy)
        val result = repository().duplicate(id)
        assertEquals(listOf(id), projects.duplicateCalls)
        assertEquals(ProjectResult.Success(copy), result)
        projects.duplicateResult = ProjectResult.Failure(ProjectFailure.NO_SPACE)
        assertEquals(ProjectResult.Failure(ProjectFailure.NO_SPACE), repository().duplicate(id))
    }

    @Test
    fun delete_delegatesToProjectRepository() = runTest {
        projects.deleteResult = ProjectResult.Success(Unit)
        val result = repository().delete(id)
        assertEquals(listOf(id), projects.deleteCalls)
        assertEquals(ProjectResult.Success(Unit), result)
        projects.deleteResult = ProjectResult.Failure(ProjectFailure.NOT_FOUND)
        assertEquals(ProjectResult.Failure(ProjectFailure.NOT_FOUND), repository().delete(id))
    }

    private fun png(width: Int, height: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.rgb(47, 111, 224))
        return ByteArrayOutputStream().also { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }.toByteArray()
    }
}

/** Only the thumbnail and project.json of each project; everything else is not used by the repository. */
private class MemoryStore : ProjectStore {
    val projects = mutableMapOf<ProjectId, ProjectReadResult>()
    val files = mutableMapOf<ProjectId, ByteArray>()
    var failReads = false

    override suspend fun ids(): List<ProjectId> = projects.keys.toList()

    override suspend fun read(id: ProjectId): ProjectReadResult = projects[id] ?: ProjectReadResult.NotFound

    override suspend fun readFile(id: ProjectId, file: ProjectFile): ByteArray? {
        if (failReads) throw IOException("read failed")
        return if (file == ProjectFile.Thumbnail) files[id] else null
    }

    override suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray) {
        if (file == ProjectFile.Thumbnail) files[id] = bytes
    }

    override suspend fun write(project: Project) = error("not used")

    override suspend fun deleteFile(id: ProjectId, file: ProjectFile) = error("not used")

    override suspend fun delete(id: ProjectId) = error("not used")

    override suspend fun deleteIncomplete(): Int = error("not used")

    override suspend fun beginImport(): ProjectImport = error("not used")

    override suspend fun deleteAbandonedImports(): Int = error("not used")
}

/** Writes [drawn] as the thumbnail when the project is readable, like the real one; counts calls. */
private class DrawingThumbnails(private val store: MemoryStore) : ProjectThumbnails {
    var drawn: ByteArray? = null
    var updates = 0

    override suspend fun update(id: ProjectId): ThumbnailResult {
        updates++
        if (store.read(id) !is ProjectReadResult.Ok) return ThumbnailResult.PROJECT_UNREADABLE
        val bytes = drawn ?: return ThumbnailResult.FAILED
        store.writeFile(id, ProjectFile.Thumbnail, bytes)
        return ThumbnailResult.WRITTEN
    }
}

/** The list, and rename, duplicate, delete recording their arguments and returning the preset result. */
private class RecordingProjectRepository : ProjectRepository {
    val list = MutableStateFlow<List<ProjectListItem>>(emptyList())
    val renameCalls = mutableListOf<Pair<ProjectId, String>>()
    val duplicateCalls = mutableListOf<ProjectId>()
    val deleteCalls = mutableListOf<ProjectId>()
    var renameResult: ProjectResult<Project> = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
    var duplicateResult: ProjectResult<Project> = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)
    var deleteResult: ProjectResult<Unit> = ProjectResult.Failure(ProjectFailure.WRITE_FAILED)

    override val projects: Flow<List<ProjectListItem>> = list
    override val createdCount: Flow<Int> = MutableStateFlow(0)

    override suspend fun create(request: NewProject): ProjectResult<Project> = error("not used")

    override suspend fun save(project: Project): ProjectResult<Project> = error("not used")

    override suspend fun rename(id: ProjectId, name: String): ProjectResult<Project> {
        renameCalls += id to name
        return renameResult
    }

    override suspend fun duplicate(id: ProjectId): ProjectResult<Project> {
        duplicateCalls += id
        return duplicateResult
    }

    override suspend fun delete(id: ProjectId): ProjectResult<Unit> {
        deleteCalls += id
        return deleteResult
    }

    override fun refresh() = Unit
}
