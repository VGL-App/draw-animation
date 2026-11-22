package com.vgl.draw.animation.feature.export.encode

/**
 * When each frame of an export starts, from the frames' [holds] (1 to 4 frame units each) and [fps] frame units per
 * second. Frame k starts at u(k), the sum of the holds before it.
 */
class ExportTimeline(holds: List<Int>, private val fps: Int) {
    private val starts: IntArray

    init {
        require(fps > 0) { "fps must be positive" }
        require(holds.isNotEmpty()) { "No frames" }
        require(holds.all { it in 1..4 }) { "Every hold must be 1 to 4" }
        starts = IntArray(holds.size + 1)
        holds.forEachIndexed { k, hold -> starts[k + 1] = starts[k] + hold }
    }

    /**
     * GIF delay of each frame in hundredths of a second. Rounded at the cumulative start times rather than per frame,
     * so rounding errors never add up and the total matches the project's duration.
     */
    fun gifDelaysCentiseconds(): IntArray =
        IntArray(starts.size - 1) { k -> centiseconds(starts[k + 1]) - centiseconds(starts[k]) }

    /** Start of frame [index] in microseconds. */
    fun frameStartMicros(index: Int): Long = micros(starts[index])

    /** Total duration in microseconds. */
    val durationMicros: Long get() = micros(starts.last())

    // Hundredths of a second at frame unit [units], rounded half up.
    private fun centiseconds(units: Int): Int = (200 * units + fps) / (2 * fps)

    private fun micros(units: Int): Long = units * 1_000_000L / fps
}
