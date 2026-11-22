package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Resolution
import org.junit.Assert.assertEquals
import org.junit.Test

class RenderSizeTest {
    @Test
    fun forExport_matchesCanvasSize() {
        assertEquals(
            RenderSize(1920, 1080),
            RenderSize.forExport(RenderTestImages.project(aspectRatio = AspectRatio.LANDSCAPE_16_9), Resolution.P1080),
        )
        assertEquals(
            RenderSize(720, 1280),
            RenderSize.forExport(RenderTestImages.project(aspectRatio = AspectRatio.PORTRAIT_9_16), Resolution.P720),
        )
        assertEquals(
            RenderSize(720, 720),
            RenderSize.forExport(RenderTestImages.project(aspectRatio = AspectRatio.SQUARE_1_1), Resolution.P720),
        )
    }

    @Test
    fun thumbnail_fitsMaxEdgeKeepingRatio() {
        val expected = mapOf(
            AspectRatio.LANDSCAPE_16_9 to RenderSize(512, 288),
            AspectRatio.PORTRAIT_9_16 to RenderSize(288, 512),
            AspectRatio.SQUARE_1_1 to RenderSize(512, 512),
        )
        for ((aspectRatio, size) in expected) {
            for (resolution in Resolution.entries) {
                val project = RenderTestImages.project(aspectRatio = aspectRatio, resolution = resolution)
                assertEquals("$aspectRatio $resolution", size, RenderSize.thumbnail(project))
            }
        }
    }
}
