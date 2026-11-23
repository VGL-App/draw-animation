package com.vgl.draw.animation.feature.projects

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** When a project was last edited, as the menu header says it (plan T-012, question 3). */
sealed interface EditedAt {
    data object Today : EditedAt

    data object Yesterday : EditedAt

    data class OnDate(val date: LocalDate, val sameYear: Boolean) : EditedAt
}

/**
 * The calendar day of [updatedAtMillis] in [zone], compared with the day of [nowMillis]. An edit after now (the clock
 * was moved back) counts as today.
 */
fun editedAt(updatedAtMillis: Long, nowMillis: Long, zone: ZoneId): EditedAt {
    val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
    val day = Instant.ofEpochMilli(updatedAtMillis).atZone(zone).toLocalDate()
    return when {
        !day.isBefore(today) -> EditedAt.Today
        day == today.minusDays(1) -> EditedAt.Yesterday
        else -> EditedAt.OnDate(day, sameYear = day.year == today.year)
    }
}

/**
 * "edited today", "edited yesterday", or "edited <short date>" in the device's language. The date is formatted from
 * the [LocalDate] already computed, so the text and the Today/Yesterday branch never disagree.
 */
@Composable
internal fun editedLabel(editedAt: EditedAt): String = when (editedAt) {
    EditedAt.Today -> stringResource(R.string.projects_menu_edited_today)
    EditedAt.Yesterday -> stringResource(R.string.projects_menu_edited_yesterday)
    is EditedAt.OnDate -> {
        val locale = LocalConfiguration.current.locales[0]
        val skeleton = if (editedAt.sameYear) "MMMd" else "yMMMd"
        val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
        val date = DateTimeFormatter.ofPattern(pattern, locale).format(editedAt.date)
        stringResource(R.string.projects_menu_edited_on, date)
    }
}
