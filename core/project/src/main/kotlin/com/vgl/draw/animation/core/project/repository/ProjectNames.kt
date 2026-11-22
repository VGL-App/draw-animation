package com.vgl.draw.animation.core.project.repository

/** Project name rules shared by the repository and the screens that edit names. Lengths count code points. */
object ProjectNames {
    const val MAX_NAME_LENGTH = 40

    fun normalize(name: String): String = TODO()

    fun copyName(source: String, existing: Collection<String>): String = TODO()
}
