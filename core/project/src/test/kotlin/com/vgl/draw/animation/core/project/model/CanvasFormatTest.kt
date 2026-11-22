package com.vgl.draw.animation.core.project.model

import com.vgl.draw.animation.core.project.storage.ProjectFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CanvasFormatTest {
    @Test
    fun canvasSize_matchesSpec() {
        assertEquals(CanvasSize(1920, 1080), canvasSize(AspectRatio.LANDSCAPE_16_9, Resolution.P1080))
        assertEquals(CanvasSize(1080, 1920), canvasSize(AspectRatio.PORTRAIT_9_16, Resolution.P1080))
        assertEquals(CanvasSize(1080, 1080), canvasSize(AspectRatio.SQUARE_1_1, Resolution.P1080))
        assertEquals(CanvasSize(1280, 720), canvasSize(AspectRatio.LANDSCAPE_16_9, Resolution.P720))
        assertEquals(CanvasSize(720, 1280), canvasSize(AspectRatio.PORTRAIT_9_16, Resolution.P720))
        assertEquals(CanvasSize(720, 720), canvasSize(AspectRatio.SQUARE_1_1, Resolution.P720))
    }

    @Test
    fun create_hasOneFrameOneLayerAndDefaults() {
        val id = ProjectId.random()
        val project = Project.create(
            id = id,
            name = "My animation 1",
            aspectRatio = AspectRatio.PORTRAIT_9_16,
            resolution = Resolution.P1080,
            frameRate = FrameRate.FPS_12,
            background = Background.Solid(BackgroundColor.WHITE),
            firstLayerName = "Layer 1",
            nowMillis = 1_000L,
        )

        assertEquals(id, project.id)
        assertEquals("My animation 1", project.name)
        assertEquals(AspectRatio.PORTRAIT_9_16, project.aspectRatio)
        assertEquals(Resolution.P1080, project.resolution)
        assertEquals(FrameRate.FPS_12, project.frameRate)
        assertEquals(Background.Solid(BackgroundColor.WHITE), project.background)
        assertEquals(1, project.frames.size)
        assertEquals(1, project.frames.single().hold)
        assertNull(project.frames.single().cameraKey)
        assertEquals(1, project.layers.size)
        assertEquals("Layer 1", project.layers.single().name)
        assertTrue(project.layers.single().visible)
        assertTrue(project.sounds.isEmpty())
        assertFalse(project.camera.enabled)
        assertEquals(
            OnionSkinSettings(
                enabled = false,
                framesBefore = 2,
                framesAfter = 1,
                opacityPercent = 45,
                beforeColorArgb = null,
                afterColorArgb = null,
                loop = false,
            ),
            project.onionSkin,
        )
        assertNull(project.reference)
        assertFalse(project.extraLayersUnlocked)
        assertEquals(1_000L, project.createdAtMillis)
        assertEquals(1_000L, project.updatedAtMillis)
        assertEquals(ProjectKind.Regular, project.kind)
        assertTrue(project.isUserProject)
        assertNull(ProjectFormat.validate(project))

        // Control: a lesson project is not a user project.
        val lesson = Project.create(
            id = ProjectId.random(),
            name = "Lesson",
            aspectRatio = AspectRatio.SQUARE_1_1,
            resolution = Resolution.P720,
            frameRate = FrameRate.FPS_8,
            background = Background.Solid(BackgroundColor.BLUE),
            firstLayerName = "Layer 1",
            nowMillis = 2_000L,
            kind = ProjectKind.Lesson(lessonId = "walk-cycle", step = 0),
        )
        assertFalse(lesson.isUserProject)
        assertNull(ProjectFormat.validate(lesson))
    }
}
