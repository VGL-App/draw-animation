package com.vgl.draw.animation.core.designsystem.theme

/**
 * Colors of the Frameloop watermark: white text and icon on a black pill at 35% opacity, as ARGB.
 *
 * The watermark is part of the exported file, so these colors never follow the device's light or dark theme. The
 * watermark preview of the Export screen uses exactly these values (`Color(WatermarkColors.PILL)`).
 * Plain `Int` rather than a Compose `Color`, so drawing on an Android `Canvas` needs no Compose.
 * Listed in docs/design-system/tokens.json as the theme-independent group `watermark`.
 */
object WatermarkColors {
    const val CONTENT: Int = 0xFFFFFFFF.toInt()

    /** Black at alpha 0x59 (89 of 255, 35% rounded down). */
    const val PILL: Int = 0x59000000
}
