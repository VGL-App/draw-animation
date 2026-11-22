package com.vgl.draw.animation.core.project.model

import java.util.UUID
import kotlinx.serialization.Serializable

/**
 * Identifies a project; also the name of its folder on disk. Always a lowercase canonical UUID, so an id never holds
 * `/`, `..` or upper case. Decoding a file with another value gives `Corrupt`.
 */
@JvmInline
@Serializable
value class ProjectId(val value: String) {
    init {
        requireCanonicalUuid(value)
    }

    companion object {
        fun random(): ProjectId = ProjectId(UUID.randomUUID().toString())

        /** Returns null instead of throwing, for names found while listing folders. */
        fun parseOrNull(value: String): ProjectId? = if (isCanonicalUuid(value)) ProjectId(value) else null
    }
}

/** Identifies a frame within a project. Always a lowercase canonical UUID. */
@JvmInline
@Serializable
value class FrameId(val value: String) {
    init {
        requireCanonicalUuid(value)
    }

    companion object {
        fun random(): FrameId = FrameId(UUID.randomUUID().toString())
    }
}

/** Identifies a layer within a project. Always a lowercase canonical UUID. */
@JvmInline
@Serializable
value class LayerId(val value: String) {
    init {
        requireCanonicalUuid(value)
    }

    companion object {
        fun random(): LayerId = LayerId(UUID.randomUUID().toString())
    }
}

/** Identifies a placed sound within a project. Always a lowercase canonical UUID. */
@JvmInline
@Serializable
value class SoundClipId(val value: String) {
    init {
        requireCanonicalUuid(value)
    }

    companion object {
        fun random(): SoundClipId = SoundClipId(UUID.randomUUID().toString())
    }
}

private fun isCanonicalUuid(value: String): Boolean = try {
    UUID.fromString(value).toString() == value
} catch (e: IllegalArgumentException) {
    false
}

private fun requireCanonicalUuid(value: String) {
    require(isCanonicalUuid(value)) { "Not a lowercase canonical UUID" }
}
