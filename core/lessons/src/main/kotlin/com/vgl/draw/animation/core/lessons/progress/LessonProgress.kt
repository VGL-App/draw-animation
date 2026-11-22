package com.vgl.draw.animation.core.lessons.progress

/** The lesson being done: its content version, the step shown and the lesson project (T-001). */
data class LessonAttempt(
    val contentVersion: String,
    /** Index of the step being done, from 0. */
    val step: Int,
    val stepCount: Int,
    /** Canonical lower-case UUID of the lesson project. */
    val projectId: String,
    val startedAtMillis: Long,
) {
    /** Fill of the progress bar: step 3 of 6 (index 2) is half full (spec lessons, question 7). */
    val fraction: Float get() = (step + 1).toFloat() / stepCount
}

/** Project of the last completion, kept for export until the lesson is done again. */
data class CompletedLesson(val projectId: String)

/** Lessons done and lessons in progress, by lesson id. */
data class LessonProgress(
    val completed: Map<String, CompletedLesson> = emptyMap(),
    val inProgress: Map<String, LessonAttempt> = emptyMap(),
)

/**
 * Content version to open [lessonId] with: a lesson in progress keeps the version it started with, until done or
 * restarted (spec lessons, KB-4); otherwise the version of the index shown.
 */
fun packVersionFor(lessonId: String, progress: LessonProgress, indexVersion: String): String =
    progress.inProgress[lessonId]?.contentVersion ?: indexVersion
