package com.vgl.draw.animation.feature.export.encode

import android.graphics.Bitmap
import android.graphics.Canvas
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.render.FrameRenderer
import com.vgl.draw.animation.core.project.render.RenderSize
import com.vgl.draw.animation.feature.export.model.ExportFormat
import com.vgl.draw.animation.feature.export.model.ExportOptions
import com.vgl.draw.animation.feature.export.model.ExportedFiles
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** Writes GIF, transparent GIF and PNG sequence files of a project. MP4 has its own exporter. */
interface GifPngExporter {
    /**
     * Writes [options.format] (GIF, PNG_SEQUENCE or TRANSPARENT_GIF) of [project] into [workDir], which must be new or
     * empty. [onFrameDone] is called after each frame with (done, total), on the encoding thread.
     * Cancellation or an error deletes every file written by this call, then rethrows.
     */
    suspend fun export(
        project: Project,
        options: ExportOptions,
        workDir: File,
        onFrameDone: (Int, Int) -> Unit,
    ): ExportedFiles
}

/**
 * Draws each frame with [renderer] into one reused bitmap, adds the watermark when asked, then encodes it. Only reads
 * the project: it has no way to write one. Logs never hold the project's name or id, paths or file contents.
 */
internal class DefaultGifPngExporter(
    private val renderer: FrameRenderer,
    private val watermark: WatermarkPainter,
    private val encodeDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
) : GifPngExporter {
    override suspend fun export(
        project: Project,
        options: ExportOptions,
        workDir: File,
        onFrameDone: (Int, Int) -> Unit,
    ): ExportedFiles {
        require(options.format != ExportFormat.MP4) { "MP4 is not written by this exporter" }
        return withContext(encodeDispatcher) {
            val size = RenderSize.forExport(project, options.resolution)
            if (!workDir.isDirectory && !workDir.mkdirs()) throw IOException("Cannot create the work directory")
            val created = mutableListOf<File>()
            val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
            try {
                val job = FrameJob(project, options, bitmap, onFrameDone)
                when (options.format) {
                    ExportFormat.GIF, ExportFormat.TRANSPARENT_GIF -> writeGif(
                        job,
                        File(workDir, GIF_FILE_NAME),
                        created,
                    )
                    ExportFormat.PNG_SEQUENCE -> writePngs(job, workDir, created)
                    ExportFormat.MP4 -> error("MP4 is not written by this exporter")
                }
                val totalBytes = created.sumOf { it.length() }
                logger.info(
                    TAG,
                    "Export finished: format=${options.format.name}, frames=${project.frames.size}, " +
                        "size=${size.width}x${size.height}, bytes=$totalBytes",
                )
                ExportedFiles(created.toList(), totalBytes)
            } catch (e: Throwable) {
                // Every stream is closed by now, so the files can be deleted.
                created.forEach { it.delete() }
                if (e is CancellationException) {
                    logger.info(
                        TAG,
                        "Export cancelled",
                    )
                } else {
                    logger.warn(TAG, "Export failed", e)
                }
                throw e
            } finally {
                bitmap.recycle()
            }
        }
    }

    private class FrameJob(
        val project: Project,
        val options: ExportOptions,
        val bitmap: Bitmap,
        val onFrameDone: (Int, Int) -> Unit,
    ) {
        val count: Int get() = project.frames.size
    }

    /** Draws frame [index] into the job's bitmap, with the watermark when it is on. */
    private suspend fun drawFrame(job: FrameJob, index: Int) {
        currentCoroutineContext().ensureActive()
        val format = job.options.format
        renderer.renderInto(job.bitmap, job.project, index, format.includesBackground)
        if (job.options.watermark) {
            watermark.draw(
                Canvas(job.bitmap),
                job.bitmap.width,
                job.bitmap.height,
                opaquePill = format == ExportFormat.TRANSPARENT_GIF,
            )
        }
    }

    private suspend fun writeGif(job: FrameJob, file: File, created: MutableList<File>) {
        val width = job.bitmap.width
        val height = job.bitmap.height
        val delays = ExportTimeline(
            job.project.frames.map {
                it.hold
            },
            job.project.frameRate.fps,
        ).gifDelaysCentiseconds()
        val pixels = IntArray(width * height)
        created += file
        BufferedOutputStream(FileOutputStream(file), BUFFER_BYTES).use { out ->
            val encoder = GifEncoder(
                out,
                width,
                height,
                transparent =
                job.options.format == ExportFormat.TRANSPARENT_GIF,
            )
            for (index in 0 until job.count) {
                drawFrame(job, index)
                job.bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                encoder.addFrame(pixels, delays[index])
                job.onFrameDone(index + 1, job.count)
            }
            encoder.finish()
        }
    }

    private suspend fun writePngs(job: FrameJob, workDir: File, created: MutableList<File>) {
        for (index in 0 until job.count) {
            drawFrame(job, index)
            val file = File(workDir, pngFileName(index, job.count))
            created += file
            BufferedOutputStream(FileOutputStream(file), BUFFER_BYTES).use { out ->
                if (!job.bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, out)) {
                    throw IOException("PNG encoding failed")
                }
            }
            job.onFrameDone(index + 1, job.count)
        }
    }

    private companion object {
        const val TAG = "Export"
        const val GIF_FILE_NAME = "animation.gif"
        const val BUFFER_BYTES = 64 * 1024
        const val PNG_QUALITY = 100
    }
}

/**
 * File name of PNG frame [index] (from 0) of [count]: numbered from 1, zero-padded to at least 3 digits and to the
 * digits of [count], so names sort in frame order (`001.png`; `0001.png` with 1 000 frames).
 */
internal fun pngFileName(index: Int, count: Int): String {
    val digits = maxOf(3, count.toString().length)
    return (index + 1).toString().padStart(digits, '0') + ".png"
}
