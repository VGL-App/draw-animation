package com.vgl.draw.animation.core.project.repository

/**
 * Project name rules shared by the repository and the screens that edit names, so both cut names the same way.
 * Lengths count code points, so an emoji counts as one and is never cut in half.
 */
object ProjectNames {
    const val MAX_NAME_LENGTH = 40

    /**
     * Trims both ends and keeps the first [MAX_NAME_LENGTH] code points, then trims the end again. Throws
     * [IllegalArgumentException] for a blank name: callers put the default name instead.
     */
    fun normalize(name: String): String {
        val trimmed = name.trim()
        require(trimmed.isNotEmpty()) { "Blank project name" }
        return takeCodePoints(trimmed, MAX_NAME_LENGTH).trimEnd()
    }

    /**
     * Name of a copy of [source]: "<source> copy", then "<source> copy 2", "<source> copy 3", ... The first one not in
     * [existing] (exact match) is returned. [source] is cut so the result has at most [MAX_NAME_LENGTH] code points.
     */
    fun copyName(source: String, existing: Collection<String>): String {
        val base = source.trim()
        var number = 1
        while (true) {
            val suffix = if (number == 1) " copy" else " copy $number"
            val room = MAX_NAME_LENGTH - suffix.codePointCount(0, suffix.length)
            val candidate = takeCodePoints(base, room).trimEnd() + suffix
            if (candidate !in existing) return candidate
            number++
        }
    }

    private fun takeCodePoints(text: String, count: Int): String {
        if (text.codePointCount(0, text.length) <= count) return text
        return text.substring(0, text.offsetByCodePoints(0, count))
    }
}
