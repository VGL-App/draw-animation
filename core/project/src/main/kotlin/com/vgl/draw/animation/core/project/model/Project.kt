package com.vgl.draw.animation.core.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Everything saved about a project. Drawing content is not here: each layer of each frame is a separate image file
 * next to `project.json` (see `ProjectFile`). Shapes, text and selections being edited are editor state, not saved.
 */
@Serializable
data class Project(
    val id: ProjectId,
    val kind: ProjectKind,
    val name: String,
    val aspectRatio: AspectRatio,
    val resolution: Resolution,
    val frameRate: FrameRate,
    val background: Background,
    val layers: List<Layer>,
    val frames: List<Frame>,
    val sounds: List<SoundClip>,
    val camera: CameraSettings,
    val onionSkin: OnionSkinSettings,
    val reference: ReferenceLayer?,
    val extraLayersUnlocked: Boolean,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
) {
    /** Lesson projects are left out of the project list, the counter and backups. */
    val isUserProject: Boolean get() = kind is ProjectKind.Regular

    companion object {
        /** A new project: one frame, one layer named [firstLayerName], default settings. */
        fun create(
            id: ProjectId,
            name: String,
            aspectRatio: AspectRatio,
            resolution: Resolution,
            frameRate: FrameRate,
            background: Background,
            firstLayerName: String,
            nowMillis: Long,
            kind: ProjectKind = ProjectKind.Regular,
        ): Project = Project(
            id = id,
            kind = kind,
            name = name,
            aspectRatio = aspectRatio,
            resolution = resolution,
            frameRate = frameRate,
            background = background,
            layers = listOf(Layer(LayerId.random(), firstLayerName, visible = true)),
            frames = listOf(Frame(FrameId.random())),
            sounds = emptyList(),
            camera = CameraSettings(),
            onionSkin = OnionSkinSettings(),
            reference = null,
            extraLayersUnlocked = false,
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        )
    }
}

@Serializable
sealed interface ProjectKind {
    @Serializable
    @SerialName("regular")
    data object Regular : ProjectKind

    /** A project made by a lesson; [step] is the step the user is on. */
    @Serializable
    @SerialName("lesson")
    data class Lesson(val lessonId: String, val step: Int) : ProjectKind
}
