package com.vgl.draw.animation.core.lessons.content

/** A group of lessons on the lessons screen, in the order of the index. */
data class LessonGroup(val id: String, val title: String)

/**
 * One lesson of the index. [thumb] and [pack] are paths relative to the version folder, always
 * `<id>/thumb.webp` and `<id>/lesson.zip`.
 */
data class LessonEntry(
    val id: String,
    val groupId: String,
    val title: String,
    val minutes: Int,
    val frameCount: Int,
    val stepCount: Int,
    val thumb: String,
    val pack: String,
)

/** The list of lessons of one content version (`lessons/<version>/index.json`). */
data class LessonIndex(val groups: List<LessonGroup>, val lessons: List<LessonEntry>) {
    /** Lessons by the order of [groups], then by their order in [lessons]. */
    val ordered: List<LessonEntry> get() = TODO()

    fun lessonsIn(groupId: String): List<LessonEntry> = TODO()

    fun entry(lessonId: String): LessonEntry? = TODO()

    /** Position of the lesson in its group, from 1; null for an unknown lesson. */
    fun numberInGroup(lessonId: String): Int? = TODO()

    /** "Lesson <number>": position in the whole library ([ordered]), from 1; null for an unknown lesson. */
    fun libraryNumber(lessonId: String): Int? = TODO()

    /** The lesson after [lessonId] in [ordered], crossing into the next group; null after the last lesson. */
    fun next(lessonId: String): LessonEntry? = TODO()

    /** How many lessons of the group are in [completed] (pass `progress.completed.keys`). */
    fun completedCount(groupId: String, completed: Set<String>): Int = TODO()
}
