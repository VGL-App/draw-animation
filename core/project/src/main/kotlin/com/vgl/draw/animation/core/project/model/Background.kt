package com.vgl.draw.animation.core.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Background {
    @Serializable
    @SerialName("solid")
    data class Solid(val color: BackgroundColor) : Background

    /** A photo copied into the project folder as `background.<fileExtension>`, so deleting the original changes nothing. */
    @Serializable
    @SerialName("photo")
    data class Photo(val fileExtension: String) : Background
}

/**
 * Background colors offered when creating a project. Values match the `background` parameter of `project_create`.
 * The actual color values live in :core:designsystem, not here.
 */
@Serializable
enum class BackgroundColor {
    @SerialName("white")
    WHITE,

    @SerialName("dark")
    DARK,

    @SerialName("blue")
    BLUE,
}
