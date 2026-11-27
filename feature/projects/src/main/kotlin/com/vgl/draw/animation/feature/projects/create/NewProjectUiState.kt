package com.vgl.draw.animation.feature.projects.create

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.Resolution

/** What the Create screen shows (spec projects, KB-3). */
sealed interface NewProjectUiState {
    /** The preset choices are being read (a few milliseconds): top bar only, "Create" off, no spinner. */
    data object Loading : NewProjectUiState

    /**
     * The choices. [photoPreview] is the last photo prepared, kept when a color is chosen again; [photoLoading] while
     * a picked photo is prepared; [creating] from the tap on "Create" until the screen leaves (or a failure).
     */
    data class Ready(
        val name: String,
        val defaultName: String,
        val aspectRatio: AspectRatio,
        val resolution: Resolution,
        val frameRate: FrameRate,
        val background: BackgroundChoice,
        val photoPreview: ImageBitmap?,
        val photoLoading: Boolean,
        val creating: Boolean,
        val error: NewProjectError?,
    ) : NewProjectUiState
}

sealed interface BackgroundChoice {
    data class Solid(val color: BackgroundColor) : BackgroundChoice

    data object Photo : BackgroundChoice
}

/** A create that failed; shown once as a message, then cleared with `onErrorShown`. */
enum class NewProjectError { NO_SPACE, WRITE_FAILED }

/** One-off events of the Create screen. */
sealed interface NewProjectEffect {
    /** The project is written; [interstitialAllowed] from the second project ever created (spec section 5). */
    data class ProjectCreated(val id: ProjectId, val interstitialAllowed: Boolean) : NewProjectEffect

    data object ShowComingSoon : NewProjectEffect

    data object ShowPhotoFailed : NewProjectEffect

    /** The projects flag went off (KB-13). */
    data object Close : NewProjectEffect
}
