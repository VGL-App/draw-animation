package com.vgl.draw.animation.feature.projects.data

import androidx.compose.ui.graphics.ImageBitmap
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.repository.NewProject
import com.vgl.draw.animation.core.project.repository.ProjectResult

/** What the Create screen reads and writes (spec projects, KB-3): the preset choices, creating, the photo. */
interface NewProjectRepository {
    /** Read once when the screen opens. Never throws an IOException (the flows retry by themselves). */
    suspend fun defaults(): NewProjectDefaults

    /** Projects ever created with the Create screen, read right before creating (create_inter, question 16). */
    suspend fun createdCount(): Int

    /** ProjectRepository.create, then ProjectThumbnails.update on success (contract with T-004). */
    suspend fun create(request: NewProject): ProjectResult<Project>

    /** Rotated by EXIF, scaled to at most 3840 px on the long side, JPEG; null when the image cannot be used. */
    suspend fun preparePhoto(uri: String): PreparedPhoto?
}

/**
 * The preset choices of the Create screen: the Settings defaults (9:16 and 12 when nothing valid is stored), the
 * default name "My animation <created + 1>" and the name of the first layer.
 */
data class NewProjectDefaults(
    val aspectRatio: AspectRatio,
    val frameRate: FrameRate,
    val defaultName: String,
    val firstLayerName: String,
)

/** A photo ready to be copied into a project: JPEG [bytes], their [extension], and a square [preview]. */
class PreparedPhoto(val bytes: ByteArray, val extension: String, val preview: ImageBitmap)
