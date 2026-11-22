package com.vgl.draw.animation.core.project.render

import android.graphics.Bitmap
import com.vgl.draw.animation.core.project.model.Project

/**
 * Draws a frame of a saved project: the visible layers bottom to top, over the project background (or transparent),
 * through the virtual camera. The reference layer and onion skin are never drawn; a frame held 2× to 4× is still one
 * image. Throws [IllegalArgumentException] for a wrong frame index or target, and [java.io.IOException] when reading
 * the project files fails.
 */
interface FrameRenderer {
    /** Draws frame [frameIndex] of [project] into a new ARGB_8888 bitmap of [size]. */
    suspend fun render(project: Project, frameIndex: Int, size: RenderSize, includeBackground: Boolean): Bitmap

    /** Same as [render], into [target] (cleared first), so export can reuse one bitmap for every frame. */
    suspend fun renderInto(target: Bitmap, project: Project, frameIndex: Int, includeBackground: Boolean)
}
