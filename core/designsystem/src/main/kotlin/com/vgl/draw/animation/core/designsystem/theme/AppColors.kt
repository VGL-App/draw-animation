package com.vgl.draw.animation.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colors of Frameloop that have no Material 3 role, from the Figma collection "Frameloop" (ADR-0008).
 * Read them with [AppTheme.colors]; Material 3 roles stay in MaterialTheme.colorScheme.
 */
@Immutable
data class AppColors(
    /** Paper of the drawing canvas. Stays light in the dark theme, like real paper. */
    val canvas: Color,
    /** Default ink on [canvas]. */
    val onCanvas: Color,
    /** Onion skin tint of the frames before the current one. */
    val onionPrevious: Color,
    /** Onion skin tint of the frames after the current one. */
    val onionNext: Color,
)

internal val LightAppColors = AppColors(
    canvas = Color(0xFFFFFFFF),
    onCanvas = Color(0xFF1A1C22),
    onionPrevious = Color(0xFFFF5A5F),
    onionNext = Color(0xFF3D8BFF),
)

internal val DarkAppColors = AppColors(
    canvas = Color(0xFFFAFAF7),
    onCanvas = Color(0xFF1A1C22),
    onionPrevious = Color(0xFFFF5A5F),
    onionNext = Color(0xFF3D8BFF),
)

internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }
