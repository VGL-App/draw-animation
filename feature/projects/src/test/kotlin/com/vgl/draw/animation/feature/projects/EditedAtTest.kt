package com.vgl.draw.animation.feature.projects

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

/** [editedAt]: the calendar day of the last edit in the device's zone (plan T-012, question 3). */
class EditedAtTest {
    private val utc: ZoneId = ZoneOffset.UTC

    // 2026-11-22 12:00 UTC, the middle of the day.
    private val now = millis(LocalDateTime.of(2026, 11, 22, 12, 0), utc)

    private fun millis(time: LocalDateTime, zone: ZoneId): Long = time.atZone(zone).toInstant().toEpochMilli()

    @Test
    fun sameDay_isToday() {
        assertEquals(EditedAt.Today, editedAt(millis(LocalDateTime.of(2026, 11, 22, 0, 0), utc), now, utc))
        assertEquals(EditedAt.Today, editedAt(millis(LocalDateTime.of(2026, 11, 22, 23, 59), utc), now, utc))
        // Control: one minute before midnight is the day before.
        assertEquals(EditedAt.Yesterday, editedAt(millis(LocalDateTime.of(2026, 11, 21, 23, 59), utc), now, utc))
    }

    @Test
    fun dayBefore_isYesterday() {
        assertEquals(EditedAt.Yesterday, editedAt(millis(LocalDateTime.of(2026, 11, 21, 0, 0), utc), now, utc))
    }

    @Test
    fun twoDaysBefore_isDateOfSameYear() {
        assertEquals(
            EditedAt.OnDate(LocalDate.of(2026, 11, 20), sameYear = true),
            editedAt(millis(LocalDateTime.of(2026, 11, 20, 23, 59), utc), now, utc),
        )
    }

    @Test
    fun yearBefore_isDateOfOtherYear() {
        assertEquals(
            EditedAt.OnDate(LocalDate.of(2025, 11, 3), sameYear = false),
            editedAt(millis(LocalDateTime.of(2025, 11, 3, 9, 0), utc), now, utc),
        )
    }

    @Test
    fun afterNow_isToday() {
        // The clock moved back: an edit 5 minutes "in the future" is today.
        assertEquals(EditedAt.Today, editedAt(now + 5 * 60_000L, now, utc))
        // Control: a day earlier is not today.
        assertEquals(EditedAt.Yesterday, editedAt(now - 24 * 60 * 60_000L, now, utc))
    }

    @Test
    fun dayBoundary_followsZone() {
        val vietnam = ZoneId.of("Asia/Ho_Chi_Minh")
        val nowVietnam = millis(LocalDateTime.of(2026, 11, 22, 0, 30), vietnam)
        val edit = millis(LocalDateTime.of(2026, 11, 21, 23, 30), vietnam)
        // Same instants: in UTC both are on 2026-11-21 (16:30 and 17:30), in Vietnam they are on two days.
        assertEquals(EditedAt.Today, editedAt(edit, nowVietnam, utc))
        assertEquals(EditedAt.Yesterday, editedAt(edit, nowVietnam, vietnam))
    }
}
