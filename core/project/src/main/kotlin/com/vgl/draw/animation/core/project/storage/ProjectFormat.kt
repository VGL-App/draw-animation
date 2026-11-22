package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.model.Project

/** Reads and writes `project.json`. */
internal object ProjectFormat {
    const val CURRENT_FORMAT_VERSION = 1

    /** The first rule [project] breaks, or null when it is valid. */
    fun validate(project: Project): String? = TODO()

    fun encode(project: Project): String = TODO()

    fun decode(text: String): ProjectReadResult = TODO()
}
