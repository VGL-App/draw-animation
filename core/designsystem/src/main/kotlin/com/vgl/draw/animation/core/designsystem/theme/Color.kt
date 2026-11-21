package com.vgl.draw.animation.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Studio Night palette of Frameloop (ADR-0008). Roles that exist on the Figma file "Frameloop – Design"
// (collection "Frameloop", modes Light and Dark) take its values. Roles the design does not use
// (secondaryContainer, tertiary, surfaceContainerLowest, surfaceBright, surfaceDim) are derived to match.
private val BrandPrimary = Color(0xFF3D6A00)
private val BrandPrimaryDark = Color(0xFFB6F35C)

internal val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDF5B8),
    onPrimaryContainer = Color(0xFF1C2C05),
    inversePrimary = BrandPrimaryDark,
    secondary = Color(0xFF2F6FE0),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD9E5FF),
    onSecondaryContainer = Color(0xFF0B2A66),
    tertiary = Color(0xFF6E4BB5),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEADDFF),
    onTertiaryContainer = Color(0xFF25005A),
    error = Color(0xFFC62828),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFDE2E2),
    onErrorContainer = Color(0xFF5A0F0F),
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF12141A),
    surface = Color(0xFFF7F8FA),
    onSurface = Color(0xFF12141A),
    surfaceVariant = Color(0xFFE3E6EA),
    onSurfaceVariant = Color(0xFF5F6675),
    outline = Color(0xFFB9BEC8),
    outlineVariant = Color(0xFFD2D6DC),
    surfaceTint = BrandPrimary,
    inverseSurface = Color(0xFF22262E),
    inverseOnSurface = Color(0xFFEEF0F4),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F2F5),
    surfaceContainer = Color(0xFFECEEF1),
    surfaceContainerHigh = Color(0xFFE3E6EA),
    surfaceContainerHighest = Color(0xFFD9DDE2),
    surfaceBright = Color(0xFFF7F8FA),
    surfaceDim = Color(0xFFD5D9DF),
)

internal val DarkColors = darkColorScheme(
    primary = BrandPrimaryDark,
    onPrimary = Color(0xFF10140A),
    primaryContainer = Color(0xFF2E3D17),
    onPrimaryContainer = Color(0xFFD8FA9E),
    inversePrimary = BrandPrimary,
    secondary = Color(0xFF6EA8FF),
    onSecondary = Color(0xFF0A1730),
    secondaryContainer = Color(0xFF1D3766),
    onSecondaryContainer = Color(0xFFD9E5FF),
    tertiary = Color(0xFFCDB8FF),
    onTertiary = Color(0xFF2E1660),
    tertiaryContainer = Color(0xFF4A3480),
    onTertiaryContainer = Color(0xFFEADDFF),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0606),
    errorContainer = Color(0xFF4A1518),
    onErrorContainer = Color(0xFFFFD9D9),
    background = Color(0xFF0E1014),
    onBackground = Color(0xFFEEF0F4),
    surface = Color(0xFF0E1014),
    onSurface = Color(0xFFEEF0F4),
    surfaceVariant = Color(0xFF22262E),
    onSurfaceVariant = Color(0xFF8C93A3),
    outline = Color(0xFF3A404C),
    outlineVariant = Color(0xFF2C313B),
    surfaceTint = BrandPrimaryDark,
    inverseSurface = Color(0xFFEEF0F4),
    inverseOnSurface = Color(0xFF12141A),
    surfaceContainerLowest = Color(0xFF0A0C10),
    surfaceContainerLow = Color(0xFF13161B),
    surfaceContainer = Color(0xFF171A20),
    surfaceContainerHigh = Color(0xFF22262E),
    surfaceContainerHighest = Color(0xFF2B303A),
    surfaceBright = Color(0xFF2B303A),
    surfaceDim = Color(0xFF0E1014),
)
