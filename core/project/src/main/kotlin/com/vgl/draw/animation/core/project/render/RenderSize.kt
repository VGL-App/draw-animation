package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.model.canvasSize

/** Longest edge of a project thumbnail in pixels; the card shows it at 168 × 94 dp (504 × 282 px at xxhdpi). */
const val THUMBNAIL_MAX_EDGE: Int = 512

/** Size in pixels of a drawn frame. */
data class RenderSize(val width: Int, val height: Int) {
    companion object {
        /** Export size: 720p and 1080p are the short side of the frame, for every aspect ratio. */
        fun forExport(project: Project, resolution: Resolution): RenderSize =
            canvasSize(project.aspectRatio, resolution).let { RenderSize(it.width, it.height) }

        /** Fits in a [THUMBNAIL_MAX_EDGE] square keeping the project's aspect ratio. */
        fun thumbnail(project: Project): RenderSize {
            // Any resolution gives the ratio; the result does not depend on the project's resolution.
            val canvas = canvasSize(project.aspectRatio, Resolution.P720)
            val long = maxOf(canvas.width, canvas.height)
            return RenderSize(
                width = canvas.width * THUMBNAIL_MAX_EDGE / long,
                height = canvas.height * THUMBNAIL_MAX_EDGE / long,
            )
        }
    }
}
