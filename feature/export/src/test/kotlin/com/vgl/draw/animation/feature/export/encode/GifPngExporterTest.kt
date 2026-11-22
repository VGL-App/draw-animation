package com.vgl.draw.animation.feature.export.encode

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.feature.export.model.ExportFormat
import com.vgl.draw.animation.feature.export.model.ExportOptions
import com.vgl.draw.animation.feature.export.model.ExportedFiles
import java.io.File
import java.io.IOException
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GifPngExporterTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val watermark: WatermarkPainter by lazy { WatermarkPainter.create(context) }
    private val logger = RecordingAppLogger()

    @Test
    fun pngFileName_padsToCount() {
        assertEquals("001.png", pngFileName(0, 3))
        assertEquals("048.png", pngFileName(47, 48))
        assertEquals("1000.png", pngFileName(999, 1000))
        assertEquals("0001.png", pngFileName(0, 1000))
    }

    @Test
    fun gif_oneFramePerProjectFrame_delaysFollowHolds() = runTest {
        val renderer = FakeFrameRenderer()
        val project = exportTestProject(listOf(1, 3, 1, 2))
        val dir = workDir()

        val result = exporter(renderer).export(project, options(ExportFormat.GIF), dir) { _, _ -> }

        assertEquals(listOf("animation.gif"), result.files.map { it.name })
        assertEquals(listOf("animation.gif"), dir.list()!!.toList())
        val gif = GifTestReader.read(result.files.single().readBytes())
        assertEquals(4, gif.frames.size)
        assertEquals(listOf(8, 25, 9, 16), gif.frames.map { it.delayTime })
        gif.frames.forEachIndexed { k, frame ->
            assertEquals("frame $k width", 720, frame.image.width)
            assertEquals("frame $k height", 720, frame.image.height)
            assertEquals("frame $k", FRAME_COLORS[k], frame.argb(360, 180))
        }
        assertEquals(
            listOf(0, 1, 2, 3).map { RenderCall(project, it, includeBackground = true, width = 720, height = 720) },
            renderer.calls,
        )
    }

    @Test
    fun gif_portrait1080p_size() = runTest {
        val project = exportTestProject(listOf(1), aspectRatio = AspectRatio.PORTRAIT_9_16)

        val result = exporter(FakeFrameRenderer())
            .export(project, options(ExportFormat.GIF, resolution = Resolution.P1080), workDir()) { _, _ -> }

        val image = GifTestReader.read(result.files.single().readBytes()).frames.single().image
        assertEquals(1080, image.width)
        assertEquals(1920, image.height)
    }

    @Test
    fun gif_firstFrameDecodesWithBitmapFactory() = runTest {
        val project = exportTestProject(listOf(1, 1))

        val result = exporter(FakeFrameRenderer()).export(project, options(ExportFormat.GIF), workDir()) { _, _ -> }

        val bitmap = BitmapFactory.decodeFile(result.files.single().absolutePath)
        assertNotNull("Skia could not decode the GIF", bitmap)
        assertEquals(RED, bitmap.getPixel(360, 180))
        bitmap.recycle()
    }

    @Test
    fun transparentGif_backgroundOmitted() = runTest {
        val renderer = FakeFrameRenderer()
        val project = exportTestProject(listOf(1, 1))

        val result = exporter(renderer).export(project, options(ExportFormat.TRANSPARENT_GIF), workDir()) { _, _ -> }

        assertTrue(renderer.calls.isNotEmpty())
        assertTrue(renderer.calls.none { it.includeBackground })
        val frame = GifTestReader.read(result.files.single().readBytes()).frames[0]
        assertEquals(0, alpha(frame.argb(5, 5)))
        assertEquals(RED, frame.argb(270, 270))
        assertTrue(frame.transparentColorFlag)
    }

    @Test
    fun pngSequence_onePngPerFrame_transparent() = runTest {
        val renderer = FakeFrameRenderer()
        val project = exportTestProject(listOf(1, 4, 2))
        val dir = workDir()

        val result = exporter(renderer).export(project, options(ExportFormat.PNG_SEQUENCE), dir) { _, _ -> }

        assertEquals(listOf("001.png", "002.png", "003.png"), result.files.map { it.name })
        assertEquals(setOf("001.png", "002.png", "003.png"), dir.list()!!.toSet())
        assertTrue(renderer.calls.none { it.includeBackground })
        result.files.forEachIndexed { k, file ->
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            assertEquals(720, bitmap.width)
            assertEquals(720, bitmap.height)
            assertEquals("frame $k corner", 0, alpha(bitmap.getPixel(5, 5)))
            assertEquals("frame $k square", FRAME_COLORS[k], bitmap.getPixel(270, 270))
            bitmap.recycle()
        }
        assertEquals(result.files.sumOf { it.length() }, result.totalBytes)
    }

    @Test
    fun watermarkOn_everyFrameHasIt() = runTest {
        val gif = pixelsAtPill(ExportFormat.GIF, watermark = true)
        gif.forEachIndexed { k, pixel ->
            val frame = FRAME_COLORS[k]
            val message = "GIF frame $k: #%08X".format(pixel)
            assertEquals(message, 255, alpha(pixel))
            assertTrue(message, abs(red(pixel) - (0.65 * red(frame)).roundToInt()) <= 3)
            assertTrue(message, abs(green(pixel) - (0.65 * green(frame)).roundToInt()) <= 3)
            assertTrue(message, abs(blue(pixel) - (0.65 * blue(frame)).roundToInt()) <= 3)
        }

        val transparentGif = pixelsAtPill(ExportFormat.TRANSPARENT_GIF, watermark = true)
        transparentGif.forEachIndexed { k, pixel ->
            assertEquals("transparent GIF frame $k", 0xFF000000.toInt(), pixel)
        }

        val png = pixelsAtPill(ExportFormat.PNG_SEQUENCE, watermark = true)
        png.forEachIndexed { k, pixel ->
            assertTrue("PNG frame $k: #%08X".format(pixel), abs(alpha(pixel) - 89) <= 1)
            assertEquals("PNG frame $k", 0, pixel and 0xFFFFFF)
        }
    }

    @Test
    fun watermarkOff_noFrameHasIt() = runTest {
        val gif = pixelsAtPill(ExportFormat.GIF, watermark = false)
        assertEquals(FRAME_COLORS.take(3), gif)

        val transparentGif = pixelsAtPill(ExportFormat.TRANSPARENT_GIF, watermark = false)
        assertEquals(listOf(0, 0, 0), transparentGif.map { alpha(it) })

        val png = pixelsAtPill(ExportFormat.PNG_SEQUENCE, watermark = false)
        assertEquals(listOf(0, 0, 0), png.map { alpha(it) })
    }

    @Test
    fun progress_reportsEachFrameInOrder() = runTest {
        for (format in listOf(ExportFormat.GIF, ExportFormat.PNG_SEQUENCE)) {
            val progress = mutableListOf<Pair<Int, Int>>()

            exporter(FakeFrameRenderer()).export(exportTestProject(listOf(1, 1, 1, 1)), options(format), workDir()) {
                    done,
                    total,
                ->
                progress += done to total
            }

            assertEquals("$format", listOf(1 to 4, 2 to 4, 3 to 4, 4 to 4), progress)
        }
    }

    @Test
    fun cancel_deletesPartialFiles() = runTest {
        for (format in listOf(ExportFormat.GIF, ExportFormat.PNG_SEQUENCE)) {
            val renderer = FakeFrameRenderer()
            val dir = workDir()
            var filesBeforeCancel = -1
            lateinit var job: Job
            job = launch {
                exporter(renderer).export(exportTestProject(List(5) { 1 }), options(format), dir) { done, _ ->
                    if (done == 1) filesBeforeCancel = dir.list()!!.size
                    if (done == 2) job.cancel()
                }
            }

            job.join()

            assertTrue("$format", job.isCancelled)
            assertTrue("$format: control, a file existed before cancelling", filesBeforeCancel >= 1)
            assertEquals("$format", emptyList<String>(), dir.list()!!.toList())
            assertTrue("$format: ${renderer.calls.size} frames rendered", renderer.calls.size <= 3)
            assertTrue(
                "$format",
                logger.entries.any {
                    it.level == LogLevel.INFO && it.tag == "Export" && it.message == "Export cancelled"
                },
            )
        }
    }

    @Test
    fun renderError_deletesPartialFilesAndRethrows() = runTest {
        for (format in listOf(ExportFormat.GIF, ExportFormat.PNG_SEQUENCE)) {
            val dir = workDir().apply { mkdirs() }
            File(dir, "keep.txt").writeText("kept")

            val error = runCatching {
                exporter(FakeFrameRenderer(failAt = 2)).export(exportTestProject(List(4) { 1 }), options(format), dir) {
                        _,
                        _,
                    ->
                }
            }.exceptionOrNull()

            assertTrue("$format: $error", error is IOException)
            assertEquals("$format", setOf("keep.txt"), dir.list()!!.toSet())
        }
        assertTrue(logger.entries.any { it.level == LogLevel.WARN && it.tag == "Export" })
    }

    @Test
    fun mp4_rejectedBeforeRendering() = runTest {
        val renderer = FakeFrameRenderer()
        val dir = workDir()

        try {
            exporter(renderer).export(exportTestProject(listOf(1)), options(ExportFormat.MP4), dir) { _, _ -> }
            fail("MP4 was accepted")
        } catch (e: IllegalArgumentException) {
            // Expected: MP4 is written by another exporter.
        }

        assertTrue(renderer.calls.isEmpty())
        assertTrue(dir.list().isNullOrEmpty())
    }

    @Test
    fun export_onlyReadsProject() = runTest {
        val renderer = FakeFrameRenderer()
        val project = exportTestProject(listOf(1, 2, 1))
        val before = project.copy()

        exporter(renderer).export(project, options(ExportFormat.GIF, watermark = true), workDir()) { _, _ -> }

        assertEquals(before, project)
        assertEquals(3, renderer.calls.size)
        assertTrue(renderer.calls.all { it.project === project })
    }

    @Test
    fun logs_finishWithoutProjectData() = runTest {
        val project = exportTestProject(listOf(1, 1, 1, 1))
        val dir = workDir()

        exporter(FakeFrameRenderer()).export(project, options(ExportFormat.GIF), dir) { _, _ -> }

        val finished = logger.entries.filter {
            it.level == LogLevel.INFO && it.tag == "Export" && "format=GIF" in it.message && "frames=4" in it.message
        }
        assertEquals(logger.entries.toString(), 1, finished.size)
        for (entry in logger.entries) {
            assertFalse(entry.message, "Secret name" in entry.message)
            assertFalse(entry.message, project.id.value in entry.message)
            assertFalse(entry.message, dir.absolutePath in entry.message)
        }
    }

    private fun TestScope.exporter(renderer: FakeFrameRenderer): GifPngExporter =
        DefaultGifPngExporter(renderer, watermark, StandardTestDispatcher(testScheduler), logger)

    private fun options(
        format: ExportFormat,
        resolution: Resolution = Resolution.P720,
        watermark: Boolean = false,
    ): ExportOptions = ExportOptions(format, resolution, watermark)

    private var workDirs = 0

    // A new, not yet created directory for every export, as the caller of the exporter guarantees.
    private fun workDir(): File = File(tmp.root, "work${workDirs++}")

    /** Exports 3 frames of a 720 × 720 project and reads each frame at the pill point of the watermark. */
    private suspend fun TestScope.pixelsAtPill(format: ExportFormat, watermark: Boolean): List<Int> {
        val project: Project = exportTestProject(listOf(1, 1, 1))
        val result: ExportedFiles =
            exporter(FakeFrameRenderer()).export(project, options(format, watermark = watermark), workDir()) { _, _ ->
            }
        val (x, y) = pillPoint(this@GifPngExporterTest.watermark.pillBounds(720, 720))
        return when (format) {
            ExportFormat.PNG_SEQUENCE -> result.files.map { file ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                bitmap.getPixel(x, y).also { bitmap.recycle() }
            }
            else -> GifTestReader.read(result.files.single().readBytes()).frames.map { it.argb(x, y) }
        }
    }
}
