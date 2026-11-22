package com.vgl.draw.animation.core.lessons.progress

import kotlinx.coroutines.flow.Flow

/**
 * Progress of the lessons, kept on the device (plan T-048). Never cleared by itself, also while the lessons feature
 * flag is off (spec lessons, KB-10). Write errors are thrown for the ViewModel to catch.
 */
interface LessonProgressStore {
    /** Current progress, then every change. Unreadable stored data reads as empty progress. */
    val progress: Flow<LessonProgress>

    /** Starts or restarts a lesson; keeps it in completed. Throws IllegalArgumentException, IOException. */
    suspend fun start(lessonId: String, attempt: LessonAttempt)

    /** False when the lesson is not in progress (nothing written). Throws IllegalArgumentException, IOException. */
    suspend fun setStep(lessonId: String, step: Int): Boolean

    /** Marks the lesson done; returns the time since start, or null when it was not in progress. Throws IOException. */
    suspend fun complete(lessonId: String, nowMillis: Long): Long?
}
