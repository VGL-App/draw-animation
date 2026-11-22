package com.vgl.draw.animation.feature.export.model

/** File formats of the Export screen. [includesBackground] is false for the formats that keep transparency. */
enum class ExportFormat(val includesBackground: Boolean) {
    MP4(includesBackground = true),
    GIF(includesBackground = true),
    PNG_SEQUENCE(includesBackground = false),
    TRANSPARENT_GIF(includesBackground = false),
}
