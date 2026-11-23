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
    val ordered: List<LessonEntry> get() = groups.flatMap { group -> lessonsIn(group.id) }

    fun lessonsIn(groupId: String): List<LessonEntry> = lessons.filter { it.groupId == groupId }

    fun entry(lessonId: String): LessonEntry? = lessons.firstOrNull { it.id == lessonId }

    /** Position of the lesson in its group, from 1; null for an unknown lesson. */
    fun numberInGroup(lessonId: String): Int? {
        val entry = entry(lessonId) ?: return null
        return lessonsIn(entry.groupId).indexOfFirst { it.id == lessonId } + 1
    }

    /** "Lesson <number>": position in the whole library ([ordered]), from 1; null for an unknown lesson. */
    fun libraryNumber(lessonId: String): Int? = ordered.indexOfFirst { it.id == lessonId }.takeIf { it >= 0 }?.plus(1)

    /** The lesson after [lessonId] in [ordered], crossing into the next group; null after the last lesson. */
    fun next(lessonId: String): LessonEntry? {
        val all = ordered
        val position = all.indexOfFirst { it.id == lessonId }
        return if (position < 0) null else all.getOrNull(position + 1)
    }

    /** How many lessons of the group are in [completed] (pass `progress.completed.keys`). */
    fun completedCount(groupId: String, completed: Set<String>): Int = lessonsIn(groupId).count { it.id in completed }
}
