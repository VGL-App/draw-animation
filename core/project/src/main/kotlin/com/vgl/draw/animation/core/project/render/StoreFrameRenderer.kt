package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * [FrameRenderer] reading cells and the background image from [store], one cell in memory at a time (a 1080 × 1920
 * cell is about 8 MB). Never reads the reference layer, onion skin or sounds. Errors of the store reach the caller;
 * a missing cell is an empty cell, an undecodable cell is skipped, a missing or undecodable background image falls
 * back to the white of [palette]. Logs never hold project, frame or layer ids, names or file contents (ADR-0019).
 */
internal class StoreFrameRenderer(
    private val store: ProjectStore,
    private val palette: BackgroundPalette,
    private val renderDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
) : FrameRenderer {
    override suspend fun render(
        project: Project,
        frameIndex: Int,
        size: RenderSize,
        includeBackground: Boolean,
    ): Bitmap {
        requireFrameIndex(project, frameIndex)
        val target = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        renderInto(target, project, frameIndex, includeBackground)
        return target
    }

    override suspend fun renderInto(target: Bitmap, project: Project, frameIndex: Int, includeBackground: Boolean) {
        requireFrameIndex(project, frameIndex)
        require(target.isMutable) { "Target bitmap is not mutable" }
        require(target.config == Bitmap.Config.ARGB_8888) { "Target bitmap is not ARGB_8888" }
        withContext(renderDispatcher) {
            target.eraseColor(Color.TRANSPARENT)
            val width = target.width
            val height = target.height
            val canvas = Canvas(target)
            val camera = cameraAt(project, frameIndex)
            if (includeBackground) drawBackground(canvas, width, height, project, camera)
            val frame = project.frames[frameIndex]
            project.layers.filter { it.visible }.forEach { layer ->
                val bytes = store.readFile(project.id, ProjectFile.Cell(frame.id, layer.id)) ?: return@forEach
                val cell = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (cell == null) {
                    logger.warn(TAG, "Cell image unreadable")
                    return@forEach
                }
                FrameComposer.drawLayer(canvas, width, height, cell, camera)
                cell.recycle()
            }
        }
    }

    private suspend fun drawBackground(canvas: Canvas, width: Int, height: Int, project: Project, camera: CameraKey) {
        when (val background = project.background) {
            is Background.Solid -> FrameComposer.drawSolid(canvas, palette.colorOf(background.color))
            is Background.Photo -> {
                val bytes = store.readFile(project.id, ProjectFile.Background(background.fileExtension))
                val photo = bytes?.let { BackgroundDecoder.decode(it, width, height) }
                if (photo == null) {
                    FrameComposer.drawSolid(canvas, palette.white)
                    logger.warn(TAG, "Background image unreadable")
                } else {
                    FrameComposer.drawPhoto(canvas, width, height, photo, palette.white, camera)
                    photo.recycle()
                }
            }
        }
    }

    private fun requireFrameIndex(project: Project, frameIndex: Int) {
        require(frameIndex in project.frames.indices) { "Frame index out of range" }
    }

    private companion object {
        const val TAG = "ProjectRender"
    }
}
