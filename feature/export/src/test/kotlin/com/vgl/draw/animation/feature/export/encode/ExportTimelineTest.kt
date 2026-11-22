package com.vgl.draw.animation.feature.export.encode

import kotlin.random.Random
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportTimelineTest {
    @Test
    fun gifDelays_followHoldsWithCumulativeRounding() {
        val delays = ExportTimeline(listOf(1, 3, 1, 2), fps = 12).gifDelaysCentiseconds()

        assertArrayEquals(intArrayOf(8, 25, 9, 16), delays)
        assertEquals(58, delays.sum())
    }

    @Test
    fun gifDelays_48FramesAt12Fps_total400() {
        val delays = ExportTimeline(List(48) { 1 }, fps = 12).gifDelaysCentiseconds()

        assertEquals(48, delays.size)
        assertEquals(400, delays.sum())
        assertTrue(delays.joinToString(), delays.all { it == 8 || it == 9 })
    }

    @Test
    fun gifDelays_examples() {
        assertArrayEquals(intArrayOf(3, 4, 3), ExportTimeline(listOf(1, 1, 1), fps = 30).gifDelaysCentiseconds())
        assertArrayEquals(intArrayOf(17), ExportTimeline(listOf(4), fps = 24).gifDelaysCentiseconds())
        assertArrayEquals(intArrayOf(67), ExportTimeline(listOf(4), fps = 6).gifDelaysCentiseconds())
    }

    @Test
    fun gifDelays_neverBelow3() {
        val random = Random(44)
        for (fps in listOf(6, 8, 12, 15, 24, 30)) {
            val holds = List(50) { random.nextInt(1, 5) }
            val delays = ExportTimeline(holds, fps).gifDelaysCentiseconds()

            assertTrue("fps=$fps ${delays.joinToString()}", delays.all { it >= 3 })
            assertEquals("fps=$fps", centiseconds(holds.sum(), fps), delays.sum())
        }
    }

    @Test
    fun frameStartMicros_usesHolds() {
        val timeline = ExportTimeline(listOf(1, 3), fps = 12)

        assertEquals(0L, timeline.frameStartMicros(0))
        assertEquals(83_333L, timeline.frameStartMicros(1))
        assertEquals(333_333L, timeline.durationMicros)
    }

    @Test
    fun init_rejectsInvalid() {
        assertThrows(IllegalArgumentException::class.java) { ExportTimeline(listOf(1, 0), fps = 12) }
        assertThrows(IllegalArgumentException::class.java) { ExportTimeline(listOf(5), fps = 12) }
        assertThrows(IllegalArgumentException::class.java) { ExportTimeline(listOf(1), fps = 0) }
        assertThrows(IllegalArgumentException::class.java) { ExportTimeline(emptyList(), fps = 12) }
    }

    // Plan T-044, section 3.1: hundredths of a second at frame unit u, rounded half up.
    private fun centiseconds(units: Int, fps: Int): Int = (200 * units + fps) / (2 * fps)
}
