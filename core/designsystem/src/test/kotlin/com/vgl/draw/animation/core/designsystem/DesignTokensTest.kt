package com.vgl.draw.animation.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import com.vgl.draw.animation.core.designsystem.theme.AppColors
import com.vgl.draw.animation.core.designsystem.theme.AppTypography
import com.vgl.draw.animation.core.designsystem.theme.DarkAppColors
import com.vgl.draw.animation.core.designsystem.theme.DarkColors
import com.vgl.draw.animation.core.designsystem.theme.InterFamily
import com.vgl.draw.animation.core.designsystem.theme.LightAppColors
import com.vgl.draw.animation.core.designsystem.theme.LightColors
import com.vgl.draw.animation.core.designsystem.theme.ProjectBackgroundColors
import com.vgl.draw.animation.core.designsystem.theme.SpaceGroteskFamily
import com.vgl.draw.animation.core.designsystem.theme.Spacing
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * docs/design-system/tokens.json lists every value of the design system for designers (ADR-0020).
 * This test fails when the file no longer matches the code. To update the file after an approved change:
 * ./gradlew :core:designsystem:testDebugUnitTest -PdesignTokens.update=true
 */
class DesignTokensTest {
    @Test
    fun tokensFile_matchesTheDesignSystem() {
        val file = File(System.getProperty("designTokens.file") ?: error("designTokens.file is not set"))
        val expected = tokensJson()
        if (System.getProperty("designTokens.update") == "true") file.writeText(expected)
        assertEquals(
            "docs/design-system/tokens.json is out of date. Run with -PdesignTokens.update=true and commit it.",
            expected,
            if (file.exists()) file.readText() else "",
        )
    }

    @Test
    fun colorRoles_listEveryRoleOfColorScheme() {
        // Color is a value class, so its getters return long and carry a mangled suffix, e.g. getPrimary-0d7_KjU.
        val schemeRoles = ColorScheme::class.java.methods
            .filter { method ->
                method.name.startsWith("get") &&
                    method.parameterCount == 0 &&
                    method.returnType == Long::class.javaPrimitiveType
            }
            .map { method ->
                method.name.removePrefix("get").substringBefore('-').replaceFirstChar { it.lowercaseChar() }
            }
            .filter { it != "scrim" }
            .toSet()
        assertEquals(
            "tokens.json must list every color role of ColorScheme except scrim (L-024).",
            schemeRoles.sorted(),
            colorRoles(LightColors).map { it.first }.sorted(),
        )
    }

    @Test
    fun appColorRoles_listEveryFieldOfAppColors() {
        val fields = AppColors::class.java.declaredFields
            .filter { !java.lang.reflect.Modifier.isStatic(it.modifiers) }
            .map { it.name }
            .sorted()
        assertEquals(
            "tokens.json must list every color of AppColors: add the new one to appColorRoles.",
            fields,
            appColorRoles(LightAppColors).map { it.first }.sorted(),
        )
    }

    @Test
    fun projectBackgroundColors_listedInTokens() {
        val lines = tokensJson().lines()
        val line = "  \"projectBackground\": {\"white\": \"#FFFFFF\", \"dark\": \"#22262E\", \"blue\": \"#2F6FE0\"},"
        val index = lines.indexOf(line)
        assertTrue("tokens.json must list the project background colors at the top level: $line", index >= 0)
        // Right after the "color" group closes, outside it: figma-export.py reads every key of "color" as a theme.
        assertEquals("  },", lines[index - 1])
        assertEquals("    \"dark\": ${colors(DarkColors, DarkAppColors)}", lines[index - 2])
    }
}

internal fun tokensJson(): String = buildString {
    appendLine("{")
    appendLine("  \"color\": {")
    appendLine("    \"light\": ${colors(LightColors, LightAppColors)},")
    appendLine("    \"dark\": ${colors(DarkColors, DarkAppColors)}")
    appendLine("  },")
    appendLine("  \"projectBackground\": ${projectBackground()},")
    val styles = typographyStyles(AppTypography)
    appendLine("  \"typography\": {")
    styles.forEachIndexed { index, (name, style) ->
        val comma = if (index < styles.lastIndex) "," else ""
        appendLine("    \"$name\": ${textStyle(style)}$comma")
    }
    appendLine("  },")
    val spacing = Spacing.scale.joinToString(", ") { (name, dp) -> "\"$name\": ${dp.value.toInt()}" }
    appendLine("  \"spacingDp\": {$spacing}")
    appendLine("}")
}

