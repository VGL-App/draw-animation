package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.SoundClipId
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ProjectFileTest {
    private val frameId = FrameId("6f1c2a3e-0000-4000-8000-0000000000b1")
    private val layerId = LayerId("6f1c2a3e-0000-4000-8000-0000000000a1")
    private val clipId = SoundClipId("6f1c2a3e-0000-4000-8000-0000000000c1")

    @Test
    fun relativePath_eachKind() {
        assertEquals(
            "cells/6f1c2a3e-0000-4000-8000-0000000000b1/6f1c2a3e-0000-4000-8000-0000000000a1.png",
            ProjectFile.Cell(frameId, layerId).relativePath,
        )
        assertEquals("sounds/6f1c2a3e-0000-4000-8000-0000000000c1.m4a", ProjectFile.Sound(clipId, "m4a").relativePath)
        assertEquals("reference/0.jpg", ProjectFile.ReferenceImage(0, "jpg").relativePath)
        assertEquals("background.png", ProjectFile.Background("png").relativePath)
        assertEquals("thumbnail.png", ProjectFile.Thumbnail.relativePath)
    }

    @Test
    fun invalidExtensionOrIndex_throws() {
        val badExtensions = listOf("../x", "", "PNG", "abcdef")
        val makers: List<Pair<String, (String) -> ProjectFile>> = listOf(
            "Background" to { ext -> ProjectFile.Background(ext) },
            "Sound" to { ext -> ProjectFile.Sound(clipId, ext) },
            "ReferenceImage" to { ext -> ProjectFile.ReferenceImage(0, ext) },
        )
        makers.forEach { (kind, make) ->
            // Control: a valid extension is accepted.
            make("png")
            badExtensions.forEach { ext -> assertThrowsIllegalArgument("$kind(\"$ext\")") { make(ext) } }
        }
        assertThrowsIllegalArgument("ReferenceImage(-1)") { ProjectFile.ReferenceImage(-1, "jpg") }
    }

    private fun assertThrowsIllegalArgument(label: String, block: () -> Unit) {
        try {
            block()
            fail("$label was accepted")
        } catch (expected: IllegalArgumentException) {
            // Rejected as required.
        }
    }
}
