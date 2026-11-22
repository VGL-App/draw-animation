package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.render.RenderTestImages.cell
import com.vgl.draw.animation.core.project.render.RenderTestImages.decode
import com.vgl.draw.animation.core.project.render.RenderTestImages.png
import com.vgl.draw.animation.core.project.storage.FileProjectStore
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectStore
import java.io.File
import java.io.IOException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ProjectThumbnailsTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val logger = RecordingAppLogger()

    private val root: File by lazy { File(tmp.root, "projects") }

    private fun TestScope.store(): FileProjectStore =
        FileProjectStore(root, StandardTestDispatcher(testScheduler), logger)

    private fun TestScope.thumbnails(store: ProjectStore): DefaultProjectThumbnails = DefaultProjectThumbnails(
        store,
        StoreFrameRenderer(store, TEST_PALETTE, StandardTestDispatcher(testScheduler), logger),
        logger,
    )

    private suspend fun ProjectStore.writeCell(project: Project, frame: Int, bitmap: Bitmap) =
        writeFile(project.id, ProjectFile.Cell(project.frames[frame].id, project.layers[0].id), png(bitmap))

    private suspend fun ProjectStore.thumbnail(id: ProjectId): Bitmap? =
        readFile(id, ProjectFile.Thumbnail)?.let { decode(it) }

    /** 16:9 720p (1280 × 720), blue background; frame 0 has a black 640 square top left, frame 1 is all red. */
    private suspend fun landscapeProject(store: ProjectStore): Project = RenderTestImages.project(
        aspectRatio = AspectRatio.LANDSCAPE_16_9,
        background = Background.Solid(BackgroundColor.BLUE),
        frameCount = 2,
    ).also { project ->
        store.write(project)
        store.writeCell(project, 0, cell(1280, 720, Square(0, 0, 640, BLACK)))
        store.writeCell(project, 1, cell(1280, 720, Square(0, 0, 1280, RED)))
    }

    @Test
    fun update_writesFirstFrameWithBackground() = runTest {
        val store = store()
        val project = landscapeProject(store)

        assertEquals(ThumbnailResult.WRITTEN, thumbnails(store).update(project.id))

        val thumbnail = checkNotNull(store.thumbnail(project.id))
        assertEquals(512, thumbnail.width)
        assertEquals(288, thumbnail.height)
        // The 640 square becomes 256 × 256.
        assertEquals(BLACK, thumbnail.getPixel(100, 144))
        assertEquals(TEST_PALETTE.blue, thumbnail.getPixel(400, 144))
        assertNotEquals(RED, thumbnail.getPixel(100, 144))
        assertNotEquals(RED, thumbnail.getPixel(400, 144))
    }

    @Test
    fun update_sizeFollowsAspectRatio() = runTest {
        val store = store()
        val thumbnails = thumbnails(store)
        val expected = mapOf(AspectRatio.PORTRAIT_9_16 to (288 to 512), AspectRatio.SQUARE_1_1 to (512 to 512))
        for ((aspectRatio, size) in expected) {
            val project = RenderTestImages.project(aspectRatio = aspectRatio).also { store.write(it) }
            assertEquals(ThumbnailResult.WRITTEN, thumbnails.update(project.id))
            val thumbnail = checkNotNull(store.thumbnail(project.id))
            assertEquals("$aspectRatio", size, thumbnail.width to thumbnail.height)
        }
    }

    @Test
    fun update_again_reflectsNewCells() = runTest {
        val store = store()
        val project = landscapeProject(store)
        val thumbnails = thumbnails(store)
        assertEquals(ThumbnailResult.WRITTEN, thumbnails.update(project.id))
        assertEquals(TEST_PALETTE.blue, checkNotNull(store.thumbnail(project.id)).getPixel(400, 144))

        store.writeCell(project, 0, cell(1280, 720, Square(0, 0, 1280, GREEN)))
        assertEquals(ThumbnailResult.WRITTEN, thumbnails.update(project.id))
        assertEquals(GREEN, checkNotNull(store.thumbnail(project.id)).getPixel(400, 144))
    }

    @Test
    fun update_unreadableProject_writesNothing() = runTest {
        val store = store()
        val thumbnails = thumbnails(store)

        val valid = RenderTestImages.project().also { store.write(it) }
        assertEquals(ThumbnailResult.WRITTEN, thumbnails.update(valid.id))
        assertNotNull(store.readFile(valid.id, ProjectFile.Thumbnail))

        val corrupt = RenderTestImages.project().also { store.write(it) }
        File(File(root, corrupt.id.value), "project.json").writeText("{")
        assertEquals(ThumbnailResult.PROJECT_UNREADABLE, thumbnails.update(corrupt.id))
        assertNull(store.readFile(corrupt.id, ProjectFile.Thumbnail))

        val unknown = ProjectId.random()
        assertEquals(ThumbnailResult.PROJECT_UNREADABLE, thumbnails.update(unknown))
        assertNull(store.readFile(unknown, ProjectFile.Thumbnail))
    }

    @Test
    fun update_writeFails_returnsFailedAndLogs() = runTest {
        val fileStore = store()
        val project = landscapeProject(fileStore)
        val failing = object : ProjectStore by fileStore {
            override suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray) =
                throw IOException("disk full")
        }

        assertEquals(ThumbnailResult.FAILED, thumbnails(failing).update(project.id))

        val warnings = logger.entries.filter { it.level == LogLevel.WARN && it.tag == "ProjectRender" }
        assertEquals(1, warnings.size)
        assertFalse(warnings.single().message.contains(project.id.value))
    }
}
