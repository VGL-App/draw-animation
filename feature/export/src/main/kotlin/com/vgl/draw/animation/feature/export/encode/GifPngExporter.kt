package com.vgl.draw.animation.feature.export.encode

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.render.FrameRenderer
import com.vgl.draw.animation.feature.export.model.ExportOptions
import com.vgl.draw.animation.feature.export.model.ExportedFiles
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher

interface GifPngExporter {
    suspend fun export(
        project: Project,
        options: ExportOptions,
        workDir: File,
        onFrameDone: (Int, Int) -> Unit,
    ): ExportedFiles
}

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
    ): ExportedFiles = TODO()
}

internal fun pngFileName(index: Int, count: Int): String = TODO()
