package com.vgl.draw.animation.core.lessons.content

import java.io.File

/** The pack is not a safe, complete lesson zip. Not an [java.io.IOException]: retrying never helps. */
internal class InvalidPackException(reason: String) : Exception(reason)

/**
 * Extracts `lesson.zip` into `root/<version>/<lessonId>/`, safely (plan T-048, section 3.1).
 * [onExtract] is a test hook called each time a pack is really extracted.
 */
internal class PackExtractor(
    private val root: File,
    private val maxEntries: Int = 4_000,
    private val maxBytes: Long = 32L * 1024 * 1024,
    private val onExtract: () -> Unit = {},
) {
    /** Call it within an I/O dispatcher. Throws [InvalidPackException], or IOException for a disk error. */
    suspend fun extract(zip: File, version: String, lessonId: String): File = TODO()
}
