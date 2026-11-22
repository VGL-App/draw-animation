package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.model.CameraSettings
import com.vgl.draw.animation.core.project.model.Frame
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.Project
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CameraTimelineTest {
    private fun projectWith(enabled: Boolean, vararg frames: Pair<Int, CameraKey?>): Project =
        RenderTestImages.project(frameCount = 1).copy(
            camera = CameraSettings(enabled = enabled),
            frames = frames.map { (hold, key) -> Frame(FrameId.random(), hold = hold, cameraKey = key) },
        )

    private fun assertKey(expected: CameraKey, actual: CameraKey) {
        assertEquals(expected.panX, actual.panX, TOLERANCE)
        assertEquals(expected.panY, actual.panY, TOLERANCE)
        assertEquals(expected.zoom, actual.zoom, TOLERANCE)
        assertEquals(expected.rotationDegrees, actual.rotationDegrees, TOLERANCE)
    }

    private val start = CameraKey(panX = 0f, panY = 0f, zoom = 1f, rotationDegrees = 0f)
    private val end = CameraKey(panX = 0.4f, panY = -0.2f, zoom = 2.0f, rotationDegrees = 90f)

    /** Holds `[1, 1, 3, 1, 1]`, keys on frames 1 and 3: frame start times 0, 1, 2, 5, 6. */
    private fun interpolationProject(): Project =
        projectWith(true, 1 to null, 1 to start, 3 to null, 1 to end, 1 to null)

    @Test
    fun cameraDisabled_returnsOriginEvenWithKeys() {
        val key = CameraKey(panX = 0.3f, panY = 0.1f, zoom = 2.0f, rotationDegrees = 45f)
        val enabled = projectWith(true, 1 to null, 1 to key, 1 to null)
        assertEquals(key, cameraAt(enabled, 1))

        val disabled = enabled.copy(camera = CameraSettings(enabled = false))
        for (index in disabled.frames.indices) {
            assertEquals(CAMERA_ORIGIN, cameraAt(disabled, index))
        }
    }

    @Test
    fun noKeys_returnsOrigin() {
        val project = projectWith(true, 1 to null, 2 to null, 1 to null)
        for (index in project.frames.indices) {
            assertEquals(CAMERA_ORIGIN, cameraAt(project, index))
        }
    }

    @Test
    fun betweenKeys_interpolatesByFrameStartTime() {
        // By index it would be (0.2, -0.1, 1.5, 45); by start time f = (2 - 1) / (5 - 1) = 0.25.
        assertKey(
            CameraKey(panX = 0.1f, panY = -0.05f, zoom = 1.25f, rotationDegrees = 22.5f),
            cameraAt(interpolationProject(), 2),
        )
    }

    @Test
    fun beforeFirstKey_isOrigin_afterLastKey_holdsLast() {
        val project = interpolationProject()
        assertEquals(CAMERA_ORIGIN, cameraAt(project, 0))
        assertEquals(end, cameraAt(project, 4))
    }

    @Test
    fun keyFrame_returnsItsKey() {
        val project = interpolationProject()
        assertEquals(start, cameraAt(project, 1))
        assertEquals(end, cameraAt(project, 3))
    }

    @Test
    fun rotation_interpolatesLinearlyInDegrees() {
        val project = projectWith(
            true,
            1 to CameraKey(panX = 0f, panY = 0f, zoom = 1f, rotationDegrees = 0f),
            1 to null,
            1 to CameraKey(panX = 0f, panY = 0f, zoom = 1f, rotationDegrees = 270f),
        )
        assertEquals(135f, cameraAt(project, 1).rotationDegrees, TOLERANCE)
    }

    @Test
    fun frameIndexOutOfRange_throws() {
        val project = interpolationProject()
        assertThrows(IllegalArgumentException::class.java) { cameraAt(project, -1) }
        assertThrows(IllegalArgumentException::class.java) { cameraAt(project, project.frames.size) }
    }

    private companion object {
        const val TOLERANCE = 1e-5f
    }
}