// Material 3 roles first, then the app roles without a Material 3 slot (AppColors), so designers find both.
private fun colors(scheme: ColorScheme, app: AppColors): String = (colorRoles(scheme) + appColorRoles(app))
    .joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, color) -> "\"$name\": \"${hex(color)}\"" }

// Not under "color": these colors do not follow the theme, and every key of "color" is a theme for figma-export.py.
private fun projectBackground(): String = listOf(
    "white" to ProjectBackgroundColors.WHITE,
    "dark" to ProjectBackgroundColors.DARK,
    "blue" to ProjectBackgroundColors.BLUE,
).joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, argb) -> "\"$name\": \"${hex(argb)}\"" }

internal fun colorRoles(scheme: ColorScheme): List<Pair<String, Color>> = listOf(
    "primary" to scheme.primary,
    "onPrimary" to scheme.onPrimary,
    "primaryContainer" to scheme.primaryContainer,
    "onPrimaryContainer" to scheme.onPrimaryContainer,
    "secondary" to scheme.secondary,
    "onSecondary" to scheme.onSecondary,
    "secondaryContainer" to scheme.secondaryContainer,
    "onSecondaryContainer" to scheme.onSecondaryContainer,
    "tertiary" to scheme.tertiary,
    "onTertiary" to scheme.onTertiary,
    "tertiaryContainer" to scheme.tertiaryContainer,
    "onTertiaryContainer" to scheme.onTertiaryContainer,
    "error" to scheme.error,
    "onError" to scheme.onError,
    "errorContainer" to scheme.errorContainer,
    "onErrorContainer" to scheme.onErrorContainer,
    "background" to scheme.background,
    "onBackground" to scheme.onBackground,
    "surface" to scheme.surface,
    "onSurface" to scheme.onSurface,
    "surfaceVariant" to scheme.surfaceVariant,
    "onSurfaceVariant" to scheme.onSurfaceVariant,
    "outline" to scheme.outline,
    "outlineVariant" to scheme.outlineVariant,
    "inversePrimary" to scheme.inversePrimary,
    "inverseSurface" to scheme.inverseSurface,
    "inverseOnSurface" to scheme.inverseOnSurface,
    "surfaceTint" to scheme.surfaceTint,
    "surfaceBright" to scheme.surfaceBright,
    "surfaceDim" to scheme.surfaceDim,
    "surfaceContainerLowest" to scheme.surfaceContainerLowest,
    "surfaceContainerLow" to scheme.surfaceContainerLow,
    "surfaceContainer" to scheme.surfaceContainer,
    "surfaceContainerHigh" to scheme.surfaceContainerHigh,
    "surfaceContainerHighest" to scheme.surfaceContainerHighest,
)

internal fun appColorRoles(app: AppColors): List<Pair<String, Color>> = listOf(
    "canvas" to app.canvas,
    "onCanvas" to app.onCanvas,
    "onionPrevious" to app.onionPrevious,
    "onionNext" to app.onionNext,
)

private fun hex(color: Color): String = hex(color.toArgb())

private fun hex(argb: Int): String = "#%06X".format(argb and 0xFFFFFF)

private fun typographyStyles(type: Typography): List<Pair<String, TextStyle>> = listOf(
    "displayLarge" to type.displayLarge,
    "displayMedium" to type.displayMedium,
    "displaySmall" to type.displaySmall,
    "headlineLarge" to type.headlineLarge,
    "headlineMedium" to type.headlineMedium,
    "headlineSmall" to type.headlineSmall,
    "titleLarge" to type.titleLarge,
    "titleMedium" to type.titleMedium,
    "titleSmall" to type.titleSmall,
    "bodyLarge" to type.bodyLarge,
    "bodyMedium" to type.bodyMedium,
    "bodySmall" to type.bodySmall,
    "labelLarge" to type.labelLarge,
    "labelMedium" to type.labelMedium,
    "labelSmall" to type.labelSmall,
)

private fun textStyle(style: TextStyle): String = "{\"fontFamily\": \"${familyName(style)}\", " +
    "\"fontSizeSp\": ${style.fontSize.value}, \"lineHeightSp\": ${style.lineHeight.value}, " +
    "\"fontWeight\": ${style.fontWeight?.weight}, \"letterSpacingSp\": ${style.letterSpacing.value}}"

private fun familyName(style: TextStyle): String = when (style.fontFamily) {
    InterFamily -> "Inter"
    SpaceGroteskFamily -> "Space Grotesk"
    else -> error("Unknown font family in AppTypography: ${style.fontFamily}. Name it in DesignTokensTest.")
}
