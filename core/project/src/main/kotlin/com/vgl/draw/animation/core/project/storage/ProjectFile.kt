package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.SoundClipId

/** A file stored next to `project.json` in a project folder. */
sealed interface ProjectFile {
    /** Path inside the project folder. */
    val relativePath: String

    data object Thumbnail : ProjectFile {
        override val relativePath: String get() = TODO()
    }

    data class Background(val extension: String) : ProjectFile {
        override val relativePath: String get() = TODO()
    }

    /** The content of one layer in one frame. No file means an empty cell. */
    data class Cell(val frameId: FrameId, val layerId: LayerId) : ProjectFile {
        override val relativePath: String get() = TODO()
    }

    data class Sound(val clipId: SoundClipId, val extension: String) : ProjectFile {
        override val relativePath: String get() = TODO()
    }

    /** One image of the reference layer, [index] from 0. */
    data class ReferenceImage(val index: Int, val extension: String) : ProjectFile {
        override val relativePath: String get() = TODO()
    }
}
