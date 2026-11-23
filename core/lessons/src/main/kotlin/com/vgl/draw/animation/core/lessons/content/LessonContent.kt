package com.vgl.draw.animation.core.lessons.content

import java.io.File
import kotlinx.coroutines.flow.Flow

/**
 * Lesson content on R2 (plan T-048): the index of the current content version and the lesson packs, kept on the
 * device once downloaded. Load errors are returned as values, never thrown.
 */
interface LessonContent {
    /**
     * Cold flow: collect again to retry. Emits the index kept on the device at once when there is one, else
     * [LessonLibraryState.Loading]; then loads the version of `lessons_content_version` in the background.
     */
    fun library(): Flow<LessonLibraryState>

    /** Thumbnail of a lesson of [version], or null when it cannot be loaded (the card shows an empty background). */
    suspend fun thumbnail(version: String, entry: LessonEntry): File?

    /** Downloads and extracts the pack of [lessonId] listed in the index of [version]. Never throws for load errors. */
    suspend fun loadPack(version: String, lessonId: String): LessonPackResult
}

sealed interface LessonLibraryState {
    data object Loading : LessonLibraryState

    data class Ready(val version: String, val index: LessonIndex) : LessonLibraryState

    data class Unavailable(val failure: LessonLoadFailure) : LessonLibraryState
}

sealed interface LessonPackResult {
    data class Ready(val pack: LessonPack) : LessonPackResult

    data class Unavailable(val failure: LessonLoadFailure) : LessonPackResult
}

enum class LessonLoadFailure {
    /** The device could not reach the server. */
    NETWORK,

    /** The file or the lesson does not exist. */
    NOT_FOUND,

    /** The content breaks the format (plan T-048, section 3.1). */
    INVALID,

    /** Any other failure. */
    OTHER,
}
