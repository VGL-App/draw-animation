package com.vgl.draw.animation.core.project.backup

import android.net.Uri
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
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
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.model.SoundClip
import com.vgl.draw.animation.core.project.model.SoundClipId
import com.vgl.draw.animation.core.project.model.SoundType
import com.vgl.draw.animation.core.project.storage.FileProjectStore
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectFormat
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The `.frameloop` format v1 (plan T-003): the sample file contract, the shared limits. */
@RunWith(RobolectricTestRunner::class)
class BackupFormatTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val logger = RecordingAppLogger()

    private val root: File by lazy { File(tmp.root, "projects") }

    private fun TestScope.store() = FileProjectStore(root, StandardTestDispatcher(testScheduler), logger)

    private fun TestScope.backup(store: FileProjectStore, streams: BackupStreams = FileBackupStreams()) =
        DefaultProjectBackup(
            store = store,
            repository = RefreshCountingRepository(),
            streams = streams,
            clock = { 1_000_000L },
            ioDispatcher = StandardTestDispatcher(testScheduler),
            logger = logger,
        )

    private fun uri(name: String): Uri = Uri.fromFile(File(tmp.root, name))

    private fun json(bytes: ByteArray) = Json.parseToJsonElement(bytes.decodeToString())

    @Test
    fun sampleV1_matchesWriter() {
        val written = ByteArrayOutputStream().also { writeSampleV1(it) }.toByteArray()
        val expected = entriesOf(written)
        val actual = sampleEntries()

        assertEquals(expected.map { it.first }, actual.map { it.first })
        expected.zip(actual).forEach { (e, a) -> assertArrayEquals(e.first, e.second, a.second) }
        assertEquals("end.json", actual.last().first)
        assertEquals(
            BackupEnd(listOf(BackupEndProject(6), BackupEndProject(7))),
            BackupFormat.decodeEnd(actual.last().second.decodeToString()),
        )
        val manifest = Manifest(
            app = "frameloop",
            backupFormatVersion = 1,
            projects = listOf(ManifestProject("Bouncing ball", 3), ManifestProject("Sunset walk", 2)),
        )
        assertEquals(ManifestResult.Ok(manifest), BackupFormat.decodeManifest(actual.first().second.decodeToString()))
        // Control: the sample really holds the two projects and their files.
        assertEquals(1 + 2 + 6 + 7 + 1, actual.size)
    }

    @Test
    fun export_matchesV1Layout() = runTest {
        val store = store()
        BackupSample.projects().forEachIndexed { index, project ->
            store.write(project)
            BackupSample.files(index).forEach { (file, bytes) -> store.writeFile(project.id, file, bytes) }
        }
        val target = uri("out.frameloop")

        val result = backup(store).export(target, listOf(BackupSample.ID_0, BackupSample.ID_1))

        assertEquals(BackupExportResult.Success(2), result)
        val exported = entriesOf(File(target.path!!).readBytes())
        val sample = sampleEntries()
        assertEquals(sample.map { it.first }, exported.map { it.first })
        assertEquals(json(sample.first().second), json(exported.first().second))
        assertEquals(json(sample.last().second), json(exported.last().second))
    }

    @Test
    fun manifest_unknownField_ignored() = runTest {
        val store = store()
        val entries = sampleEntries()
        val manifest = (json(entries.first().second) as JsonObject).toMutableMap()
        manifest["createdBy"] = JsonPrimitive("Frameloop 9.9")
        val file = File(tmp.root, "extra.frameloop")
        file.writeBytes(zipOf(entries.replace("manifest.json", JsonObject(manifest).toString().toByteArray())))
        // Control: the sample as is restores too, so the unknown field is what this test changes.
        val plain = File(tmp.root, "plain.frameloop").also { it.writeBytes(sampleBytes()) }
        assertTrue(backup(store).restore(Uri.fromFile(plain)) {} is RestoreResult.Success)

        val result = backup(store).restore(Uri.fromFile(file)) {}

        assertTrue("$result", result is RestoreResult.Success)
        assertEquals(2, (result as RestoreResult.Success).projectIds.size)
    }

    @Test
    fun maxBytesFor_table() {
        val frame = FrameId.random()
        val layer = LayerId.random()
        assertEquals(8L shl 20, BackupFormat.maxBytesFor(ProjectFile.Thumbnail))
        assertEquals(32L shl 20, BackupFormat.maxBytesFor(ProjectFile.Cell(frame, layer)))
        assertEquals(64L shl 20, BackupFormat.maxBytesFor(ProjectFile.Background("jpg")))
        assertEquals(64L shl 20, BackupFormat.maxBytesFor(ProjectFile.ReferenceImage(3, "png")))
        assertEquals(64L shl 20, BackupFormat.maxBytesFor(ProjectFile.Sound(SoundClipId.random(), "m4a")))
    }

    @Test
    fun projectJsonCap_fitsFrameLimit() {
        val largest = largestProject(BackupFormat.MAX_FRAMES_PER_PROJECT)

        assertNull(BackupFormat.limitViolation(largest))
        val size = ProjectFormat.encode(largest).toByteArray(Charsets.UTF_8).size
        assertTrue("$size", size < BackupFormat.MAX_PROJECT_JSON_BYTES)

        val oneFrameMore = largest.copy(frames = largest.frames + largest.frames.last().copy(id = FrameId.random()))
        assertNotNull(BackupFormat.limitViolation(oneFrameMore))
    }

    /** The largest project the limits allow: every frame held 4 with a camera key, 10 layers, every sound. */
    private fun largestProject(frameCount: Int): Project {
        val key = CameraKey(-1.2345679E-30f, -1.2345679E-30f, 1.2345679E30f, -1.2345679E-30f)
        val frames = List(frameCount) { Frame(FrameId.random(), hold = 4, cameraKey = key) }
        return Project(
            id = ProjectId.random(),
            kind = ProjectKind.Regular,
            name = "n".repeat(40),
            aspectRatio = AspectRatio.LANDSCAPE_16_9,
            resolution = Resolution.P1080,
            frameRate = FrameRate.FPS_30,
            background = Background.Solid(BackgroundColor.WHITE),
            layers = List(10) { Layer(LayerId.random(), "l".repeat(40), visible = true) },
            frames = frames,
            sounds = List(BackupFormat.MAX_SOUNDS) {
                SoundClip(
                    id = SoundClipId.random(),
                    type = SoundType.EFFECT,
                    title = "t".repeat(100),
                    fileExtension = "m4a",
                    startFrameId = frames[it].id,
                    durationMillis = Long.MAX_VALUE,
                    effectSource = EffectSource(packId = "cartoon-pack", soundId = "pop-sound"),
                )
            },
            camera = CameraSettings(enabled = true),
            onionSkin = OnionSkinSettings(),
            reference = null,
            extraLayersUnlocked = true,
            createdAtMillis = Long.MAX_VALUE,
            updatedAtMillis = Long.MAX_VALUE,
        )
    }
}
