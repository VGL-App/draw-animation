package com.vgl.draw.animation.core.project.repository

private const val MAX_CAUSE_DEPTH = 10
private val OUT_OF_SPACE_MARKERS = listOf("ENOSPC", "No space left on device")

/**
 * True when [e] or one of its causes says the device is full. Android reports `write failed: ENOSPC (No space left on
 * device)`, the JVM on Linux `No space left on device`. Checked by message rather than `android.system.ErrnoException`
 * so it also runs in JVM unit tests. Follows at most [MAX_CAUSE_DEPTH] causes, so a cause loop cannot hang.
 */
internal fun isOutOfSpace(e: Throwable): Boolean {
    var current: Throwable? = e
    repeat(MAX_CAUSE_DEPTH) {
        val error = current ?: return false
        val message = error.message
        if (message != null && OUT_OF_SPACE_MARKERS.any { it in message }) return true
        current = error.cause
    }
    return false
}
