package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.SoundClipId

/**
 * A file stored next to `project.json` in a project folder. Ids are checked by their own classes; extensions must
 * match `[a-z0-9]{1,5}`, so a path can never leave the project folder.
 */
sealed interface ProjectFile {
    /** Path inside the project folder. */
    val relativePath: String

    data object Thumbnail : ProjectFile {
        override val relativePath: String get() = "thumbnail.png"
    }

    data class Background(val extension: String) : ProjectFile {
        init {
            requireFileExtension(extension)
        }

        override val relativePath: String get() = "background.$extension"
    }

    /** The content of one layer in one frame. No file means an empty cell. */
    data class Cell(val frameId: FrameId, val layerId: LayerId) : ProjectFile {
        override val relativePath: String get() = "cells/${frameId.value}/${layerId.value}.png"
    }

    data class Sound(val clipId: SoundClipId, val extension: String) : ProjectFile {
        init {
            requireFileExtension(extension)
        }

        override val relativePath: String get() = "sounds/${clipId.value}.$extension"
    }

    /** One image of the reference layer, [index] from 0. */
    data class ReferenceImage(val index: Int, val extension: String) : ProjectFile {
        init {
            require(index >= 0) { "Negative reference image index" }
            requireFileExtension(extension)
        }

        override val relativePath: String get() = "reference/$index.$extension"
    }
}

private val FILE_EXTENSION = Regex("[a-z0-9]{1,5}")

internal fun isValidFileExtension(extension: String): Boolean = FILE_EXTENSION.matches(extension)

private fun requireFileExtension(extension: String) {
    require(isValidFileExtension(extension)) { "Invalid file extension" }
}
