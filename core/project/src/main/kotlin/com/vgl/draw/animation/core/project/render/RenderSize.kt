package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.Resolution

/** Longest edge of a project thumbnail in pixels; the card shows it at 168 × 94 dp (504 × 282 px at xxhdpi). */
const val THUMBNAIL_MAX_EDGE: Int = 512

/** Size in pixels of a drawn frame. */
data class RenderSize(val width: Int, val height: Int) {
    companion object {
        /** Export size: 720p and 1080p are the short side of the frame, for every aspect ratio. */
        fun forExport(project: Project, resolution: Resolution): RenderSize = TODO()

        /** Fits in a [THUMBNAIL_MAX_EDGE] square keeping the project's aspect ratio. */
        fun thumbnail(project: Project): RenderSize = TODO()
    }
}
