package com.vgl.draw.animation.core.project.backup

import android.net.Uri
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.TestProjects
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.Frame
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Layer
import com.vgl.draw.animation.core.project.model.LayerId
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.ProjectKind
import com.vgl.draw.animation.core.project.model.ReferenceLayer
import com.vgl.draw.animation.core.project.model.ReferenceType
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.model.SoundClip
import com.vgl.draw.animation.core.project.model.SoundClipId
import com.vgl.draw.animation.core.project.model.SoundType
import com.vgl.draw.animation.core.project.storage.FileProjectStore
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectFormat
import com.vgl.draw.animation.core.project.storage.ProjectImport
import com.vgl.draw.animation.core.project.storage.ProjectReadResult
import com.vgl.draw.animation.core.project.storage.ProjectStore
import com.vgl.draw.animation.core.project.storage.referencedFiles
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.Base64
import java.util.Random
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Backing up and restoring `.frameloop` files (plan T-003), on a real [FileProjectStore] in a temporary folder and
 * `file://` uris. Both the store and the backup run on a dispatcher of the test's own scheduler.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class DefaultProjectBackupTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val logger = RecordingAppLogger()
    private val streams = FileBackupStreams()
    private val repository = RefreshCountingRepository()

    private val root: File by lazy { File(tmp.root, "projects") }

    private fun TestScope.store(): FileProjectStore =
        FileProjectStore(root, StandardTestDispatcher(testScheduler), logger)

    private fun TestScope.backup(
        store: ProjectStore,
        streams: BackupStreams = this@DefaultProjectBackupTest.streams,
        newId: () -> ProjectId = { ProjectId.random() },
    ): DefaultProjectBackup = DefaultProjectBackup(
        store = store,
        repository = repository,
        streams = streams,
        clock = { NOW },
        ioDispatcher = StandardTestDispatcher(testScheduler),
        logger = logger,
        newId = newId,
    )

    private fun file(name: String) = File(tmp.root, name)

    private fun uri(name: String): Uri = Uri.fromFile(file(name))

    private fun backupFile(name: String, bytes: ByteArray): Uri {
        file(name).writeBytes(bytes)
        return uri(name)
    }

    private fun stagingDirs(): List<File> = root.listFiles().orEmpty().filter { it.name.startsWith(".restoring-") }

    private fun stagedFileCount(): Int = stagingDirs().sumOf { dir -> dir.walk().count { it.isFile } }

    private fun assertNoStaging() = assertEquals(emptyList<File>(), stagingDirs())

    /** Names of the folders under [root] that are project ids. */
    private fun idDirs(): Set<String> =
        root.listFiles().orEmpty().map { it.name }.filter { ProjectId.parseOrNull(it) != null }.toSet()

    private fun project(
        name: String = "Cat",
        frames: Int = 1,
        layers: Int = 1,
        updatedAt: Long = 100L,
        kind: ProjectKind = ProjectKind.Regular,
    ): Project = Project.create(
        id = ProjectId.random(),
        name = name,
        aspectRatio = AspectRatio.PORTRAIT_9_16,
        resolution = Resolution.P720,
        frameRate = FrameRate.FPS_12,
        background = Background.Solid(BackgroundColor.WHITE),
        firstLayerName = "Layer 1",
        nowMillis = updatedAt,
        kind = kind,
    ).copy(
        layers = List(layers) { Layer(LayerId.random(), "Layer ${it + 1}", visible = true) },
        frames = List(frames) { Frame(FrameId.random()) },
    )

    /** Writes [project] and the cells [hasCell] selects, each holding `"cell-<frame>-<layer>"`. */
    private suspend fun writeWithCells(
        store: ProjectStore,
        project: Project,
        hasCell: (frame: Int, layer: Int) -> Boolean = { _, _ -> true },
    ) {
        store.write(project)
        project.frames.forEachIndexed { f, frame ->
            project.layers.forEachIndexed { l, layer ->
                val cell = ProjectFile.Cell(frame.id, layer.id)
                if (hasCell(f, l)) store.writeFile(project.id, cell, "cell-$f-$l".toByteArray())
            }
        }
    }

    /** Restores [source] and checks the failure leaves nothing: same ids, no staging folder, no refresh. */
    private suspend fun assertRestoreFails(
        label: String,
        backup: DefaultProjectBackup,
        source: Uri,
        expected: RestoreFailure,
        store: ProjectStore,
    ) {
        val idsBefore = store.ids()
        val dirsBefore = idDirs()
        val refreshBefore = repository.count

        val result = backup.restore(source) {}

        assertEquals(label, RestoreResult.Failure(expected), result)
        assertEquals(label, idsBefore, store.ids())
        assertEquals(label, dirsBefore, idDirs())
        assertEquals(label, emptyList<File>(), stagingDirs())
        assertEquals(label, refreshBefore, repository.count)
    }

    private fun Project.cell(frame: Int, layer: Int) = ProjectFile.Cell(frames[frame].id, layers[layer].id)

    private fun sha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun manifestNames(zip: ByteArray): List<String> {
        val manifest = Json.parseToJsonElement(entriesOf(zip).first().second.decodeToString()).jsonObject
        return manifest.getValue("projects").jsonArray.map { it.jsonObject.getValue("name").jsonPrimitive.content }
    }

    /** The sample written by [writeSampleV1], and where its entries lie. */
    private fun writerSample(): Pair<ByteArray, List<SampleEntry>> {
        val out = ByteArrayOutputStream()
        val positions = writeSampleV1(out)
        return out.toByteArray() to positions
    }

    private fun List<SampleEntry>.named(name: String): SampleEntry = single { it.name == name }

    private fun cellEntry(index: Int, file: ProjectFile) = "projects/$index/${file.relativePath}"

    /** Entry names of the cells of project 0 of the sample, in file order. */
    private val sampleCells0: List<String>
        get() = BackupSample.files(0).map { it.first }.filterIsInstance<ProjectFile.Cell>().map { cellEntry(0, it) }

    /** [entries] with `project.json` of project [index] changed by [edit] (the "project" object) or [editRoot]. */
    private fun editProject(
        entries: List<Entry>,
        index: Int,
        editRoot: (MutableMap<String, JsonElement>) -> Unit = {},
        edit: (MutableMap<String, JsonElement>) -> Unit = {},
    ): List<Entry> {
        val name = "projects/$index/project.json"
        val rootMap = Json.parseToJsonElement(entries.single { it.first == name }.second.decodeToString())
            .jsonObject.toMutableMap()
        val project = rootMap.getValue("project").jsonObject.toMutableMap()
        edit(project)
        rootMap["project"] = JsonObject(project)
        editRoot(rootMap)
        return entries.replace(name, JsonObject(rootMap).toString().toByteArray())
    }

    /**
     * Writes a ZIP file entry by entry, without holding big entries in memory: an entry named in [big] gets that many
     * zero bytes instead of its content.
     */
    private fun writeZipFile(target: File, entries: List<Entry>, big: Map<String, Long> = emptyMap()): Uri {
        ZipOutputStream(FileOutputStream(target).buffered()).use { zip ->
            val chunk = ByteArray(CHUNK)
            for ((name, content) in entries) {
                zip.putNextEntry(ZipEntry(name))
                val size = big[name]
                if (size == null) {
                    zip.write(content)
                } else {
                    var left = size
                    while (left > 0) {
                        val count = minOf(left, CHUNK.toLong()).toInt()
                        zip.write(chunk, 0, count)
                        left -= count
                    }
                }
                zip.closeEntry()
            }
        }
        return Uri.fromFile(target)
    }

    @Test
    fun exportThenRestore_oneProject_identical() = runTest {
        val store = store()
        val original = TestProjects.full().copy(kind = ProjectKind.Regular)
        store.write(original)
        val empty = original.cell(2, 1)
        original.referencedFiles().filter { it != empty }.forEachIndexed { index, file ->
            store.writeFile(original.id, file, "file-$index-${file.relativePath}".toByteArray())
        }
        val newId = ProjectId.random()
        val backup = backup(store, newId = { newId })

        assertEquals(BackupExportResult.Success(1), backup.export(uri("one.frameloop"), listOf(original.id)))
        val result = backup.restore(uri("one.frameloop")) {}

        assertEquals(RestoreResult.Success(listOf(newId)), result)
        assertNotEquals(original.id, newId)
        assertEquals(ProjectReadResult.Ok(original.copy(id = newId, updatedAtMillis = NOW)), store.read(newId))
        for (file in original.referencedFiles()) {
            val expected = store.readFile(original.id, file)
            assertArrayEquals(file.relativePath, expected, store.readFile(newId, file))
        }
        assertNull(store.readFile(newId, empty))
    }

    @Test
    fun exportThenRestore_largeSound_roundTrips() = runTest {
        val store = store()
        val clip = SoundClip(
            id = SoundClipId.random(),
            type = SoundType.MUSIC,
            title = "Song",
            fileExtension = "m4a",
            startFrameId = null,
            durationMillis = 60_000,
            effectSource = null,
        )
        val song = project("Song").copy(sounds = listOf(clip))
        store.write(song)
        val soundFile = ProjectFile.Sound(clip.id, "m4a")
        val size = 17 shl 20
        val bytes = ByteArray(size) { ((it / CHUNK) * 31 + it).toByte() }
        val expectedHash = sha256(bytes)
        store.writeFile(song.id, soundFile, bytes)
        val backup = backup(store)

        assertEquals(BackupExportResult.Success(1), backup.export(uri("song.frameloop"), listOf(song.id)))
        val result = backup.restore(uri("song.frameloop")) {}

        assertTrue("$result", result is RestoreResult.Success)
        val restoredId = (result as RestoreResult.Success).projectIds.single()
        val restored = checkNotNull(store.readFile(restoredId, soundFile))
        assertEquals(size, restored.size)
        assertEquals(expectedHash, sha256(restored))
    }

    @Test
    fun exportAllThenRestore_threeProjects_allRestoredWithTotalFrames() = runTest {
        val store = store()
        val one = project("One", frames = 1, updatedAt = 300)
        val three = project("Three", frames = 3, updatedAt = 100)
        val five = project("Five", frames = 5, updatedAt = 200)
        val lesson = project("Lesson", frames = 2, updatedAt = 400, kind = ProjectKind.Lesson("walk-cycle", 0))
        listOf(one, three, five, lesson).forEach { writeWithCells(store, it) }
        val backup = backup(store)

        assertEquals(BackupExportResult.Success(3), backup.exportAll(uri("all.frameloop")))
        assertEquals(listOf("One", "Five", "Three"), manifestNames(file("all.frameloop").readBytes()))
        listOf(one, three, five).forEach { store.delete(it.id) }
        val existing = project("Existing", updatedAt = 900_000)
        store.write(existing)
        val progress = mutableListOf<RestoreProgress>()

        val result = backup.restore(uri("all.frameloop")) { progress += it }

        assertEquals(RestoreProgress(BackupSummary(3, null, 9), 0), progress.first())
        val done = progress.map { it.doneFrames }
        assertEquals(done.sorted().distinct(), done)
        assertEquals(9, done.last())
        assertTrue("$result", result is RestoreResult.Success)
        val ids = (result as RestoreResult.Success).projectIds
        assertEquals(3, ids.size)
        val restored = ids.map { (store.read(it) as ProjectReadResult.Ok).project }
        assertEquals(listOf("One", "Five", "Three"), restored.map { it.name })
        assertEquals(listOf(1, 5, 3), restored.map { it.frames.size })
        assertEquals(listOf(NOW, NOW - 1, NOW - 2), restored.map { it.updatedAtMillis })
        assertTrue(
            restored.all {
                it.updatedAtMillis > existing.updatedAtMillis &&
                    it.updatedAtMillis > lesson.updatedAtMillis
            },
        )
        assertEquals((ids + existing.id + lesson.id).toSet(), store.ids().toSet())
        assertTrue(restored.all { it.kind == ProjectKind.Regular })
    }

    @Test
    fun restore_oneProject_summaryHasName() = runTest {
        val store = store()
        val cat = project("Cat", frames = 2)
        writeWithCells(store, cat)
        val backup = backup(store)
        assertEquals(BackupExportResult.Success(1), backup.export(uri("cat.frameloop"), listOf(cat.id)))
        var first: RestoreProgress? = null
        var stagedAtFirst = -1

        val result = backup.restore(uri("cat.frameloop")) {
            if (first == null) {
                first = it
                stagedAtFirst = stagedFileCount()
            }
        }

        assertTrue("$result", result is RestoreResult.Success)
        assertEquals(RestoreProgress(BackupSummary(1, "Cat", 2), 0), first)
        assertEquals(0, stagedAtFirst)
    }

    @Test
    fun restore_progress_increasesPerFrame() = runTest {
        val store = store()
        val everyCell = project("Every", frames = 4)
        writeWithCells(store, everyCell)
        val oneCell = project("One", frames = 4)
        writeWithCells(store, oneCell) { frame, _ -> frame == 2 }
        val backup = backup(store)
        assertEquals(BackupExportResult.Success(1), backup.export(uri("every.frameloop"), listOf(everyCell.id)))
        assertEquals(BackupExportResult.Success(1), backup.export(uri("one.frameloop"), listOf(oneCell.id)))

        val everyDone = mutableListOf<Int>()
        backup.restore(uri("every.frameloop")) { everyDone += it.doneFrames }
        val oneDone = mutableListOf<Int>()
        backup.restore(uri("one.frameloop")) { oneDone += it.doneFrames }

        assertEquals(listOf(0, 1, 2, 3, 4), everyDone)
        assertEquals(listOf(0, 2, 4), oneDone)
    }

    @Test
    fun restore_lessonInFile_skippedButCounted() = runTest {
        val store = store()
        val regular = project("Regular", frames = 2)
        val lesson = project("Lesson", frames = 3, kind = ProjectKind.Lesson("walk-cycle", 1))
        val zip = zipOf(
            "manifest.json" to manifestJson(listOf("Regular" to 2, "Lesson" to 3)).toByteArray(),
            "projects/0/project.json" to ProjectFormat.encode(regular).toByteArray(),
            cellEntry(0, regular.cell(0, 0)) to byteArrayOf(1),
            cellEntry(0, regular.cell(1, 0)) to byteArrayOf(2),
            "projects/1/project.json" to ProjectFormat.encode(lesson).toByteArray(),
            cellEntry(1, lesson.cell(1, 0)) to byteArrayOf(3),
            "end.json" to endJson(listOf(2, 1)).toByteArray(),
        )
        val idsBefore = store.ids()
        val progress = mutableListOf<RestoreProgress>()

        val result = backup(store).restore(backupFile("lesson.frameloop", zip)) { progress += it }

        assertTrue("$result", result is RestoreResult.Success)
        val ids = (result as RestoreResult.Success).projectIds
        assertEquals(1, ids.size)
        assertEquals(5, progress.first().summary.totalFrames)
        assertEquals(5, progress.last().doneFrames)
        assertEquals((idsBefore + ids).toSet(), store.ids().toSet())
        assertEquals("Regular", (store.read(ids.single()) as ProjectReadResult.Ok).project.name)
    }

    @Test
    fun restore_success_refreshesRepositoryOnce() = runTest {
        val store = store()

        val result = backup(store).restore(backupFile("sample.frameloop", sampleBytes())) {}

        assertTrue("$result", result is RestoreResult.Success)
        assertEquals(1, repository.count)
    }

    @Test
    fun restore_projectStillPresent_createsNewOneAlongside() = runTest {
        val store = store()
        val cat = project("Cat", frames = 2)
        writeWithCells(store, cat)
        val backup = backup(store)
        assertEquals(BackupExportResult.Success(1), backup.export(uri("cat.frameloop"), listOf(cat.id)))

        val result = backup.restore(uri("cat.frameloop")) {}

        val newId = (result as RestoreResult.Success).projectIds.single()
        assertEquals(setOf(cat.id, newId), store.ids().toSet())
        assertEquals(ProjectReadResult.Ok(cat), store.read(cat.id))
        assertEquals("Cat", (store.read(newId) as ProjectReadResult.Ok).project.name)
    }

    @Test
    fun restore_cancelledMidway_leavesNothing() = runTest {
        val store = store()
        val projects = listOf(project("A", frames = 2), project("B", frames = 2), project("C", frames = 2))
        projects.forEach { writeWithCells(store, it) }
        val backup = backup(store)
        assertEquals(BackupExportResult.Success(3), backup.export(uri("three.frameloop"), projects.map { it.id }))
        val idsBefore = store.ids()
        val framesOfFirst = projects.first().frames.size
        var staged = -1

        lateinit var job: Job
        job = launch {
            backup.restore(uri("three.frameloop")) { p ->
                if (p.doneFrames == framesOfFirst && staged < 0) {
                    staged = stagedFileCount()
                    job.cancel()
                }
            }
        }
        advanceUntilIdle()

        assertTrue("$staged", staged > 0)
        assertTrue(job.isCancelled)
        assertEquals(idsBefore, store.ids())
        assertNoStaging()
        assertEquals(0, repository.count)
        assertTrue(logger.entries.any { it.level == LogLevel.INFO && it.message == "Restore cancelled" })
    }

    @Test
    fun restore_twoConcurrent_bothSucceed() = runTest {
        val store = store()
        val source = backupFile("sample.frameloop", sampleBytes())
        val backup = backup(store)

        val results = listOf(async { backup.restore(source) {} }, async { backup.restore(source) {} }).awaitAll()

        val ids = results.map { (it as RestoreResult.Success).projectIds }
        assertEquals(listOf(2, 2), ids.map { it.size })
        assertEquals(4, ids.flatten().toSet().size)
        assertEquals(ids.flatten().toSet(), store.ids().toSet())
        for (run in ids) {
            run.forEachIndexed { index, id ->
                assertTrue(store.read(id) is ProjectReadResult.Ok)
                BackupSample.files(index).forEach { (file, bytes) ->
                    assertArrayEquals(bytes, store.readFile(id, file))
                }
            }
        }
        assertNoStaging()
        assertEquals(2, repository.count)
    }

    @Test
    fun restore_cancelledWhileReadingManifest_noStagingCreated() = runTest {
        val store = store()
        val source = backupFile("sample.frameloop", sampleBytes())
        val idsBefore = store.ids()
        lateinit var job: Job
        var hook: HookInputStream? = null
        streams.wrapRead = { input -> HookInputStream(input) { job.cancel() }.also { hook = it } }
        var progressCalls = 0

        job = launch { backup(store).restore(source) { progressCalls++ } }
        advanceUntilIdle()

        assertTrue(hook?.wasRead == true)
        assertTrue(job.isCancelled)
        assertNoStaging()
        assertEquals(0, progressCalls)
        assertEquals(idsBefore, store.ids())
    }

    @Test
    fun restore_cleanupDuringRestore_doesNotTouchStaging() = runTest {
        val store = store()
        var stagedAtCleanup = -1
        var abandonedRemoved = -1

        val result = backup(store).restore(backupFile("sample.frameloop", sampleBytes())) { p ->
            if (p.doneFrames > 0 && abandonedRemoved < 0) {
                stagedAtCleanup = stagedFileCount()
                store.deleteIncomplete()
                abandonedRemoved = store.deleteAbandonedImports()
            }
        }

        assertTrue("$stagedAtCleanup", stagedAtCleanup > 0)
        assertEquals(0, abandonedRemoved)
        assertTrue("$result", result is RestoreResult.Success)
        val ids = (result as RestoreResult.Success).projectIds
        ids.forEachIndexed { index, id ->
            BackupSample.files(index).forEach { (file, bytes) -> assertArrayEquals(bytes, store.readFile(id, file)) }
        }
    }

    @Test
    fun restore_noSpace_failsWithNoSpaceAndLeavesNothing() = runTest {
        val store = store()
        store.write(project("Existing"))
        val source = backupFile("sample.frameloop", sampleBytes())
        val cases = listOf(
            "write failed: ENOSPC (No space left on device)" to RestoreFailure.NO_SPACE,
            "Permission denied" to RestoreFailure.WRITE_FAILED,
        )

        for ((message, expected) in cases) {
            val failing = FailingImportStore(store).apply {
                failWriteFileAt = 3
                this.message = message
            }
            assertRestoreFails(message, backup(failing), source, expected, store)
            assertEquals(message, 2, failing.writeFileSuccesses)
        }
    }

    @Test
    fun restore_commitFails_removesMovedProjects() = runTest {
        val store = store()
        store.write(project("Existing"))
        val failing = FailingImportStore(store).apply {
            failCommit = true
            message = "rename"
        }

        assertRestoreFails(
            "commit",
            backup(failing),
            backupFile("sample.frameloop", sampleBytes()),
            RestoreFailure.WRITE_FAILED,
            store,
        )
    }

    @Test
    fun restore_commitTargetExists_keepsExistingProject() = runTest {
        val store = store()
        val existing = project("Existing")
        writeWithCells(store, existing)
        val existingCell = store.readFile(existing.id, existing.cell(0, 0))
        val fresh = ProjectId.random()
        val ids = ArrayDeque(listOf(fresh, existing.id))

        val result = backup(store, newId = { ids.removeFirst() }).restore(backupFile("s.frameloop", sampleBytes())) {}

        assertEquals(RestoreResult.Failure(RestoreFailure.WRITE_FAILED), result)
        assertEquals(ProjectReadResult.Ok(existing), store.read(existing.id))
        assertArrayEquals(existingCell, store.readFile(existing.id, existing.cell(0, 0)))
        assertEquals(listOf(existing.id), store.ids())
        assertNoStaging()
        assertEquals(0, repository.count)
    }

    @Test
    fun restore_notABackup_failsWithNotABackup() = runTest {
        val store = store()
        store.write(project("Existing"))
        val backup = backup(store)
        val sample = sampleEntries()
        val (writerBytes, positions) = writerSample()
        val secondCell0 = sampleCells0[1]
        val midSecondCell = positions.named(secondCell0).let { (it.dataStart + it.end - 16) / 2 }
        var case = 0

        suspend fun check(label: String, bytes: ByteArray) = assertRestoreFails(
            label,
            backup,
            backupFile("case-${case++}.frameloop", bytes),
            RestoreFailure.NOT_A_BACKUP,
            store,
        )

        check("a: text", "hello".toByteArray())
        check("b: png", Base64.getDecoder().decode(PNG_1X1))
        check("c: empty zip", byteArrayOf(0x50, 0x4b, 0x05, 0x06) + ByteArray(18))
        check("d: manifest not first", zipOf(listOf(sample[1], sample[0]) + sample.drop(2)))
        check("e: manifest not json", zipOf(sample.replace("manifest.json", "not json {".toByteArray())))
        val names = listOf("Bouncing ball" to 3, "Sunset walk" to 2)
        check("f: other app", zipOf(sample.replace("manifest.json", manifestJson(names, app = "other").toByteArray())))
        check("g: version 0", zipOf(sample.replace("manifest.json", manifestJson(names, version = 0).toByteArray())))
        check("h: no name", zipOf(editProject(sample, 0) { it.remove("name") }))
        val wrongCount = listOf("Bouncing ball" to 4, "Sunset walk" to 2)
        check("i: frame count", zipOf(sample.replace("manifest.json", manifestJson(wrongCount).toByteArray())))
        val threeProjects = names + ("Extra" to 1)
        check("j: missing project", zipOf(sample.replace("manifest.json", manifestJson(threeProjects).toByteArray())))
        val thumbnail0 = sample.single { it.first == "projects/0/thumbnail.png" }
        val fileFirst = sample.filterNot { it === thumbnail0 }.toMutableList().apply { add(1, thumbnail0) }
        check("k: file before project.json", zipOf(fileFirst))

        val counting = FailingImportStore(store)
        assertRestoreFails(
            "l: cut in an entry",
            backup(counting),
            backupFile("cut.frameloop", writerBytes.copyOf(midSecondCell.toInt())),
            RestoreFailure.NOT_A_BACKUP,
            store,
        )
        assertTrue("l: ${counting.writeFileSuccesses}", counting.writeFileSuccesses >= 1)

        val bigCell = writeZipFile(file("big-cell.frameloop"), sample, mapOf(sampleCells0[0] to (32L shl 20) + 1))
        assertRestoreFails("m: cell over cap", backup, bigCell, RestoreFailure.NOT_A_BACKUP, store)

        val network = FailingImportStore(store)
        val failingStreams = FileBackupStreams().apply {
            wrapRead = { FailingInputStream(it, midSecondCell, "network") }
        }
        assertRestoreFails(
            "n: read fails",
            backup(network, streams = failingStreams),
            backupFile("network.frameloop", writerBytes),
            RestoreFailure.NOT_A_BACKUP,
            store,
        )
        assertTrue("n: ${network.writeFileSuccesses}", network.writeFileSuccesses >= 1)

        for (evil in listOf("projects/0/../../evil.png", "/abs.png")) {
            val renamed = sample.map { if (it === thumbnail0) evil to it.second else it }
            check("o: $evil", zipOf(renamed))
        }
        val duplicate = sample.toMutableList().apply { add(indexOf(thumbnail0) + 1, thumbnail0) }
        check("p: duplicate", storedZipOf(duplicate.recountEnd()))
        val movedCell = sample.single { it.first == sampleCells0[0] }
        val backwards = sample.filterNot { it === movedCell }.toMutableList().apply {
            add(indexOfFirst { it.first == "projects/1/project.json" } + 1, movedCell)
        }
        check("q: backwards", zipOf(backwards))
        val onlyLesson = project("Lesson", kind = ProjectKind.Lesson("walk-cycle", 0))
        check(
            "r: only a lesson",
            zipOf(
                "manifest.json" to manifestJson(listOf("Lesson" to 1)).toByteArray(),
                "projects/0/project.json" to ProjectFormat.encode(onlyLesson).toByteArray(),
                "end.json" to endJson(listOf(0)).toByteArray(),
            ),
        )
        val hugeReference = editProject(sample, 1) { project ->
            val reference = project.getValue("reference").jsonObject.toMutableMap()
            reference["frameCount"] = JsonPrimitive(2_000_000_000)
            project["reference"] = JsonObject(reference)
        }
        check("s: reference count", zipOf(hugeReference))
        val tooManyFrames = listOf("Bouncing ball" to BackupFormat.MAX_FRAMES_PER_PROJECT + 1, "Sunset walk" to 2)
        check("t: too many frames", zipOf(sample.replace("manifest.json", manifestJson(tooManyFrames).toByteArray())))
        val unknownBig = "projects/0/extra/big.bin"
        val withUnknown = sample.toMutableList().apply { add(indexOf(thumbnail0) + 1, unknownBig to ByteArray(0)) }
        val bigUnknown = writeZipFile(
            file("big-unknown.frameloop"),
            withUnknown.recountEnd(),
            mapOf(unknownBig to (16L shl 20) + 1),
        )
        assertRestoreFails("u: unknown entry over cap", backup, bigUnknown, RestoreFailure.NOT_A_BACKUP, store)

        val throwing = FileBackupStreams().apply { wrapRead = { ThrowingInputStream(UnsupportedOperationException()) } }
        assertRestoreFails(
            "v: read throws",
            backup(store, streams = throwing),
            backupFile("throwing.frameloop", sampleBytes()),
            RestoreFailure.NOT_A_BACKUP,
            store,
        )
        val badUri = FileBackupStreams().apply { readError = IllegalArgumentException("bad uri") }
        assertRestoreFails(
            "v: open throws",
            backup(store, streams = badUri),
            backupFile("bad.frameloop", sampleBytes()),
            RestoreFailure.NOT_A_BACKUP,
            store,
        )

        check("w: no end.json", zipOf(sample.without("end.json")))
        check("x: file count", zipOf(sample.replace("end.json", endJson(listOf(7, 7)).toByteArray())))
        check("y: missing cell", zipOf(sample.without(sampleCells0[0])))
        val end = sample.last()
        val endEarly = sample.dropLast(1).toMutableList().apply {
            add(indexOfFirst { it.first == "projects/1/project.json" }, end)
        }
        check("z: end.json early", zipOf(endEarly))
        check("aa: blank name", zipOf(editProject(sample, 0) { it["name"] = JsonPrimitive("   ") }))
    }

    @Test
    fun restore_truncatedAtEntryBoundary_notABackup() = runTest {
        val store = store()
        val (writerBytes, positions) = writerSample()
        val afterSecondCell = positions.named(sampleCells0[1]).end
        val beforeEnd = positions[positions.size - 2].end
        assertEquals("end.json", positions.last().name)

        assertRestoreFails(
            "after a whole entry",
            backup(store),
            backupFile("cut1.frameloop", writerBytes.copyOf(afterSecondCell.toInt())),
            RestoreFailure.NOT_A_BACKUP,
            store,
        )
        assertRestoreFails(
            "right before end.json",
            backup(store),
            backupFile("cut2.frameloop", writerBytes.copyOf(beforeEnd.toInt())),
            RestoreFailure.NOT_A_BACKUP,
            store,
        )
    }

    @Test
    fun restore_sampleV1_succeeds() = runTest {
        val store = store()
        val backup = backup(store)

        val result = backup.restore(backupFile("sample.frameloop", sampleBytes())) {}
        val rewritten = backup.restore(backupFile("rewritten.frameloop", zipOf(sampleEntries()))) {}

        for (restored in listOf(result, rewritten)) {
            assertTrue("$restored", restored is RestoreResult.Success)
            val ids = (restored as RestoreResult.Success).projectIds
            assertEquals(2, ids.size)
            BackupSample.projects().forEachIndexed { index, project ->
                val expected = project.copy(id = ids[index], updatedAtMillis = NOW - index)
                assertEquals(ProjectReadResult.Ok(expected), store.read(ids[index]))
                BackupSample.files(index).forEach { (file, bytes) ->
                    assertArrayEquals(bytes, store.readFile(ids[index], file))
                }
            }
        }
    }

    @Test
    fun restore_unknownSafeEntries_ignored() = runTest {
        val store = store()
        val sample = sampleEntries()
        val at = sample.indexOfFirst { it.first == "projects/0/project.json" } + 1
        val entries = sample.toMutableList().apply {
            add(at, "projects/0/cells/x/y.png" to byteArrayOf(1))
            add(at, "projects/0/notes.txt" to "notes".toByteArray())
        }

        val result = backup(store).restore(backupFile("extra.frameloop", zipOf(entries.recountEnd()))) {}

        assertTrue("$result", result is RestoreResult.Success)
        val ids = (result as RestoreResult.Success).projectIds
        val restoredFiles = root.walk().filter { it.isFile }.map { it.relativeTo(root).path }.toSet()
        val expected = ids.flatMapIndexed { index, id ->
            BackupSample.files(index).map { "${id.value}/${it.first.relativePath}" } + "${id.value}/project.json"
        }.toSet()
        assertEquals(expected, restoredFiles)
        assertFalse(tmp.root.walk().any { it.name == "notes.txt" })
    }

    @Test
    fun restore_moreThan65535Entries_succeeds() = runTest {
        val store = store()
        val sample = sampleEntries()
        val at = sample.indexOfFirst { it.first == "projects/1/project.json" }
        val extra = List(70_000) { "projects/0/extra/$it.bin" to byteArrayOf(it.toByte()) }
        val entries = (sample.take(at) + extra + sample.drop(at)).recountEnd()
        val source = writeZipFile(file("many.frameloop"), entries)

        val result = backup(store).restore(source) {}

        assertTrue("$result", result is RestoreResult.Success)
        assertEquals(2, (result as RestoreResult.Success).projectIds.size)
        assertFalse(root.walk().any { it.name == "extra" || it.name.endsWith(".bin") })
    }

    @Test
    fun restore_longName_truncatedTo40() = runTest {
        val store = store()
        val long = "a".repeat(45)
        val progress = mutableListOf<RestoreProgress>()

        val result = backup(store).restore(backupFile("long.frameloop", oneProjectSample(long))) { progress += it }

        val id = (result as RestoreResult.Success).projectIds.single()
        assertEquals("a".repeat(40), (store.read(id) as ProjectReadResult.Ok).project.name)
        assertEquals("a".repeat(40), progress.first().summary.projectName)
    }

    @Test
    fun restore_paddedName_trimmed() = runTest {
        val store = store()

        val result = backup(store).restore(backupFile("padded.frameloop", oneProjectSample("  Cat  "))) {}

        val id = (result as RestoreResult.Success).projectIds.single()
        assertEquals("Cat", (store.read(id) as ProjectReadResult.Ok).project.name)
    }

    /** Project 0 of the sample alone, named [name] in both `manifest.json` and `project.json`. */
    private fun oneProjectSample(name: String): ByteArray {
        val sample = sampleEntries().filter { it.first == "manifest.json" || it.first.startsWith("projects/0/") }
        val renamed = editProject(sample, 0) { it["name"] = JsonPrimitive(name) }
            .replace("manifest.json", manifestJson(listOf(name to 3)).toByteArray())
        return zipOf(renamed + ("end.json" to endJson(listOf(6)).toByteArray()))
    }

    @Test
    fun restore_newerBackupFormat_failsWithNewerVersion() = runTest {
        val fileStore = store()
        var imports = 0
        val store = object : ProjectStore by fileStore {
            override suspend fun beginImport(): ProjectImport {
                imports++
                return fileStore.beginImport()
            }
        }
        val names = listOf("Bouncing ball" to 3, "Sunset walk" to 2)
        val newer = sampleEntries().replace("manifest.json", manifestJson(names, version = 2).toByteArray())
        // Control: the same file with version 1 does begin an import.
        assertTrue(backup(store).restore(backupFile("v1.frameloop", sampleBytes())) {} is RestoreResult.Success)
        assertEquals(1, imports)

        assertRestoreFails(
            "newer backup",
            backup(store),
            backupFile("newer.frameloop", zipOf(newer)),
            RestoreFailure.NEWER_VERSION,
            store,
        )
        assertEquals(1, imports)
    }

    @Test
    fun restore_newerProjectFormat_failsWithNewerVersion() = runTest {
        val store = store()
        val newer = editProject(sampleEntries(), 0, editRoot = { it["formatVersion"] = JsonPrimitive(2) })

        assertRestoreFails(
            "newer project",
            backup(store),
            backupFile("newer.frameloop", zipOf(newer)),
            RestoreFailure.NEWER_VERSION,
            store,
        )
    }

    @Test
    fun restore_openFails_notABackup() = runTest {
        val store = store()
        for (error in listOf(FileNotFoundException("gone"), SecurityException("no grant"))) {
            val failing = FileBackupStreams().apply { readError = error }
            assertRestoreFails(
                error::class.java.simpleName,
                backup(store, streams = failing),
                backupFile("sample.frameloop", sampleBytes()),
                RestoreFailure.NOT_A_BACKUP,
                store,
            )
        }
    }

    @Test
    fun export_skipsLessonsAndUnreadable() = runTest {
        val store = store()
        val lesson = project("Lesson", kind = ProjectKind.Lesson("walk-cycle", 0))
        val regular = project("Regular")
        writeWithCells(store, lesson)
        writeWithCells(store, regular)
        val corrupt = ProjectId.random()
        File(root, "${corrupt.value}/project.json").apply {
            parentFile!!.mkdirs()
            writeText("{ broken")
        }

        val result = backup(store).export(uri("out.frameloop"), listOf(lesson.id, corrupt, regular.id))

        assertEquals(BackupExportResult.Success(1), result)
        val zip = file("out.frameloop").readBytes()
        assertEquals(listOf("Regular"), manifestNames(zip))
        val names = entriesOf(zip).map { it.first }
        assertEquals(1, names.count { it == "projects/0/project.json" })
        assertFalse(names.any { it.startsWith("projects/1/") })
    }

    @Test
    fun export_onlyLessons_nothingToExport() = runTest {
        val store = store()
        val lesson = project("Lesson", kind = ProjectKind.Lesson("walk-cycle", 0))
        store.write(lesson)

        val result = backup(store).export(uri("out.frameloop"), listOf(lesson.id))

        assertEquals(BackupExportResult.Failure(BackupExportFailure.NOTHING_TO_EXPORT), result)
        assertEquals(emptyList<String>(), streams.writeModes)
        assertFalse(file("out.frameloop").exists())
    }

    @Test
    fun export_writeFails_reportsKind() = runTest {
        val store = store()
        val cat = project("Cat", frames = 2)
        writeWithCells(store, cat)
        store.writeFile(cat.id, ProjectFile.Thumbnail, ByteArray(300) { it.toByte() })

        suspend fun exportWith(streams: BackupStreams) =
            backup(store, streams = streams).export(uri("out.frameloop"), listOf(cat.id))

        val noSpace = FileBackupStreams().apply { wrapWrite = { FailingOutputStream(it, 100, "ENOSPC") } }
        assertEquals(BackupExportResult.Failure(BackupExportFailure.NO_SPACE), exportWith(noSpace))

        val brokenPipe = FileBackupStreams().apply { wrapWrite = { FailingOutputStream(it, 100, "broken pipe") } }
        assertEquals(BackupExportResult.Failure(BackupExportFailure.WRITE_FAILED), exportWith(brokenPipe))

        val noGrant = FileBackupStreams().apply { writeError = { SecurityException("no grant") } }
        assertEquals(BackupExportResult.Failure(BackupExportFailure.WRITE_FAILED), exportWith(noGrant))
        assertEquals(listOf("wt"), noGrant.writeModes)

        val notFound = FileBackupStreams().apply { writeError = { FileNotFoundException("gone") } }
        assertEquals(BackupExportResult.Failure(BackupExportFailure.WRITE_FAILED), exportWith(notFound))
        assertEquals(listOf("wt", "w"), notFound.writeModes)

        val noTruncate = FailingWriteStreams()
        assertEquals(BackupExportResult.Success(1), exportWith(noTruncate))
        assertEquals(listOf("wt", "w"), noTruncate.writeModes)
        assertTrue(backup(store).restore(uri("out.frameloop")) {} is RestoreResult.Success)

        val illegalState = FileBackupStreams().apply {
            wrapWrite = { FailingOutputStream(it, 0, "x", error = { IllegalStateException("x") }) }
        }
        assertEquals(BackupExportResult.Failure(BackupExportFailure.WRITE_FAILED), exportWith(illegalState))
    }

    /** One frame, ten layers, every cell 16 KB of fixed random bytes (about 160 KB that does not compress). */
    private suspend fun writeIncompressible(store: ProjectStore): Project {
        val project = project("Big", frames = 1, layers = 10)
        store.write(project)
        project.layers.forEachIndexed { index, layer ->
            val bytes = ByteArray(16 * 1024).also { Random(index.toLong()).nextBytes(it) }
            store.writeFile(project.id, ProjectFile.Cell(project.frames[0].id, layer.id), bytes)
        }
        return project
    }

    @Test
    fun export_cancelledMidway_fileNotRestorable() = runTest {
        val store = store()
        val big = writeIncompressible(store)
        lateinit var job: Job
        val hooked = FileBackupStreams().apply { wrapWrite = { HookOutputStream(it, 20_000) { job.cancel() } } }

        job = launch { backup(store, streams = hooked).export(uri("out.frameloop"), listOf(big.id)) }
        advanceUntilIdle()

        assertTrue(job.isCancelled)
        val target = file("out.frameloop")
        assertTrue(target.length() > 0)
        assertZipUnreadable(target)
        assertEquals(
            RestoreResult.Failure(RestoreFailure.NOT_A_BACKUP),
            backup(store, streams = FileBackupStreams()).restore(uri("out.frameloop")) {},
        )
    }

    @Test
    fun export_storeReadFails_fileNotRestorable() = runTest {
        val store = store()
        val big = writeIncompressible(store)

        val result = backup(FailingReadStore(store, failReadFileAt = 8)).export(uri("out.frameloop"), listOf(big.id))

        assertEquals(BackupExportResult.Failure(BackupExportFailure.WRITE_FAILED), result)
        assertZipUnreadable(file("out.frameloop"))
        assertEquals(RestoreResult.Failure(RestoreFailure.NOT_A_BACKUP), backup(store).restore(uri("out.frameloop")) {})
    }

    private fun assertZipUnreadable(target: File) {
        try {
            ZipFile(target).close()
            fail("The file has a central directory")
        } catch (expected: ZipException) {
            // No central directory: finish() was not called.
        }
    }

    @Test
    fun export_projectOverLimit_tooLarge() = runTest {
        val store = store()
        val reference = ReferenceLayer(ReferenceType.VIDEO, "jpg", frameCount = BackupFormat.MAX_REFERENCE_IMAGES + 1)
        val huge = project("Huge").copy(reference = reference)
        store.write(huge)

        val result = backup(store).export(uri("out.frameloop"), listOf(huge.id))

        assertEquals(BackupExportResult.Failure(BackupExportFailure.TOO_LARGE), result)
        assertEquals(emptyList<String>(), streams.writeModes)
    }

    @Test
    fun export_fileOverCap_tooLarge() = runTest {
        val store = store()
        val cat = project("Cat")
        store.write(cat)
        val backup = backup(store)
        // Control: a thumbnail of exactly the cap is backed up.
        store.writeFile(cat.id, ProjectFile.Thumbnail, ByteArray(8 shl 20))
        assertEquals(BackupExportResult.Success(1), backup.export(uri("ok.frameloop"), listOf(cat.id)))

        store.writeFile(cat.id, ProjectFile.Thumbnail, ByteArray((8 shl 20) + 1))
        val result = backup.export(uri("out.frameloop"), listOf(cat.id))

        assertEquals(BackupExportResult.Failure(BackupExportFailure.TOO_LARGE), result)
        assertEquals(RestoreResult.Failure(RestoreFailure.NOT_A_BACKUP), backup.restore(uri("out.frameloop")) {})
    }

    @Test
    fun export_neverWritesToProjects() = runTest {
        val store = store()
        val cat = project("Cat", frames = 2)
        writeWithCells(store, cat)
        store.writeFile(cat.id, ProjectFile.Thumbnail, ByteArray(500))
        fun snapshot() = root.walk().map { Triple(it.relativeTo(root).path, it.length(), it.lastModified()) }.toList()
        val before = snapshot()

        assertEquals(BackupExportResult.Success(1), backup(store).export(uri("ok.frameloop"), listOf(cat.id)))
        val failing = FileBackupStreams().apply { wrapWrite = { FailingOutputStream(it, 10, "broken pipe") } }
        val failed = backup(store, streams = failing).export(uri("failed.frameloop"), listOf(cat.id))

        assertEquals(BackupExportResult.Failure(BackupExportFailure.WRITE_FAILED), failed)
        assertEquals(before, snapshot())
    }

    @Test
    fun export_closeFails_isFailure() = runTest {
        val store = store()
        val cat = project("Cat")
        writeWithCells(store, cat)
        val failing = FileBackupStreams().apply {
            wrapWrite = { FailingOutputStream(it, failAfterBytes = null, message = "close", failOnClose = true) }
        }

        val result = backup(store, streams = failing).export(uri("out.frameloop"), listOf(cat.id))

        assertEquals(BackupExportResult.Failure(BackupExportFailure.WRITE_FAILED), result)
    }

    @Test
    fun export_and_restore_runOnIoDispatcher() = runTest {
        val cat = project("Cat")
        writeWithCells(store(), cat)
        val queue = QueueDispatcher()
        val backup = DefaultProjectBackup(
            store = FileProjectStore(root, queue, logger),
            repository = repository,
            streams = streams,
            clock = { NOW },
            ioDispatcher = queue,
            logger = logger,
        )

        val export = async { backup.export(uri("out.frameloop"), listOf(cat.id)) }
        runCurrent()
        assertFalse(export.isCompleted)
        repeat(20) {
            if (!export.isCompleted) {
                queue.runAll()
                runCurrent()
            }
        }
        assertTrue("export not done after 20 rounds", export.isCompleted)
        assertEquals(BackupExportResult.Success(1), export.await())

        val restore = async { backup.restore(uri("out.frameloop")) {} }
        runCurrent()
        assertFalse(restore.isCompleted)
        repeat(20) {
            if (!restore.isCompleted) {
                queue.runAll()
                runCurrent()
            }
        }
        assertTrue("restore not done after 20 rounds", restore.isCompleted)
        assertTrue(restore.await() is RestoreResult.Success)
    }

    private companion object {
        const val NOW = 1_000_000L
        const val CHUNK = 64 * 1024
        const val PNG_1X1 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk" +
            "YAAAAAYAAjCB0C8AAAAASUVORK5CYII="
    }
}
