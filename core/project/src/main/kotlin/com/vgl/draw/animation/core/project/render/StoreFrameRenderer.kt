package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.storage.ProjectStore
import kotlinx.coroutines.CoroutineDispatcher

/** [FrameRenderer] reading cells and the background image from [store], one cell in memory at a time. */
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
    ): Bitmap = TODO()

    override suspend fun renderInto(target: Bitmap, project: Project, frameIndex: Int, includeBackground: Boolean) {
        TODO()
    }
}
