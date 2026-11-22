package com.vgl.draw.animation.core.project.render

import com.vgl.draw.animation.core.designsystem.theme.ProjectBackgroundColors
import com.vgl.draw.animation.core.project.model.BackgroundColor

/** ARGB values of the project background colors used when drawing. The app always uses [Default]. */
data class BackgroundPalette(val white: Int, val dark: Int, val blue: Int) {
    fun colorOf(color: BackgroundColor): Int = when (color) {
        BackgroundColor.WHITE -> white
        BackgroundColor.DARK -> dark
        BackgroundColor.BLUE -> blue
    }

    companion object {
        /** The colors of :core:designsystem, the same in the light and dark theme. */
        val Default = BackgroundPalette(
            white = ProjectBackgroundColors.WHITE,
            dark = ProjectBackgroundColors.DARK,
            blue = ProjectBackgroundColors.BLUE,
        )
    }
}
