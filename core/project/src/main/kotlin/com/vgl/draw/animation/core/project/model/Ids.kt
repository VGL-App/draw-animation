package com.vgl.draw.animation.core.project.model

import kotlinx.serialization.Serializable

/** Identifies a project; also the name of its folder on disk. Always a lowercase canonical UUID. */
@JvmInline
@Serializable
value class ProjectId(val value: String) {
    companion object {
        fun random(): ProjectId = TODO()

        /** Returns null instead of throwing, for names found while listing folders. */
        fun parseOrNull(value: String): ProjectId? = TODO()
    }
}

/** Identifies a frame within a project. Always a lowercase canonical UUID. */
@JvmInline
@Serializable
value class FrameId(val value: String) {
    companion object {
        fun random(): FrameId = TODO()
    }
}

/** Identifies a layer within a project. Always a lowercase canonical UUID. */
@JvmInline
@Serializable
value class LayerId(val value: String) {
    companion object {
        fun random(): LayerId = TODO()
    }
}

/** Identifies a placed sound within a project. Always a lowercase canonical UUID. */
@JvmInline
@Serializable
value class SoundClipId(val value: String) {
    companion object {
        fun random(): SoundClipId = TODO()
    }
}
