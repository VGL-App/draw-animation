package com.vgl.draw.animation.core.network

/** Returns a monotonic time in milliseconds, for how long the dialog stayed. A fun interface for virtual time in tests. */
internal fun interface NetworkClock {
    fun elapsedRealtimeMillis(): Long
}
