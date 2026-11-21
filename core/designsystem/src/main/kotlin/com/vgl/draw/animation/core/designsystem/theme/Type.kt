package com.vgl.draw.animation.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vgl.draw.animation.core.designsystem.R

/** Text typeface, Inter 4.1 (SIL OFL 1.1, see third_party/inter). */
internal val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

/** Display typeface of Studio Night, Space Grotesk (SIL OFL 1.1, see third_party/space-grotesk). */
internal val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

private fun TextStyle.display() =
    copy(fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.Bold, letterSpacing = 0.sp)
private fun TextStyle.text(weight: FontWeight? = null) =
    copy(fontFamily = InterFamily, fontWeight = weight ?: fontWeight, letterSpacing = 0.sp)

/**
 * Material 3 type scale of Studio Night, without letter spacing, as the text styles of the Figma file (ADR-0020):
 * display and headline in Space Grotesk Bold, titleLarge in Space Grotesk Medium, the rest in Inter.
 */
internal val AppTypography: Typography = Typography().run {
    Typography(
        displayLarge = displayLarge.display(),
        displayMedium = displayMedium.display(),
        displaySmall = displaySmall.display(),
        headlineLarge = headlineLarge.display(),
        headlineMedium = headlineMedium.display(),
        headlineSmall = headlineSmall.display(),
        titleLarge = titleLarge.copy(
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
        ),
        titleMedium = titleMedium.text(FontWeight.SemiBold),
        titleSmall = titleSmall.text(),
        bodyLarge = bodyLarge.text(),
        bodyMedium = bodyMedium.text(),
        bodySmall = bodySmall.text(),
        labelLarge = labelLarge.text(FontWeight.SemiBold),
        labelMedium = labelMedium.text(),
        labelSmall = labelSmall.text(),
    )
}
