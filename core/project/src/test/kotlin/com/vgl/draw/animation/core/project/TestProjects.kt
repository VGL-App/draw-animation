package com.vgl.draw.animation.core.project

import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.model.CameraSettings
import com.vgl.draw.animation.core.project.model.EffectSource
import com.vgl.draw.animation.core.project.model.Frame
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Layer
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.OnionSkinSettings
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.ProjectKind
import com.vgl.draw.animation.core.project.model.ReferenceLayer
import com.vgl.draw.animation.core.project.model.ReferenceType
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.model.SoundClip
import com.vgl.draw.animation.core.project.model.SoundClipId
import com.vgl.draw.animation.core.project.model.SoundType

/** Test data shared by the tests of :core:project. */
object TestProjects {
    private const val PREFIX = "6f1c2a3e-0000-4000-8000-"

    val SAMPLE_ID = ProjectId("${PREFIX}000000000001")

    /** Text of `project-v1.json`, the format contract approved with plan T-001. */
    fun v1SampleText(): String =
        checkNotNull(TestProjects::class.java.classLoader?.getResource("project-v1.json")).readText()

    /** Exactly the project described by `project-v1.json`. */
    fun full(): Project = Project(
        id = SAMPLE_ID,
        kind = ProjectKind.Lesson(lessonId = "walk-cycle", step = 3),
        name = "My animation 3",
        aspectRatio = AspectRatio.LANDSCAPE_16_9,
        resolution = Resolution.P720,
        frameRate = FrameRate.FPS_24,
        background = Background.Photo(fileExtension = "jpg"),
        layers = listOf(
            Layer(LayerId("${PREFIX}0000000000a1"), "Layer 1", visible = true),
            Layer(LayerId("${PREFIX}0000000000a2"), "Sketch", visible = false),
            Layer(LayerId("${PREFIX}0000000000a3"), "Layer 3", visible = true),
        ),
        frames = listOf(
            Frame(FrameId("${PREFIX}0000000000b1"), hold = 1, cameraKey = null),
            Frame(FrameId("${PREFIX}0000000000b2"), hold = 3, cameraKey = null),
            Frame(
                FrameId("${PREFIX}0000000000b3"),
                hold = 1,
                cameraKey = CameraKey(panX = 0.1f, panY = -0.2f, zoom = 1.2f, rotationDegrees = 15.0f),
            ),
            Frame(FrameId("${PREFIX}0000000000b4"), hold = 1, cameraKey = null),
        ),
        sounds = listOf(
            SoundClip(
                id = SoundClipId("${PREFIX}0000000000c1"),
                type = SoundType.MUSIC,
                title = "Track 1",
                fileExtension = "mp3",
                startFrameId = null,
                durationMillis = 4000,
                effectSource = null,
            ),
            SoundClip(
                id = SoundClipId("${PREFIX}0000000000c2"),
                type = SoundType.VOICE,
                title = "Recording 1",
                fileExtension = "m4a",
                startFrameId = FrameId("${PREFIX}0000000000b2"),
                durationMillis = 2500,
                effectSource = null,
            ),
            SoundClip(
                id = SoundClipId("${PREFIX}0000000000c3"),
                type = SoundType.EFFECT,
                title = "Pop",
                fileExtension = "ogg",
                startFrameId = FrameId("${PREFIX}0000000000b3"),
                durationMillis = 300,
                effectSource = EffectSource(packId = "cartoon", soundId = "pop"),
            ),
        ),
        camera = CameraSettings(enabled = true),
        onionSkin = OnionSkinSettings(
            enabled = true,
            framesBefore = 3,
            framesAfter = 2,
            opacityPercent = 60,
            beforeColorArgb = -65536,
            afterColorArgb = -16776961,
            loop = true,
        ),
        reference = ReferenceLayer(
            type = ReferenceType.VIDEO,
            fileExtension = "jpg",
            frameCount = 120,
            opacityPercent = 40,
            visible = true,
        ),
        extraLayersUnlocked = true,
        createdAtMillis = 1763769600000,
        updatedAtMillis = 1763773200000,
    )
}
