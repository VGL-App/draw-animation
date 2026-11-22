package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.TestProjects
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.SoundClipId
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FileProjectStoreTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val logger = RecordingAppLogger()

    private val root: File by lazy { File(tmp.root, "projects") }

    private fun TestScope.store(dir: File = root): FileProjectStore =
        FileProjectStore(dir, StandardTestDispatcher(testScheduler), logger)

    private fun projectDir(id: ProjectId) = File(root, id.value)

    private fun tempFiles(dir: File): List<File> = dir.walk().filter { it.name.endsWith(".tmp") }.toList()

    private fun newProject(name: String = "My animation 3"): Project =
        TestProjects.full().copy(id = ProjectId.random(), name = name)

    @Test
    fun writeThenRead_returnsEqualProject() = runTest {
        val store = store()
        val full = TestProjects.full()

        store.write(full)

        assertEquals(ProjectReadResult.Ok(full), store.read(full.id))
        assertTrue(File(root, "${full.id.value}/project.json").isFile)
    }

    @Test
    fun read_missingProject_returnsNotFound() = runTest {
        val store = store()
        assertEquals(ProjectReadResult.NotFound, store.read(ProjectId.random()))

        val withoutMetadata = ProjectId.random()
        assertTrue(projectDir(withoutMetadata).mkdirs())
        assertEquals(ProjectReadResult.NotFound, store.read(withoutMetadata))
    }

    @Test
    fun read_idMismatch_returnsCorrupt() = runTest {
        val store = store()
        val a = TestProjects.full()
        store.write(a)
        val b = ProjectId.random()
        assertTrue(projectDir(b).mkdirs())
        File(projectDir(a.id), "project.json").copyTo(File(projectDir(b), "project.json"))

        assertEquals(ProjectReadResult.Corrupt("My animation 3", CorruptReason.INVALID_CONTENT), store.read(b))
    }

    @Test
    fun write_overwrite_replacesAndLeavesNoTempFile() = runTest {
        val store = store()
        val full = TestProjects.full()

        store.write(full.copy(name = "First"))
        store.write(full.copy(name = "Second"))

        assertEquals(ProjectReadResult.Ok(full.copy(name = "Second")), store.read(full.id))
        assertEquals(emptyList<File>(), tempFiles(root))
    }

    @Test
    fun write_tempFails_throwsIOExceptionAndKeepsPrevious() = runTest {
        val store = store()
        val full = TestProjects.full()
        store.write(full.copy(name = "First"))
        // A non-empty directory where the temp file goes: it cannot be deleted or opened for writing, even by root.
        val blocker = File(projectDir(full.id), "project.json.tmp")
        assertTrue(blocker.mkdir())
        File(blocker, "inside").writeText("x")

        try {
            store.write(full.copy(name = "Second"))
            fail("write succeeded")
        } catch (error: Exception) {
            assertTrue("$error", error is IOException)
        }

        assertEquals(ProjectReadResult.Ok(full.copy(name = "First")), store.read(full.id))
    }

    @Test
    fun read_ignoresLeftoverTempFile() = runTest {
        val store = store()
        val full = TestProjects.full()
        store.write(full)
        File(projectDir(full.id), "project.json.tmp").writeText("garbage{")

        assertEquals(ProjectReadResult.Ok(full), store.read(full.id))
    }

    @Test
    fun write_invalidProject_throwsAndKeepsPrevious() = runTest {
        val store = store()
        val full = TestProjects.full()
        store.write(full)

        try {
            store.write(full.copy(frames = emptyList()))
            fail("invalid project was written")
        } catch (error: Exception) {
            assertTrue("$error", error is IllegalArgumentException)
        }

        assertEquals(ProjectReadResult.Ok(full), store.read(full.id))
        assertEquals(emptyList<File>(), tempFiles(root))
    }

    @Test
    fun write_concurrentSameProject_endsWithOneWholeWrite() {
        val executor = Executors.newFixedThreadPool(4)
        val dispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()
        try {
            val store = FileProjectStore(root, dispatcher, logger)
            val full = TestProjects.full()
            runBlocking {
                (0 until 20).map { i -> async(dispatcher) { store.write(full.copy(name = "n$i")) } }.awaitAll()
                val result = store.read(full.id)
                assertTrue("$result", result is ProjectReadResult.Ok)
                val name = (result as ProjectReadResult.Ok).project.name
                assertTrue(name, name in (0 until 20).map { "n$it" })
                assertEquals(full.copy(name = name), result.project)
            }
            assertEquals(emptyList<File>(), tempFiles(root))
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun readAll_hundredProjectsOneCorrupt_othersOk() = runTest {
        val store = store()
        val projects = (0 until 100).map { i -> newProject(name = "P$i") }
        projects.forEach { store.write(it) }
        val broken = projects[42]
        File(projectDir(broken.id), "project.json").writeText("{")

        val ids = store.ids()
        assertEquals(projects.map { it.id }.toSet(), ids.toSet())
        assertEquals(100, ids.size)

        val results = projects.associate { it.id to store.read(it.id) }
        val ok = results.filterValues { it is ProjectReadResult.Ok }
        assertEquals(99, ok.size)
        projects.filter { it != broken }.forEach { assertEquals(ProjectReadResult.Ok(it), results.getValue(it.id)) }
        assertTrue("${results[broken.id]}", results[broken.id] is ProjectReadResult.Corrupt)
        assertTrue(projectDir(broken.id).isDirectory)
    }

    @Test
    fun read_unreadableFile_returnsCorrupt() = runTest {
        val store = store()
        val id = ProjectId.random()
        assertTrue(File(projectDir(id), "project.json").mkdirs())

        assertEquals(ProjectReadResult.Corrupt(null, CorruptReason.UNREADABLE_FILE), store.read(id))
    }

    @Test
    fun ids_listsOnlyProjectDirsWithMetadata() = runTest {
        // Control: no root yet.
        assertEquals(emptyList<ProjectId>(), store().ids())

        val first = ProjectId.random()
        val second = ProjectId.random()
        fun dirWithMetadata(name: String) {
            val dir = File(root, name)
            assertTrue(dir.mkdirs())
            File(dir, "project.json").writeText("{}")
        }
        dirWithMetadata(first.value)
        dirWithMetadata(second.value)
        assertTrue(File(root, UUID.randomUUID().toString()).mkdirs())
        dirWithMetadata(".deleting-${UUID.randomUUID()}")
        dirWithMetadata(".restore-x")
        dirWithMetadata("abc")
        dirWithMetadata("1-1-1-1-1")
        dirWithMetadata(UUID.randomUUID().toString().uppercase())
        File(root, "${UUID.randomUUID()}.txt").writeText("x")

        assertEquals(setOf(first, second), store().ids().toSet())
        assertEquals(2, store().ids().size)
    }

    @Test
    fun writeFileThenRead_eachKind_returnsSameBytes() = runTest {
        val store = store()
        val id = ProjectId.random()
        val frameId = FrameId.random()
        val layerId = LayerId.random()
        val clipId = SoundClipId.random()
        val files = listOf(
            ProjectFile.Thumbnail to "thumbnail.png",
            ProjectFile.Background("jpg") to "background.jpg",
            ProjectFile.Cell(frameId, layerId) to "cells/${frameId.value}/${layerId.value}.png",
            ProjectFile.Sound(clipId, "m4a") to "sounds/${clipId.value}.m4a",
            ProjectFile.ReferenceImage(7, "jpg") to "reference/7.jpg",
        )

        files.forEachIndexed { index, (file, path) ->
            val bytes = byteArrayOf(index.toByte(), 1, 2, 3)
            store.writeFile(id, file, bytes)
            assertArrayEquals("$file", bytes, store.readFile(id, file))
            assertArrayEquals("$file on disk", bytes, File(projectDir(id), path).readBytes())
        }
        assertEquals(emptyList<File>(), tempFiles(root))
    }

    @Test
    fun readFile_missing_returnsNull() = runTest {
        val store = store()
        val full = TestProjects.full()
        store.write(full)

        assertNull(store.readFile(full.id, ProjectFile.Thumbnail))
        assertNull(store.readFile(ProjectId.random(), ProjectFile.Background("png")))
    }

    @Test
    fun deleteFile_removesOnlyThatFile() = runTest {
        val store = store()
        val id = ProjectId.random()
        val frameId = FrameId.random()
        val removed = ProjectFile.Cell(frameId, LayerId.random())
        val kept = ProjectFile.Cell(frameId, LayerId.random())
        store.writeFile(id, removed, byteArrayOf(1))
        store.writeFile(id, kept, byteArrayOf(2))

        store.deleteFile(id, removed)

        assertNull(store.readFile(id, removed))
        assertFalse(File(projectDir(id), removed.relativePath).exists())
        assertArrayEquals(byteArrayOf(2), store.readFile(id, kept))
    }

    @Test
    fun deleteFile_missing_doesNothing() = runTest {
        val store = store()
        val full = TestProjects.full()
        store.write(full)

        store.deleteFile(full.id, ProjectFile.Thumbnail)
        store.deleteFile(ProjectId.random(), ProjectFile.Thumbnail)

        assertEquals(ProjectReadResult.Ok(full), store.read(full.id))
    }

    @Test
    fun delete_removesWholeDirectory() = runTest {
        val store = store()
        val deleted = newProject()
        val other = newProject()
        store.write(deleted)
        store.writeFile(deleted.id, ProjectFile.Thumbnail, byteArrayOf(1))
        store.write(other)

        store.delete(deleted.id)

        assertFalse(projectDir(deleted.id).exists())
        assertFalse(File(root, ".deleting-${deleted.id.value}").exists())
        assertEquals(ProjectReadResult.NotFound, store.read(deleted.id))
        assertEquals(ProjectReadResult.Ok(other), store.read(other.id))
        assertEquals(listOf(other.id), store.ids())

        // Deleting a project that does not exist does nothing.
        store.delete(ProjectId.random())
        assertEquals(ProjectReadResult.Ok(other), store.read(other.id))
    }

    @Test
    fun delete_cleansLeftoverDeletingDirs() = runTest {
        val store = store()
        val leftover = File(root, ".deleting-${UUID.randomUUID()}")
        assertTrue(File(leftover, "cells").mkdirs())
        File(leftover, "cells/a.png").writeText("x")
        File(leftover, "project.json").writeText("{}")

        store.delete(ProjectId.random())

        assertFalse(leftover.exists())
    }

    @Test
    fun filesWithoutMetadata_notListed() = runTest {
        val store = store()
        val project = newProject()

        store.writeFile(project.id, ProjectFile.Background("png"), byteArrayOf(1, 2))

        assertFalse(project.id in store.ids())
        assertEquals(ProjectReadResult.NotFound, store.read(project.id))

        store.write(project)
        assertTrue(project.id in store.ids())
    }

    @Test
    fun photoBackground_storedInsideProject() = runTest {
        val store = store()
        val project = newProject().copy(background = Background.Photo("png"))
        val source = File(tmp.newFolder("gallery"), "photo.png")
        source.writeBytes(byteArrayOf(9, 8, 7, 6, 5))
        val bytes = source.readBytes()

        store.write(project)
        store.writeFile(project.id, ProjectFile.Background("png"), bytes)
        assertTrue(source.delete())

        assertArrayEquals(byteArrayOf(9, 8, 7, 6, 5), store.readFile(project.id, ProjectFile.Background("png")))
        assertEquals(ProjectReadResult.Ok(project), store.read(project.id))
    }

    @Test
    fun rootThroughSymlink_writeAndReadWork() = runTest {
        val real = tmp.newFolder("real")
        val link = File(tmp.root, "link")
        Files.createSymbolicLink(link.toPath(), real.toPath())
        val store = store(link)
        val full = TestProjects.full()

        store.write(full)
        store.writeFile(full.id, ProjectFile.Thumbnail, byteArrayOf(4, 2))

        assertEquals(ProjectReadResult.Ok(full), store.read(full.id))
        assertArrayEquals(byteArrayOf(4, 2), store.readFile(full.id, ProjectFile.Thumbnail))
        assertTrue(File(real, "${full.id.value}/project.json").isFile)
    }

    @Test
    fun read_corrupt_logsWarningWithoutNameOrId() = runTest {
        val store = store()
        val valid = newProject()
        store.write(valid)

        // Control: reading a valid project logs no warning.
        assertTrue(store.read(valid.id) is ProjectReadResult.Ok)
        assertEquals(emptyList<Any>(), logger.entries.filter { it.level == LogLevel.WARN })

        val corrupt = ProjectId.random()
        writeRaw(corrupt, sampleWith(id = corrupt, name = "Secret name", version = 1, emptyFrames = true))
        assertTrue(store.read(corrupt) is ProjectReadResult.Corrupt)
        val warnings = logger.entries.filter { it.level == LogLevel.WARN }
        assertEquals(1, warnings.size)
        val warning = warnings.single()
        assertEquals("Project", warning.tag)
        assertTrue(warning.message, "INVALID_CONTENT" in warning.message)
        assertFalse(warning.message, "Secret name" in warning.message)
        assertFalse(warning.message, corrupt.value in warning.message)

        val newer = ProjectId.random()
        writeRaw(newer, sampleWith(id = newer, name = "Secret name", version = 2, emptyFrames = false))
        assertEquals(ProjectReadResult.NewerVersion(2, "Secret name"), store.read(newer))
        val newerWarnings = logger.entries.filter { it.level == LogLevel.WARN }
        assertEquals(2, newerWarnings.size)
        val newerWarning = newerWarnings.last()
        assertEquals("Project", newerWarning.tag)
        assertTrue(newerWarning.message, "2" in newerWarning.message)
        assertFalse(newerWarning.message, "Secret name" in newerWarning.message)
        assertFalse(newerWarning.message, newer.value in newerWarning.message)
    }

    @Test
    fun operations_runOnIoDispatcher() {
        val io = StandardTestDispatcher()
        val store = FileProjectStore(root, io, logger)
        val full = TestProjects.full()
        val file = File(projectDir(full.id), "project.json")
        assertTrue(file.parentFile!!.mkdirs())
        file.writeText(ProjectFormat.encode(full))

        runTest {
            val d = async { store.read(full.id) }
            runCurrent()
            assertFalse(d.isCompleted)
            io.scheduler.advanceUntilIdle()
            runCurrent()
            assertTrue(d.await() is ProjectReadResult.Ok)
        }
    }

    private fun writeRaw(id: ProjectId, text: String) {
        val dir = projectDir(id)
        assertTrue(dir.mkdirs())
        File(dir, "project.json").writeText(text)
    }

    /** The v1 sample with another id, name and format version, and optionally no frames; built as text. */
    private fun sampleWith(id: ProjectId, name: String, version: Int, emptyFrames: Boolean): String {
        val rootMap = (Json.parseToJsonElement(TestProjects.v1SampleText()) as JsonObject).toMutableMap()
        val project = (rootMap.getValue("project") as JsonObject).toMutableMap()
        project["id"] = JsonPrimitive(id.value)
        project["name"] = JsonPrimitive(name)
        if (emptyFrames) project["frames"] = JsonArray(emptyList())
        rootMap["project"] = JsonObject(project)
        rootMap["formatVersion"] = JsonPrimitive(version)
        return JsonObject(rootMap).toString()
    }
}
