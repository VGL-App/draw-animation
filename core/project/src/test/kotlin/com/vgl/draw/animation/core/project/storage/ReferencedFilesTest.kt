package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.TestProjects
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.SoundClipId
import org.junit.Assert.assertEquals
import org.junit.Test

class ReferencedFilesTest {
    private val prefix = "6f1c2a3e-0000-4000-8000-"

    private fun frame(n: Int) = FrameId("${prefix}0000000000b$n")

    private fun layer(n: Int) = LayerId("${prefix}0000000000a$n")

    @Test
    fun fullProject_listsEveryFileInOrder() {
        val expected = listOf(
            ProjectFile.Thumbnail,
            ProjectFile.Background("jpg"),
            ProjectFile.Sound(SoundClipId("${prefix}0000000000c1"), "mp3"),
            ProjectFile.Sound(SoundClipId("${prefix}0000000000c2"), "m4a"),
            ProjectFile.Sound(SoundClipId("${prefix}0000000000c3"), "ogg"),
        ) + (0 until 120).map { ProjectFile.ReferenceImage(it, "jpg") } + listOf(
            ProjectFile.Cell(frame(1), layer(1)),
            ProjectFile.Cell(frame(1), layer(2)),
            ProjectFile.Cell(frame(1), layer(3)),
            ProjectFile.Cell(frame(2), layer(1)),
            ProjectFile.Cell(frame(2), layer(2)),
            ProjectFile.Cell(frame(2), layer(3)),
            ProjectFile.Cell(frame(3), layer(1)),
            ProjectFile.Cell(frame(3), layer(2)),
            ProjectFile.Cell(frame(3), layer(3)),
            ProjectFile.Cell(frame(4), layer(1)),
            ProjectFile.Cell(frame(4), layer(2)),
            ProjectFile.Cell(frame(4), layer(3)),
        )

        assertEquals(expected, TestProjects.full().referencedFiles())
    }

    @Test
    fun solidBackgroundNoReference_hasNoBackgroundOrReference() {
        val project = TestProjects.full().copy(
            background = Background.Solid(BackgroundColor.DARK),
            reference = null,
            sounds = emptyList(),
        )

        val files = project.referencedFiles()

        assertEquals(ProjectFile.Thumbnail, files.first())
        assertEquals(12, files.count { it is ProjectFile.Cell })
        assertEquals(13, files.size)
    }
}
