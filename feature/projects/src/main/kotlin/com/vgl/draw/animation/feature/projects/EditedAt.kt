package com.vgl.draw.animation.feature.projects

import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.ZoneId

/** When a project was last edited, as the menu header says it (plan T-012, question 3). */
sealed interface EditedAt {
    data object Today : EditedAt

    data object Yesterday : EditedAt

    data class OnDate(val date: LocalDate, val sameYear: Boolean) : EditedAt
}

fun editedAt(updatedAtMillis: Long, nowMillis: Long, zone: ZoneId): EditedAt = EditedAt.Today

@Composable
internal fun editedLabel(editedAt: EditedAt): String = ""
