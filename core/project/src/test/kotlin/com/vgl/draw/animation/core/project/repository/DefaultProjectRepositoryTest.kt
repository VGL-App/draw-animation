package com.vgl.draw.animation.core.project.repository

import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import com.vgl.draw.animation.core.project.TestProjects
import com.vgl.draw.animation.core.project.model.AspectRatio
import com.vgl.draw.animation.core.project.model.Background
import com.vgl.draw.animation.core.project.model.BackgroundColor
import com.vgl.draw.animation.core.project.model.Frame
import com.vgl.draw.animation.core.project.model.FrameId
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.model.ProjectKind
import com.vgl.draw.animation.core.project.model.Resolution
import com.vgl.draw.animation.core.project.storage.CorruptReason
import com.vgl.draw.animation.core.project.storage.FileProjectStore
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectReadResult
import com.vgl.draw.animation.core.project.storage.ProjectStore
import java.io.File
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** The project repository on a real [FileProjectStore] in a temporary folder (plan T-002). */
@OptIn(ExperimentalCoroutinesApi::class)
class DefaultProjectRepositoryTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val logger = RecordingAppLogger()
    private val clock = FakeClock()
    private val preferences = MemoryPreferencesDataStore()

    private val root: File by lazy { File(tmp.root, "projects") }

    private fun TestScope.fileStore(dir: File = root): FileProjectStore =
        FileProjectStore(dir, StandardTestDispatcher(testScheduler), logger)

    private fun repo(
        store: ProjectStore,
        counter: CreatedProjectsCounter = DataStoreCreatedProjectsCounter(preferences, logger),
    ): DefaultProjectRepository = DefaultProjectRepository(store, counter, clock, logger)

    private fun project(
        name: String = "Cat",
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
    )

    private fun request(
        name: String = "My animation 1",
        background: NewBackground = NewBackground.Solid(BackgroundColor.WHITE),
        kind: ProjectKind = ProjectKind.Regular,
    ): NewProject = NewProject(
        name = name,
        aspectRatio = AspectRatio.PORTRAIT_9_16,
        resolution = Resolution.P1080,
        frameRate = FrameRate.FPS_12,
        background = background,
        firstLayerName = "Layer 1",
        kind = kind,
    )

    private fun <T> ProjectResult<T>.successValue(): T {
        assertTrue("$this", this is ProjectResult.Success)
        return (this as ProjectResult.Success).value
    }

    private fun List<ProjectListItem>.names(): List<String?> = map {
        when (it) {
            is ProjectListItem.Readable -> it.project.name
            is ProjectListItem.Unreadable -> it.name
        }
    }

    /** Names of the folders under [root] that are project ids. */
    private fun idDirs(): List<String> =
        root.listFiles().orEmpty().map { it.name }.filter { ProjectId.parseOrNull(it) != null }

    private fun writeRaw(id: ProjectId, text: String) {
        val dir = File(root, id.value)
        assertTrue(dir.mkdirs())
        File(dir, "project.json").writeText(text)
    }

    private fun repoWarnings() = logger.entries.filter { it.level == LogLevel.WARN && it.tag == "ProjectRepo" }

    @Test
    fun projects_sortedByUpdatedDescending() = runTest {
        val store = fileStore()
        val p10 = project("A", updatedAt = 10)
        val p30 = project("B", updatedAt = 30)
        val p20 = project("C", updatedAt = 20)
        listOf(p10, p30, p20).forEach { store.write(it) }

        val items = repo(store).projects.first()

        assertEquals(
            listOf(p30, p20, p10).map { ProjectListItem.Readable(it) },
            items,
        )
    }

    @Test
    fun create_newProjectFirstInList() = runTest {
        val store = fileStore()
        store.write(project("Old", updatedAt = 5000))
        clock.now = 6000
        val repo = repo(store)

        val created = repo.create(request()).successValue()

        val first = repo.projects.first().first()
        assertEquals(ProjectListItem.Readable(created), first)
        assertEquals(6000, created.createdAtMillis)
        assertEquals(6000, created.updatedAtMillis)
    }

    @Test
    fun duplicate_copyFirstInList() = runTest {
        val store = fileStore()
        val source = project("Cat", updatedAt = 100)
        store.write(source)
        store.write(project("Dog", updatedAt = 200))
        clock.now = 300
        val repo = repo(store)

        val copy = repo.duplicate(source.id).successValue()

        val items = repo.projects.first()
        assertEquals(3, items.size)
        assertEquals(ProjectListItem.Readable(copy), items.first())
    }

    @Test
    fun save_setsUpdatedAtAndMovesFirst() = runTest {
        val store = fileStore()
        val p100 = project("A", updatedAt = 100)
        store.write(p100)
        store.write(project("B", updatedAt = 200))
        clock.now = 500
        val repo = repo(store)

        val saved = repo.save(p100.copy(frames = p100.frames + Frame(FrameId.random()))).successValue()

        assertEquals(500, saved.updatedAtMillis)
        assertEquals(2, saved.frames.size)
        val first = repo.projects.first().first()
        assertEquals(ProjectListItem.Readable(saved), first)
        assertEquals(ProjectReadResult.Ok(saved), store.read(p100.id))
    }

    @Test
    fun projects_reemitsAfterEachChange() = runTest {
        val repo = repo(fileStore())
        val values = mutableListOf<List<ProjectListItem>>()
        // runCurrent(), not advanceUntilIdle(): the list loads in backgroundScope, whose tasks advanceUntilIdle()
        // does not wait for. Nothing here is delayed, so every task is due at the current virtual time.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { repo.projects.toList(values) }
        runCurrent()
        assertEquals(listOf(emptyList<ProjectListItem>()), values)

        val created = repo.create(request()).successValue()
        runCurrent()
        assertEquals(1, values.last().size)

        repo.rename(created.id, "New name").successValue()
        runCurrent()
        assertEquals(listOf("New name"), values.last().names())

        val copy = repo.duplicate(created.id).successValue()
        runCurrent()
        assertEquals(2, values.last().size)

        repo.delete(copy.id).successValue()
        runCurrent()
        assertEquals(1, values.last().size)

        repo.refresh()
        runCurrent()
        assertEquals(1, values.last().size)
        assertEquals(6, values.size)
    }

    @Test
    fun refresh_picksUpExternalWrite() = runTest {
        val store = fileStore()
        val repo = repo(store)
        val values = mutableListOf<List<ProjectListItem>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { repo.projects.toList(values) }
        runCurrent()

        val external = project("Restored")
        store.write(external)
        runCurrent()
        assertFalse(values.last().any { it.id == external.id })

        repo.refresh()
        runCurrent()
        assertEquals(listOf(ProjectListItem.Readable(external)), values.last())
    }

    @Test
    fun projects_hundredWithOneCorrupt_allListed() = runTest {
        val store = fileStore()
        val valid = (1..99).map { project("P$it", updatedAt = it.toLong()) }
        valid.forEach { store.write(it) }
        val broken = ProjectId.random()
        writeRaw(broken, """{"formatVersion":1,"project":{"name":"Broken"""")

        val items = repo(store).projects.first()

        assertEquals(100, items.size)
        assertEquals(valid.reversed().map { ProjectListItem.Readable(it) }, items.take(99))
        assertEquals(ProjectListItem.Unreadable(broken, name = null, newerVersion = false), items.last())
        assertTrue(File(root, "${broken.value}/project.json").isFile)
    }

    @Test
    fun projects_unreadableKeepsNameAndNewerFlag() = runTest {
        val store = fileStore()
        val kept = ProjectId.random()
        val future = ProjectId.random()
        writeRaw(kept, """{"formatVersion":1,"project":{"name":"Kept"}}""")
        writeRaw(future, """{"formatVersion":99,"project":{"name":"Future"}}""")
        assertEquals(ProjectReadResult.Corrupt("Kept", CorruptReason.INVALID_CONTENT), store.read(kept))
        assertEquals(ProjectReadResult.NewerVersion(99, "Future"), store.read(future))

        val items = repo(store).projects.first()

        val expected = listOf(
            ProjectListItem.Unreadable(kept, name = "Kept", newerVersion = false),
            ProjectListItem.Unreadable(future, name = "Future", newerVersion = true),
        ).sortedBy { it.id.value }
        assertEquals(expected, items)
    }

    @Test
    fun projects_excludesLessonProjects() = runTest {
        val store = fileStore()
        val regular = project("Mine")
        store.write(regular)
        store.write(project("Lesson", kind = ProjectKind.Lesson("walk", 0)))

        val items = repo(store).projects.first()

        assertEquals(listOf(ProjectListItem.Readable(regular)), items)
    }

    @Test
    fun create_solid_writesProjectAndReturnsIt() = runTest {
        val store = fileStore()
        val repo = repo(store)

        val p = repo.create(
            NewProject(
                name = "My animation 1",
                aspectRatio = AspectRatio.PORTRAIT_9_16,
                resolution = Resolution.P1080,
                frameRate = FrameRate.FPS_12,
                background = NewBackground.Solid(BackgroundColor.WHITE),
                firstLayerName = "Layer 1",
            ),
        ).successValue()

        assertEquals(ProjectReadResult.Ok(p), store.read(p.id))
        assertEquals("My animation 1", p.name)
        assertEquals(AspectRatio.PORTRAIT_9_16, p.aspectRatio)
        assertEquals(Resolution.P1080, p.resolution)
        assertEquals(FrameRate.FPS_12, p.frameRate)
        assertEquals(1, p.frames.size)
        assertEquals(listOf("Layer 1"), p.layers.map { it.name })
        assertEquals(Background.Solid(BackgroundColor.WHITE), p.background)
        assertEquals(ProjectKind.Regular, p.kind)
    }

    @Test
    fun create_photo_copiesBackgroundBytes() = runTest {
        val store = fileStore()

        val p = repo(store).create(request(background = NewBackground.Photo(byteArrayOf(1, 2, 3), "jpg")))
            .successValue()

        assertEquals(Background.Photo("jpg"), p.background)
        assertArrayEquals(byteArrayOf(1, 2, 3), store.readFile(p.id, ProjectFile.Background("jpg")))
    }

    @Test
    fun create_trimsAndTruncatesName() = runTest {
        val p = repo(fileStore()).create(request(name = "  " + "a".repeat(45) + "  ")).successValue()

        assertEquals("a".repeat(40), p.name)
    }

    @Test
    fun create_blankName_throwsWithoutWriting() = runTest {
        val store = fileStore()
        val repo = repo(store)

        val e = runCatching { repo.create(request(name = "  ")) }.exceptionOrNull()

        assertTrue("$e", e is IllegalArgumentException)
        assertEquals(emptyList<ProjectId>(), store.ids())
        assertTrue(root.listFiles().orEmpty().isEmpty())
        assertEquals(0, repo.createdCount.first())
    }

    @Test
    fun create_invalidPhotoExtension_throwsWithoutWriting() = runTest {
        val repo = repo(fileStore())

        val e = runCatching {
            repo.create(request(background = NewBackground.Photo(byteArrayOf(1), "../x")))
        }.exceptionOrNull()

        assertTrue("$e", e is IllegalArgumentException)
        assertEquals(emptyList<String>(), idDirs())
        assertEquals(0, repo.createdCount.first())
    }

    @Test
    fun create_noSpace_returnsNoSpaceAndLeavesNothing() = runTest {
        val store = fileStore()
        val failing = FailingProjectStore(store).apply {
            failWriteAt = 1
            writeMessage = "write failed: ENOSPC (No space left on device)"
        }
        val repo = repo(failing)
        failing.arm()

        val result = repo.create(request(background = NewBackground.Photo(byteArrayOf(1, 2, 3), "jpg")))

        assertEquals(ProjectResult.Failure(ProjectFailure.NO_SPACE), result)
        assertEquals(emptyList<ProjectId>(), store.ids())
        assertEquals(emptyList<String>(), idDirs())
        assertEquals(0, repo.createdCount.first())
        val warnings = repoWarnings()
        assertEquals(1, warnings.size)
        val warning = warnings.single()
        assertNull(warning.error)
        assertTrue(warning.message, "NO_SPACE" in warning.message)
        assertFalse(warning.message, root.path in warning.message)
        assertFalse(warning.message, UUID_PATTERN.containsMatchIn(warning.message))
        assertEquals(emptyList<ProjectListItem>(), repo.projects.first())
    }

    @Test
    fun create_otherIoError_returnsWriteFailed() = runTest {
        val failing = FailingProjectStore(fileStore()).apply { failWriteFileAt = 1 }
        val repo = repo(failing)
        failing.arm()

        val result = repo.create(request(background = NewBackground.Photo(byteArrayOf(1, 2, 3), "jpg")))

        assertEquals(ProjectResult.Failure(ProjectFailure.WRITE_FAILED), result)
        assertEquals(emptyList<String>(), idDirs())
        assertEquals(0, repo.createdCount.first())
    }

    @Test
    fun createdCount_startsAtZero_thenCountsCreates() = runTest {
        val repo = repo(fileStore())
        assertEquals(0, repo.createdCount.first())

        // Control: a constant 0 would fail here.
        repo.create(request(name = "My animation 1")).successValue()
        repo.create(request(name = "My animation 2")).successValue()

        assertEquals(2, repo.createdCount.first())
    }

    @Test
    fun createdCount_notDecreasedByDelete() = runTest {
        val repo = repo(fileStore())
        val a = repo.create(request(name = "A")).successValue()
        val b = repo.create(request(name = "B")).successValue()

        repo.delete(a.id).successValue()
        repo.delete(b.id).successValue()

        assertEquals(2, repo.createdCount.first())
        assertEquals(emptyList<ProjectListItem>(), repo.projects.first())
    }

    @Test
    fun createdCount_ignoresDuplicateLessonAndExternalWrites() = runTest {
        val store = fileStore()
        val repo = repo(store)
        val created = repo.create(request()).successValue()
        assertEquals(1, repo.createdCount.first())

        repo.duplicate(created.id).successValue()
        repo.create(request(name = "Walk", kind = ProjectKind.Lesson("walk", 0))).successValue()
        store.write(project("Restored"))
        repo.refresh()

        assertEquals(1, repo.createdCount.first())
    }

    @Test
    fun create_concurrent_bothCountedAndListed() = runTest {
        val repo = repo(fileStore())

        val results = awaitAll(
            async { repo.create(request(name = "A")) },
            async { repo.create(request(name = "B")) },
        ).map { it.successValue() }

        assertNotEquals(results[0].id, results[1].id)
        assertEquals(2, repo.projects.first().size)
        assertEquals(2, repo.createdCount.first())
    }

    @Test
    fun create_counterWriteFails_stillSucceeds() = runTest {
        val repo = repo(fileStore(), DataStoreCreatedProjectsCounter(UnwritablePreferencesDataStore(), logger))

        val p = repo.create(request()).successValue()

        assertEquals(listOf(ProjectListItem.Readable(p)), repo.projects.first())
        assertEquals(1, repoWarnings().size)
    }

    @Test
    fun rename_persistsTrimmedNameKeepsUpdatedAt() = runTest {
        val store = fileStore()
        val p = project("Old", updatedAt = 100)
        store.write(p)
        clock.now = 900

        val renamed = repo(store).rename(p.id, "  New name ").successValue()

        assertEquals("New name", renamed.name)
        assertEquals(100, renamed.updatedAtMillis)
        val reopened = DefaultProjectRepository(fileStore(root), MemoryCounter(), clock, logger)
        assertEquals(listOf("New name"), reopened.projects.first().names())
    }

    @Test
    fun rename_duplicateNameAllowed() = runTest {
        val store = fileStore()
        store.write(project("A", updatedAt = 1))
        val b = project("B", updatedAt = 2)
        store.write(b)
        val repo = repo(store)

        repo.rename(b.id, "A").successValue()

        assertEquals(listOf("A", "A"), repo.projects.first().names())
    }

    @Test
    fun rename_blank_throwsAndKeepsName() = runTest {
        val store = fileStore()
        val p = project("Cat")
        store.write(p)

        val e = runCatching { repo(store).rename(p.id, " ") }.exceptionOrNull()

        assertTrue("$e", e is IllegalArgumentException)
        assertEquals(ProjectReadResult.Ok(p), store.read(p.id))
    }

    @Test
    fun rename_missing_returnsNotFound() = runTest {
        val result = repo(fileStore()).rename(ProjectId.random(), "New")

        assertEquals(ProjectResult.Failure(ProjectFailure.NOT_FOUND), result)
    }

    @Test
    fun rename_corrupt_returnsUnreadable() = runTest {
        val store = fileStore()
        val broken = ProjectId.random()
        writeRaw(broken, """{"formatVersion":1,"project":{"name":"Broken"""")
        val file = File(root, "${broken.value}/project.json")
        val before = file.readBytes()

        val result = repo(store).rename(broken, "New")

        assertEquals(ProjectResult.Failure(ProjectFailure.UNREADABLE), result)
        assertArrayEquals(before, file.readBytes())
    }

    @Test
    fun rename_concurrentWithSave_keepsBoth() = runTest {
        val store = fileStore()
        val p = project("Old")
        store.write(p)
        val failing = FailingProjectStore(store).apply { pauseWriteAt = 1 }
        val repo = repo(failing)
        failing.arm()

        val saving = async { repo.save(p.copy(frames = p.frames + Frame(FrameId.random()))) }
        val renaming = async { repo.rename(p.id, "New") }
        runCurrent()
        assertTrue(failing.writeReached.isCompleted)
        failing.releaseWrite.complete(Unit)
        awaitAll(saving, renaming).forEach { it.successValue() }

        val stored = (store.read(p.id) as ProjectReadResult.Ok).project
        assertEquals(2, stored.frames.size)
        assertEquals("New", stored.name)
    }

    @Test
    fun duplicate_copiesEveryReferencedFile() = runTest {
        val store = fileStore()
        val full = TestProjects.full()
        val source = full.copy(
            id = ProjectId.random(),
            kind = ProjectKind.Regular,
            reference = full.reference?.copy(frameCount = 3),
        )
        store.write(source)
        val cells = source.frames.flatMap { frame -> source.layers.map { ProjectFile.Cell(frame.id, it.id) } }
        val referenced: List<ProjectFile> = listOf(ProjectFile.Thumbnail, ProjectFile.Background("jpg")) +
            cells +
            source.sounds.map { ProjectFile.Sound(it.id, it.fileExtension) } +
            (0 until 3).map { ProjectFile.ReferenceImage(it, "jpg") }
        val leftEmpty = cells.filterIndexed { index, _ -> index !in setOf(0, 3, 5, 8, 11) }.toSet()
        referenced.filter { it !in leftEmpty }.forEachIndexed { index, file ->
            store.writeFile(source.id, file, byteArrayOf(index.toByte(), 42, (index * 3).toByte()))
        }
        val before = referenced.associateWith { store.readFile(source.id, it)?.toList() }
        assertEquals(7, before.values.count { it == null })
        clock.now = 5000

        val copy = repo(store).duplicate(source.id).successValue()

        assertNotEquals(source.id, copy.id)
        assertEquals(5000, copy.createdAtMillis)
        assertEquals(5000, copy.updatedAtMillis)
        assertEquals(
            source,
            copy.copy(
                id = source.id,
                name = source.name,
                createdAtMillis = source.createdAtMillis,
                updatedAtMillis = source.updatedAtMillis,
            ),
        )
        assertEquals(ProjectReadResult.Ok(copy), store.read(copy.id))
        for (file in referenced) {
            assertEquals(file.relativePath, before.getValue(file), store.readFile(copy.id, file)?.toList())
            assertEquals(file.relativePath, before.getValue(file), store.readFile(source.id, file)?.toList())
        }
        assertEquals(ProjectReadResult.Ok(source), store.read(source.id))
    }

    @Test
    fun duplicate_independentFiles() = runTest {
        val store = fileStore()
        val source = project("Cat")
        store.write(source)
        val cell = ProjectFile.Cell(source.frames.single().id, source.layers.single().id)
        store.writeFile(source.id, cell, byteArrayOf(1))
        val repo = repo(store)
        val copy = repo.duplicate(source.id).successValue()

        store.writeFile(copy.id, cell, byteArrayOf(2))
        assertArrayEquals(byteArrayOf(1), store.readFile(source.id, cell))

        store.writeFile(source.id, cell, byteArrayOf(3))
        assertArrayEquals(byteArrayOf(2), store.readFile(copy.id, cell))

        repo.rename(copy.id, "Other").successValue()
        assertEquals("Cat", (store.read(source.id) as ProjectReadResult.Ok).project.name)
    }

    @Test
    fun duplicate_twice_namesCopyThenCopy2() = runTest {
        val store = fileStore()
        val source = project("Cat")
        store.write(source)
        val repo = repo(store)

        assertEquals("Cat copy", repo.duplicate(source.id).successValue().name)
        assertEquals("Cat copy 2", repo.duplicate(source.id).successValue().name)
    }

    @Test
    fun duplicate_concurrent_distinctNames() = runTest {
        val store = fileStore()
        val source = project("Cat")
        store.write(source)
        val repo = repo(store)

        val names = awaitAll(
            async { repo.duplicate(source.id) },
            async { repo.duplicate(source.id) },
        ).map { it.successValue().name }

        assertEquals(setOf("Cat copy", "Cat copy 2"), names.toSet())
    }

    @Test
    fun duplicate_noSpaceMidway_leavesNoCopy() = runTest {
        val store = fileStore()
        val source = fiveCellSource(store)
        val failing = FailingProjectStore(store).apply {
            failWriteFileAt = 3
            writeFileMessage = "No space left on device"
        }
        val repo = repo(failing)
        failing.arm()

        val result = repo.duplicate(source.id)

        assertEquals(ProjectResult.Failure(ProjectFailure.NO_SPACE), result)
        assertEquals(listOf(source.id), store.ids())
        assertEquals(listOf(source.id.value), root.listFiles().orEmpty().map { it.name })
        assertEquals(1, repo.projects.first().size)
        assertEquals(0, repo.createdCount.first())
    }

    @Test
    fun duplicate_metadataWriteFails_leavesNoCopy() = runTest {
        val store = fileStore()
        val source = fiveCellSource(store)
        val failing = FailingProjectStore(store).apply { failWriteAt = 1 }
        val repo = repo(failing)
        failing.arm()

        val result = repo.duplicate(source.id)

        assertEquals(ProjectResult.Failure(ProjectFailure.WRITE_FAILED), result)
        assertEquals(listOf(source.id.value), root.listFiles().orEmpty().map { it.name })
    }

    @Test
    fun duplicate_missing_returnsNotFound() = runTest {
        val result = repo(fileStore()).duplicate(ProjectId.random())

        assertEquals(ProjectResult.Failure(ProjectFailure.NOT_FOUND), result)
        assertEquals(emptyList<String>(), idDirs())
    }

    @Test
    fun duplicate_corrupt_returnsUnreadable() = runTest {
        val store = fileStore()
        val broken = ProjectId.random()
        writeRaw(broken, """{"formatVersion":1,"project":{"name":"Broken"""")

        val result = repo(store).duplicate(broken)

        assertEquals(ProjectResult.Failure(ProjectFailure.UNREADABLE), result)
        assertEquals(listOf(broken.value), root.listFiles().orEmpty().map { it.name })
    }

    @Test
    fun delete_removesFromListAndDisk() = runTest {
        val store = fileStore()
        val a = project("A", updatedAt = 1)
        val b = project("B", updatedAt = 2)
        store.write(a)
        store.write(b)
        val repo = repo(store)

        assertEquals(ProjectResult.Success(Unit), repo.delete(a.id))

        assertEquals(listOf(ProjectListItem.Readable(b)), repo.projects.first())
        assertFalse(File(root, a.id.value).exists())
        val reopened = DefaultProjectRepository(fileStore(root), MemoryCounter(), clock, logger)
        assertEquals(listOf(ProjectListItem.Readable(b)), reopened.projects.first())
    }

    @Test
    fun delete_last_listEmpty() = runTest {
        val store = fileStore()
        val p = project()
        store.write(p)
        val repo = repo(store)

        repo.delete(p.id).successValue()

        assertEquals(emptyList<ProjectListItem>(), repo.projects.first())
    }

    @Test
    fun delete_ioError_returnsWriteFailed() = runTest {
        val store = fileStore()
        val p = project()
        store.write(p)
        val failing = FailingProjectStore(store).apply { failDelete = true }
        val repo = repo(failing)
        failing.arm()

        val result = repo.delete(p.id)

        assertEquals(ProjectResult.Failure(ProjectFailure.WRITE_FAILED), result)
        assertEquals(listOf(ProjectListItem.Readable(p)), repo.projects.first())
    }

    @Test
    fun projects_firstLoad_cleansIncompleteDirs() = runTest {
        val store = fileStore()
        val valid = project()
        store.write(valid)
        val incomplete = File(root, "${ProjectId.random().value}/cells/x.png")
        assertTrue(incomplete.parentFile!!.mkdirs())
        incomplete.writeBytes(byteArrayOf(1))
        val incompleteDir = incomplete.parentFile!!.parentFile!!

        val repo = repo(store)
        // Control: building the repository does not clean.
        assertTrue(incompleteDir.isDirectory)

        assertEquals(listOf(ProjectListItem.Readable(valid)), repo.projects.first())
        assertFalse(incompleteDir.exists())
        assertTrue(File(root, "${valid.id.value}/project.json").isFile)
    }

    @Test
    fun projects_cleanUpRunsOnce() = runTest {
        val failing = FailingProjectStore(fileStore())
        val repo = repo(failing)

        repo.projects.first()
        repo.projects.first()
        repo.refresh()
        repo.projects.first()

        assertEquals(1, failing.deleteIncompleteCalls)
    }

    @Test
    fun projects_cleanUpFails_listStillLoads() = runTest {
        val store = fileStore()
        val valid = project()
        store.write(valid)
        val failing = FailingProjectStore(store).apply { failDeleteIncomplete = true }
        val repo = repo(failing)
        failing.arm()

        assertEquals(listOf(ProjectListItem.Readable(valid)), repo.projects.first())
        assertEquals(1, repoWarnings().size)
    }

    @Test
    fun projects_firstCollect_deletesAbandonedImports() = runTest {
        val store = fileStore()
        val valid = project()
        store.write(valid)
        val leftover = File(root, ".restoring-old/${ProjectId.random().value}/cells/x.png")
        assertTrue(leftover.parentFile!!.mkdirs())
        leftover.writeBytes(byteArrayOf(1))
        val staging = File(root, ".restoring-old")

        val repo = repo(store)
        // Control: building the repository does not clean.
        assertTrue(staging.isDirectory)

        assertEquals(listOf(ProjectListItem.Readable(valid)), repo.projects.first())
        assertFalse(staging.exists())
    }

    @Test
    fun projects_abandonedImportCleanupFails_stillLoads() = runTest {
        val fileStore = fileStore()
        val valid = project()
        fileStore.write(valid)
        val store = object : ProjectStore by fileStore {
            override suspend fun deleteAbandonedImports(): Int = throw IOException("x")
        }
        val incomplete = File(root, "${ProjectId.random().value}/cells/x.png")
        assertTrue(incomplete.parentFile!!.mkdirs())
        incomplete.writeBytes(byteArrayOf(1))

        assertEquals(listOf(ProjectListItem.Readable(valid)), repo(store).projects.first())

        // The other clean-up still ran.
        assertFalse(incomplete.parentFile!!.parentFile!!.exists())
        val warnings = repoWarnings()
        assertEquals(1, warnings.size)
        assertNull(warnings.single().error)
    }

    /** A regular project with 5 of its 12 cells drawn, written through the inner store (not counted). */
    private suspend fun fiveCellSource(store: ProjectStore): Project {
        val full = TestProjects.full()
        val source = full.copy(
            id = ProjectId.random(),
            kind = ProjectKind.Regular,
            name = "Cat",
            background = Background.Solid(BackgroundColor.WHITE),
            sounds = emptyList(),
            reference = null,
        )
        store.write(source)
        source.frames.flatMap { frame -> source.layers.map { ProjectFile.Cell(frame.id, it.id) } }
            .take(5)
            .forEachIndexed { index, cell -> store.writeFile(source.id, cell, byteArrayOf(index.toByte())) }
        return source
    }

    private companion object {
        val UUID_PATTERN = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
    }
}
