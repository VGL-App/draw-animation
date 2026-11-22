package com.vgl.draw.animation.core.project.backup

import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.CameraSettings
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
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectFormat
import java.io.OutputStream
import java.util.Random
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Where one entry of the sample lies in the file: [headerStart] before its local header, [dataStart] where its
 * compressed data starts, [end] after its data descriptor (the 16-byte tail of the entry).
 */
data class SampleEntry(val name: String, val headerStart: Long, val dataStart: Long, val end: Long)

/**
 * The content of `backup-v1.frameloop`, the backup format contract approved with plan T-003. Two regular projects:
 * project 0 has a solid background, 3 frames, 2 layers and one empty cell; project 1 has a photo background, a sound
 * and a reference video of 2 images. Every image and sound file is 200 fixed bytes, different for each file.
 */
object BackupSample {
    private const val PREFIX = "b0c1d2e3-0000-4000-8000-"
    private const val FILE_SIZE = 200

    val ID_0 = ProjectId("${PREFIX}000000000001")
    val ID_1 = ProjectId("${PREFIX}000000000002")

    private val layer0a = LayerId("${PREFIX}0000000000a1")
    private val layer0b = LayerId("${PREFIX}0000000000a2")
    private val frame0a = FrameId("${PREFIX}0000000000b1")
    private val frame0b = FrameId("${PREFIX}0000000000b2")
    private val frame0c = FrameId("${PREFIX}0000000000b3")
    private val layer1a = LayerId("${PREFIX}0000000000a3")
    private val frame1a = FrameId("${PREFIX}0000000000b4")
    private val frame1b = FrameId("${PREFIX}0000000000b5")
    private val sound1 = SoundClipId("${PREFIX}0000000000c1")

    fun project0(): Project = Project(
        id = ID_0,
        kind = ProjectKind.Regular,
        name = "Bouncing ball",
        aspectRatio = AspectRatio.PORTRAIT_9_16,
        resolution = Resolution.P720,
        frameRate = FrameRate.FPS_12,
        background = Background.Solid(BackgroundColor.BLUE),
        layers = listOf(Layer(layer0a, "Layer 1", visible = true), Layer(layer0b, "Layer 2", visible = true)),
        frames = listOf(Frame(frame0a), Frame(frame0b, hold = 2), Frame(frame0c)),
        sounds = emptyList(),
        camera = CameraSettings(),
        onionSkin = OnionSkinSettings(),
        reference = null,
        extraLayersUnlocked = false,
        createdAtMillis = 1763769600000,
        updatedAtMillis = 1763773200000,
    )

    fun project1(): Project = Project(
        id = ID_1,
        kind = ProjectKind.Regular,
        name = "Sunset walk",
        aspectRatio = AspectRatio.LANDSCAPE_16_9,
        resolution = Resolution.P1080,
        frameRate = FrameRate.FPS_24,
        background = Background.Photo(fileExtension = "jpg"),
        layers = listOf(Layer(layer1a, "Layer 1", visible = true)),
        frames = listOf(Frame(frame1a), Frame(frame1b)),
        sounds = listOf(
            SoundClip(
                id = sound1,
                type = SoundType.MUSIC,
                title = "Track 1",
                fileExtension = "m4a",
                startFrameId = null,
                durationMillis = 2000,
                effectSource = null,
            ),
        ),
        camera = CameraSettings(),
        onionSkin = OnionSkinSettings(),
        reference = ReferenceLayer(type = ReferenceType.VIDEO, fileExtension = "png", frameCount = 2),
        extraLayersUnlocked = true,
        createdAtMillis = 1763856000000,
        updatedAtMillis = 1763859600000,
    )

    fun projects(): List<Project> = listOf(project0(), project1())

    /** The files of project [index], in entry order. Cell `frames[1]` x `layers[1]` of project 0 is empty. */
    fun files(index: Int): List<Pair<ProjectFile, ByteArray>> {
        val list = when (index) {
            0 -> listOf(
                ProjectFile.Thumbnail,
                ProjectFile.Cell(frame0a, layer0a),
                ProjectFile.Cell(frame0a, layer0b),
                ProjectFile.Cell(frame0b, layer0a),
                ProjectFile.Cell(frame0c, layer0a),
                ProjectFile.Cell(frame0c, layer0b),
            )

            1 -> listOf(
                ProjectFile.Thumbnail,
                ProjectFile.Background("jpg"),
                ProjectFile.Sound(sound1, "m4a"),
                ProjectFile.ReferenceImage(0, "png"),
                ProjectFile.ReferenceImage(1, "png"),
                ProjectFile.Cell(frame1a, layer1a),
                ProjectFile.Cell(frame1b, layer1a),
            )

            else -> error("No project $index in the sample")
        }
        return list.mapIndexed { position, file -> file to fixedBytes(index * 100L + position) }
    }

    /** 200 bytes that depend only on [seed]: java.util.Random is the same on every JVM. */
    private fun fixedBytes(seed: Long): ByteArray = ByteArray(FILE_SIZE).also { Random(seed).nextBytes(it) }
}

/**
 * Writes the content of `backup-v1.frameloop` to [out] and returns where each entry lies. Entry times are fixed (0),
 * so the file written again is the same byte for byte.
 */
fun writeSampleV1(out: OutputStream): List<SampleEntry> {
    val counting = CountingOutputStream(out)
    val zip = ZipOutputStream(counting)
    val positions = mutableListOf<SampleEntry>()

    fun entry(name: String, bytes: ByteArray) {
        val headerStart = counting.count
        zip.putNextEntry(ZipEntry(name).apply { time = 0 })
        val dataStart = counting.count
        zip.write(bytes)
        zip.closeEntry()
        zip.flush()
        positions += SampleEntry(name, headerStart, dataStart, counting.count)
    }

    val projects = BackupSample.projects()
    val manifest = projects.joinToString(",", prefix = """{"app":"frameloop","backupFormatVersion":1,"projects":[""") {
        """{"name":"${it.name}","frameCount":${it.frames.size}}"""
    } + "]}"
    entry("manifest.json", manifest.toByteArray())
    val fileCounts = mutableListOf<Int>()
    projects.forEachIndexed { index, project ->
        entry("projects/$index/project.json", ProjectFormat.encode(project).toByteArray())
        val files = BackupSample.files(index)
        files.forEach { (file, bytes) -> entry("projects/$index/${file.relativePath}", bytes) }
        fileCounts += files.size
    }
    entry("end.json", fileCounts.joinToString(",", """{"projects":[""", "]}") { """{"fileCount":$it}""" }.toByteArray())
    zip.finish()
    zip.flush()
    return positions
}

/** Counts the bytes written through it. */
private class CountingOutputStream(private val out: OutputStream) : OutputStream() {
    var count = 0L
        private set

    override fun write(b: Int) {
        out.write(b)
        count++
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        out.write(b, off, len)
        count += len
    }

    override fun flush() = out.flush()

    override fun close() = out.close()
}
