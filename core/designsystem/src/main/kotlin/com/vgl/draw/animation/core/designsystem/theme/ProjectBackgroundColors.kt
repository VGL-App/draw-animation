package com.vgl.draw.animation.core.designsystem.theme

/**
 * The three background colors a project can have ("white", "dark gray", "blue"), as ARGB.
 *
 * The background is part of the exported file, so these colors never follow the device's light or dark theme: the
 * color swatches of the New project screen and the project background on the drawing screen use exactly these values
 * (`Color(ProjectBackgroundColors.BLUE)`), never [AppColors.canvas], which changes with the theme.
 * Plain `Int` rather than a Compose `Color`, so :core:project reads them without Compose on its compile classpath.
 * Listed in docs/design-system/tokens.json as the theme-independent group `projectBackground`.
 */
object ProjectBackgroundColors {
    const val WHITE: Int = 0
    const val DARK: Int = 0
    const val BLUE: Int = 0
}
