package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.model.CameraSettings
import com.vgl.draw.animation.core.project.model.OnionSkinSettings
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.ReferenceLayer
import com.vgl.draw.animation.core.project.model.ReferenceType
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.render.RenderTestImages.alpha
import com.vgl.draw.animation.core.project.render.RenderTestImages.cell
import com.vgl.draw.animation.core.project.render.RenderTestImages.decode
import com.vgl.draw.animation.core.project.render.RenderTestImages.pixels
import com.vgl.draw.animation.core.project.render.RenderTestImages.png
import com.vgl.draw.animation.core.project.storage.FileProjectStore
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectStore
import java.io.File
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class StoreFrameRendererTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val logger = RecordingAppLogger()

    private val root: File by lazy { File(tmp.root, "projects") }

    private val size100 = RenderSize(100, 100)

    private fun TestScope.store(): FileProjectStore =
        FileProjectStore(root, StandardTestDispatcher(testScheduler), logger)

    private fun TestScope.renderer(
        store: ProjectStore,
        logger: RecordingAppLogger = this@StoreFrameRendererTest.logger,
    ): StoreFrameRenderer = StoreFrameRenderer(store, TEST_PALETTE, StandardTestDispatcher(testScheduler), logger)

    private fun RecordingAppLogger.renderWarnings() =
        entries.filter { it.level == LogLevel.WARN && it.tag == "ProjectRender" }

    private suspend fun ProjectStore.writeCell(project: Project, frame: Int, layer: Int, bitmap: Bitmap) =
        writeCell(project, frame, layer, png(bitmap))

    private suspend fun ProjectStore.writeCell(project: Project, frame: Int, layer: Int, bytes: ByteArray) =
        writeFile(project.id, ProjectFile.Cell(project.frames[frame].id, project.layers[layer].id), bytes)

    private fun full720(color: Int) = cell(720, 720, Square(0, 0, 720, color))

    private fun topLeft720(color: Int = BLACK) = cell(720, 720, Square(0, 0, 360, color))

    /** 1:1 720p, white background, 3 layers: bottom all black, middle all red and hidden, top a blue top-left quarter. */
    private suspend fun threeLayerProject(store: ProjectStore, background: Background = solid(BackgroundColor.WHITE)) =
        RenderTestImages.project(background = background, layerCount = 3).let { base ->
            base.copy(layers = base.layers.mapIndexed { index, layer -> layer.copy(visible = index != 1) })
        }.also { project ->
            store.write(project)
            store.writeCell(project, 0, 0, full720(BLACK))
            store.writeCell(project, 0, 1, full720(RED))
            store.writeCell(project, 0, 2, topLeft720(BLUE_INK))
        }

    private fun solid(color: BackgroundColor) = Background.Solid(color)

    private suspend fun assertIllegalArgument(block: suspend () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected.
        }
    }

    @Test
    fun layers_bottomToTop_hiddenSkipped() = runTest {
        val store = store()
        val project = threeLayerProject(store)
        val renderer = renderer(store)

        val bitmap = renderer.render(project, 0, size100, includeBackground = true)
        assertEquals(BLACK, bitmap.getPixel(75, 75))
        assertEquals(BLUE_INK, bitmap.getPixel(25, 25))

        val allVisible = project.copy(layers = project.layers.map { it.copy(visible = true) })
        assertEquals(RED, renderer.render(allVisible, 0, size100, includeBackground = true).getPixel(75, 75))
    }

    @Test
    fun solidBackground_usesPaletteColor() = runTest {
        val store = store()
        val renderer = renderer(store)
        val blue = RenderTestImages.project(background = solid(BackgroundColor.BLUE)).also { store.write(it) }
        assertEquals(TEST_PALETTE.blue, renderer.render(blue, 0, size100, includeBackground = true).getPixel(50, 50))

        val white = RenderTestImages.project(background = solid(BackgroundColor.WHITE)).also { store.write(it) }
        assertEquals(TEST_PALETTE.white, renderer.render(white, 0, size100, includeBackground = true).getPixel(50, 50))
    }

    @Test
    fun withoutBackground_isTransparentOutsideStrokes() = runTest {
        val store = store()
        val project = RenderTestImages.project(background = Background.Photo("png")).also { store.write(it) }
        store.writeFile(project.id, ProjectFile.Background("png"), png(RenderTestImages.threeStripes()))
        store.writeCell(project, 0, 0, topLeft720())

        val bitmap = renderer(store).render(project, 0, size100, includeBackground = false)
        assertEquals(BLACK, bitmap.getPixel(25, 25))
        assertEquals(0, alpha(bitmap.getPixel(75, 75)))
    }

    @Test
    fun photoBackground_readFromProject() = runTest {
        val store = store()
        val project = RenderTestImages.project(background = Background.Photo("png")).also { store.write(it) }
        store.writeFile(project.id, ProjectFile.Background("png"), png(RenderTestImages.threeStripes()))

        val bitmap = renderer(store).render(project, 0, size100, includeBackground = true)
        assertEquals(RED, bitmap.getPixel(5, 50))
        assertEquals(RED, bitmap.getPixel(95, 50))
    }

    @Test
    fun photoBackground_large_rendersCenter() = runTest {
        val store = store()
        val project = RenderTestImages.project(background = Background.Photo("png")).also { store.write(it) }
        store.writeFile(project.id, ProjectFile.Background("png"), png(BackgroundDecoderTest.largeStripes()))

        val bitmap = renderer(store).render(project, 0, size100, includeBackground = true)
        assertEquals(RED, bitmap.getPixel(5, 50))
        assertEquals(RED, bitmap.getPixel(94, 50))
    }

    @Test
    fun photoBackgroundMissing_fallsBackToWhiteAndLogs() = runTest {
        val store = store()
        val project = RenderTestImages.project(background = Background.Photo("png")).also { store.write(it) }

        val missingLogger = RecordingAppLogger()
        val missing = renderer(store, missingLogger).render(project, 0, size100, includeBackground = true)
        assertEquals(TEST_PALETTE.white, missing.getPixel(50, 50))
        assertEquals(1, missingLogger.renderWarnings().size)

        store.writeFile(project.id, ProjectFile.Background("png"), "not a png".toByteArray())
        val brokenLogger = RecordingAppLogger()
        val broken = renderer(store, brokenLogger).render(project, 0, size100, includeBackground = true)
        assertEquals(TEST_PALETTE.white, broken.getPixel(50, 50))
        assertEquals(1, brokenLogger.renderWarnings().size)
    }

    @Test
    fun missingCell_isEmpty() = runTest {
        val store = store()
        val project = RenderTestImages.project(layerCount = 2).also { store.write(it) }
        store.writeCell(project, 0, 0, topLeft720())

        val bitmap = renderer(store).render(project, 0, size100, includeBackground = true)
        assertEquals(BLACK, bitmap.getPixel(25, 25))
        assertEquals(TEST_PALETTE.white, bitmap.getPixel(75, 75))
        assertTrue(logger.entries.none { it.level == LogLevel.WARN })
    }

    @Test
    fun undecodableCell_skippedAndLogged() = runTest {
        val store = store()
        val project = RenderTestImages.project(layerCount = 2).also { store.write(it) }
        store.writeCell(project, 0, 0, topLeft720())
        val renderer = renderer(store)
        val withoutTopCell = pixels(renderer.render(project, 0, size100, includeBackground = true))

        store.writeCell(project, 0, 1, "not a png".toByteArray())
        val bitmap = renderer.render(project, 0, size100, includeBackground = true)
        assertEquals(BLACK, bitmap.getPixel(25, 25))
        assertEquals(TEST_PALETTE.white, bitmap.getPixel(75, 75))
        assertArrayEquals(withoutTopCell, pixels(bitmap))

        val warnings = logger.renderWarnings()
        assertEquals(1, warnings.size)
        val message = warnings.single().message
        val secrets = listOf(project.id.value, project.frames[0].id.value, project.name) +
            project.layers.map { it.id.value }
        secrets.forEach { assertFalse("Log must not contain $it", message.contains(it)) }
    }

    @Test
    fun frameIndex_selectsThatFrame() = runTest {
        val store = store()
        val project = RenderTestImages.project(frameCount = 2).also { store.write(it) }
        store.writeCell(project, 0, 0, topLeft720())
        store.writeCell(project, 1, 0, cell(720, 720, Square(360, 360, 360, BLACK)))
        val renderer = renderer(store)

        val second = renderer.render(project, 1, size100, includeBackground = true)
        assertEquals(BLACK, second.getPixel(75, 75))
        assertEquals(TEST_PALETTE.white, second.getPixel(25, 25))

        val first = renderer.render(project, 0, size100, includeBackground = true)
        assertEquals(BLACK, first.getPixel(25, 25))
        assertEquals(TEST_PALETTE.white, first.getPixel(75, 75))
    }

    @Test
    fun cameraKey_appliedWhenEnabled() = runTest {
        val store = store()
        val base = RenderTestImages.project()
        val project = base.copy(
            frames = listOf(base.frames[0].copy(cameraKey = CameraKey(0.5f, 0f, 1f, 0f))),
            camera = CameraSettings(enabled = true),
        ).also { store.write(it) }
        // 144 of 720 is 20 of 100.
        store.writeCell(project, 0, 0, cell(720, 720, Square(0, 0, 144, BLACK)))
        val renderer = renderer(store)

        val enabled = renderer.render(project, 0, size100, includeBackground = true)
        assertEquals(BLACK, enabled.getPixel(60, 10))
        assertEquals(TEST_PALETTE.white, enabled.getPixel(10, 10))

        val disabledProject = project.copy(camera = CameraSettings(enabled = false))
        val disabled = renderer.render(disabledProject, 0, size100, includeBackground = true)
        assertEquals(BLACK, disabled.getPixel(10, 10))
        assertEquals(TEST_PALETTE.white, disabled.getPixel(60, 10))
    }

    @Test
    fun export720pOf1080pProject_scalesCells() = runTest {
        val store = store()
        val project = RenderTestImages.project(aspectRatio = AspectRatio.LANDSCAPE_16_9, resolution = Resolution.P1080)
            .also { store.write(it) }
        store.writeCell(project, 0, 0, cell(1920, 1080, Square(0, 0, 192, BLACK)))

        val size = RenderSize.forExport(project, Resolution.P720)
        val bitmap = renderer(store).render(project, 0, size, includeBackground = true)
        assertEquals(1280, bitmap.width)
        assertEquals(720, bitmap.height)
        assertEquals(BLACK, bitmap.getPixel(64, 64))
        assertEquals(TEST_PALETTE.white, bitmap.getPixel(200, 200))
    }

    @Test
    fun referenceAndOnionSkin_neverDrawn() = runTest {
        val store = store()
        val base = RenderTestImages.project(frameCount = 2)
        val project = base.copy(
            reference = ReferenceLayer(type = ReferenceType.VIDEO, fileExtension = "png", frameCount = 2),
            onionSkin = OnionSkinSettings(enabled = true),
        ).also { store.write(it) }
        store.writeFile(project.id, ProjectFile.ReferenceImage(0, "png"), png(full720(RED)))
        store.writeFile(project.id, ProjectFile.ReferenceImage(1, "png"), png(full720(RED)))
        store.writeCell(project, 0, 0, full720(GREEN))
        store.writeCell(project, 1, 0, topLeft720())
        val renderer = renderer(store)

        val bitmap = renderer.render(project, 1, size100, includeBackground = true)
        val plain = project.copy(reference = null, onionSkin = OnionSkinSettings(enabled = false))
        assertArrayEquals(pixels(renderer.render(plain, 1, size100, includeBackground = true)), pixels(bitmap))
        assertEquals(TEST_PALETTE.white, bitmap.getPixel(75, 75))
        assertNotEquals(RED, bitmap.getPixel(75, 75))
        assertNotEquals(GREEN, bitmap.getPixel(75, 75))
    }

    @Test
    fun renderInto_clearsPreviousContent() = runTest {
        val store = store()
        val project = RenderTestImages.project(frameCount = 2).also { store.write(it) }
        store.writeCell(project, 0, 0, full720(BLACK))
        val renderer = renderer(store)
        val target = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        renderer.renderInto(target, project, 0, includeBackground = false)
        assertEquals(BLACK, target.getPixel(50, 50))

        renderer.renderInto(target, project, 1, includeBackground = false)
        assertEquals(0, alpha(target.getPixel(50, 50)))
    }

    @Test
    fun composeInMemory_equalsRenderFromStore() = runTest {
        val store = store()
        val stripes = png(RenderTestImages.threeStripes())
        val base = threeLayerProject(store, background = Background.Photo("png"))
        val project = base.copy(
            frames = listOf(base.frames[0].copy(cameraKey = CameraKey(0.1f, 0f, 1.5f, 30f))),
            camera = CameraSettings(enabled = true),
        ).also { store.write(it) }
        store.writeFile(project.id, ProjectFile.Background("png"), stripes)

        val fromStore = renderer(store).render(project, 0, size100, includeBackground = true)

        val inMemory = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val photo = checkNotNull(BackgroundDecoder.decode(stripes, 100, 100))
        val cells = project.layers.filter { it.visible }.map { layer ->
            store.readFile(project.id, ProjectFile.Cell(project.frames[0].id, layer.id))?.let { decode(it) }
        }
        FrameComposer.compose(
            Canvas(inMemory),
            100,
            100,
            FrameBackground.Photo(photo, TEST_PALETTE.white),
            cells,
            cameraAt(project, 0),
        )
        assertArrayEquals(pixels(inMemory), pixels(fromStore))
    }

    @Test
    fun invalidArguments_throw() = runTest {
        val store = store()
        val project = RenderTestImages.project().also { store.write(it) }
        val renderer = renderer(store)

        val immutable = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, false)
        assertIllegalArgument { renderer.renderInto(immutable, project, 0, includeBackground = true) }
        val rgb565 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
        assertIllegalArgument { renderer.renderInto(rgb565, project, 0, includeBackground = true) }
        assertIllegalArgument { renderer.render(project, -1, size100, includeBackground = true) }
        assertIllegalArgument { renderer.render(project, project.frames.size, size100, includeBackground = true) }
    }

    @Test
    fun storeIoError_propagates() = runTest {
        val fileStore = store()
        val project = RenderTestImages.project().also { fileStore.write(it) }
        fileStore.writeCell(project, 0, 0, topLeft720())
        val failing = object : ProjectStore by fileStore {
            override suspend fun readFile(id: ProjectId, file: ProjectFile): ByteArray? =
                if (file is ProjectFile.Cell) throw IOException("disk") else fileStore.readFile(id, file)
        }

        try {
            renderer(failing).render(project, 0, size100, includeBackground = true)
            fail("Expected IOException")
        } catch (e: Exception) {
            assertTrue(e is IOException)
        }
    }

    @Test
    fun render_runsOnRenderDispatcher() = runTest {
        val store = FileProjectStore(root, UnconfinedTestDispatcher(testScheduler), logger)
        val project = RenderTestImages.project(background = solid(BackgroundColor.BLUE)).also { store.write(it) }
        store.writeCell(project, 0, 0, topLeft720())
        // Not a TestDispatcher on another scheduler: runTest rejects that ("Detected use of different schedulers").
        // A dispatcher that only queues shows the same thing: nothing is drawn until its queue runs.
        val render = QueueDispatcher()
        val renderer = StoreFrameRenderer(store, TEST_PALETTE, render, logger)

        val deferred = async { renderer.render(project, 0, size100, includeBackground = true) }
        runCurrent()
        assertFalse(deferred.isCompleted)

        repeat(10) {
            if (!deferred.isCompleted) {
                render.runAll()
                advanceUntilIdle()
            }
        }
        assertTrue(deferred.isCompleted)
        val bitmap = deferred.await()
        assertEquals(BLACK, bitmap.getPixel(25, 25))
        assertEquals(TEST_PALETTE.blue, bitmap.getPixel(75, 75))
    }

    /** Runs nothing until [runAll] is called. */
    private class QueueDispatcher : CoroutineDispatcher() {
        private val queue = ArrayDeque<Runnable>()

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            queue.addLast(block)
        }

        fun runAll() {
            while (queue.isNotEmpty()) queue.removeFirst().run()
        }
    }
}
