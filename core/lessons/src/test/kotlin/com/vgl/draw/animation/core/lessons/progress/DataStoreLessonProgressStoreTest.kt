package com.vgl.draw.animation.core.lessons.progress

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vgl.draw.animation.core.lessons.FlakyDataStore
import com.vgl.draw.animation.core.lessons.UnwritableDataStore
import com.vgl.draw.animation.core.lessons.editedSample
import com.vgl.draw.animation.core.lessons.resourceText
import com.vgl.draw.animation.core.lessons.testDataStore
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Lesson progress on the shared preferences file (plan T-048, section 3.1). */
class DataStoreLessonProgressStoreTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val logger = RecordingAppLogger()

    // Raw key written here on purpose, not taken from the implementation: renaming it must turn tests red.
    private val progressKey = stringPreferencesKey("lessons_progress")

    private fun TestScope.realDataStore(): DataStore<Preferences> =
        testDataStore(tempFolder.newFolder(), backgroundScope)

    private fun attempt(
        step: Int = 0,
        stepCount: Int = 3,
        projectId: String = PROJECT_1,
        startedAtMillis: Long = 1_000,
        version: String = "v1",
    ) = LessonAttempt(version, step, stepCount, projectId, startedAtMillis)

    private fun warnings(recorder: RecordingAppLogger = logger) = recorder.entries.filter { it.level == LogLevel.WARN }

    @Test
    fun sampleFormat_readAndWrite() = runTest {
        val stored = realDataStore()
        stored.edit { it[progressKey] = resourceText("progress-v1.json") }
        val sample = LessonProgress(
            completed = mapOf("bouncing-ball" to CompletedLesson(PROJECT_2)),
            inProgress = mapOf("squash-and-stretch" to LessonAttempt("v1", 2, 6, PROJECT_1, 1763769600000)),
        )
        assertEquals(sample, DataStoreLessonProgressStore(stored, logger).progress.first())

        val empty = realDataStore()
        val store = DataStoreLessonProgressStore(empty, logger)
        store.start("bouncing-ball", attempt(projectId = PROJECT_2))
        store.complete("bouncing-ball", 5_000)
        store.start("squash-and-stretch", LessonAttempt("v1", 0, 6, PROJECT_1, 1763769600000))
        store.setStep("squash-and-stretch", 2)

        val written = empty.data.first()[progressKey]
        assertEquals(Json.parseToJsonElement(resourceText("progress-v1.json")), Json.parseToJsonElement(written!!))
        assertEquals(sample, store.progress.first())
    }

    @Test
    fun empty_returnsEmptyProgress() = runTest {
        val store = DataStoreLessonProgressStore(realDataStore(), logger)
        assertEquals(LessonProgress(), store.progress.first())

        // Control: a constant implementation would fail here.
        store.start("bouncing-ball", attempt())
        assertNotEquals(LessonProgress(), store.progress.first())
    }

    @Test
    fun start_storesAttempt() = runTest {
        val store = DataStoreLessonProgressStore(realDataStore(), logger)
        store.start("bouncing-ball", LessonAttempt("v1", 0, 3, PROJECT_1, 1000))

        val progress = store.progress.first()
        assertEquals(LessonAttempt("v1", 0, 3, PROJECT_1, 1000), progress.inProgress["bouncing-ball"])
        assertEquals(emptyMap<String, CompletedLesson>(), progress.completed)
    }

    @Test
    fun setStep_updatesStep() = runTest {
        val dataStore = realDataStore()
        val store = DataStoreLessonProgressStore(dataStore, logger)
        store.start("bouncing-ball", attempt(stepCount = 3))

        assertTrue(store.setStep("bouncing-ball", 2))
        assertEquals(2, store.progress.first().inProgress.getValue("bouncing-ball").step)

        val before = dataStore.data.first()[progressKey]
        assertFalse(store.setStep("walk-in-place", 1))
        assertEquals(before, dataStore.data.first()[progressKey])

        val emptyStore = realDataStore()
        assertFalse(DataStoreLessonProgressStore(emptyStore, logger).setStep("bouncing-ball", 1))
        assertNull(emptyStore.data.first()[progressKey])

        for (step in listOf(3, -1)) {
            val error = runCatching { store.setStep("bouncing-ball", step) }.exceptionOrNull()
            assertTrue("$step: $error", error is IllegalArgumentException)
        }
        assertEquals(2, store.progress.first().inProgress.getValue("bouncing-ball").step)
    }

    @Test
    fun complete_onceThenNull() = runTest {
        val dataStore = realDataStore()
        val store = DataStoreLessonProgressStore(dataStore, logger)
        store.start("bouncing-ball", attempt(startedAtMillis = 1_000))

        assertEquals(60_000L, store.complete("bouncing-ball", 61_000))
        val progress = store.progress.first()
        assertEquals(CompletedLesson(PROJECT_1), progress.completed["bouncing-ball"])
        assertFalse(progress.inProgress.containsKey("bouncing-ball"))

        assertNull(store.complete("bouncing-ball", 70_000))
        assertTrue(store.progress.first().completed.containsKey("bouncing-ball"))

        val before = dataStore.data.first()[progressKey]
        assertNull(store.complete("walk-in-place", 70_000))
        assertEquals(before, dataStore.data.first()[progressKey])

        store.start("squash-and-stretch", attempt(startedAtMillis = 50_000, projectId = PROJECT_3))
        assertEquals(0L, store.complete("squash-and-stretch", 40_000))
    }

    @Test
    fun start_completedLesson_keepsCompleted() = runTest {
        val store = DataStoreLessonProgressStore(realDataStore(), logger)
        store.start("bouncing-ball", attempt(projectId = PROJECT_1))
        store.complete("bouncing-ball", 2_000)

        store.start("bouncing-ball", attempt(step = 0, projectId = PROJECT_2))
        val redoing = store.progress.first()
        assertEquals(CompletedLesson(PROJECT_1), redoing.completed["bouncing-ball"])
        assertEquals(attempt(step = 0, projectId = PROJECT_2), redoing.inProgress["bouncing-ball"])
        assertNotEquals(PROJECT_1, redoing.inProgress.getValue("bouncing-ball").projectId)

        store.complete("bouncing-ball", 3_000)
        assertEquals(CompletedLesson(PROJECT_2), store.progress.first().completed["bouncing-ball"])
    }

    @Test
    fun start_inProgress_replacesAttempt() = runTest {
        val store = DataStoreLessonProgressStore(realDataStore(), logger)
        store.start("bouncing-ball", attempt(step = 2, version = "v1"))
        store.start("bouncing-ball", attempt(step = 0, version = "v2", projectId = PROJECT_2))

        assertEquals(
            mapOf("bouncing-ball" to attempt(step = 0, version = "v2", projectId = PROJECT_2)),
            store.progress.first().inProgress,
        )
    }

    @Test
    fun start_invalid_throwsAndWritesNothing() = runTest {
        val dataStore = realDataStore()
        val store = DataStoreLessonProgressStore(dataStore, logger)
        val cases = listOf(
            "Bad Id" to attempt(),
            "bouncing-ball" to attempt(version = "x"),
            "bouncing-ball" to attempt(projectId = "1-1-1-1-1"),
            "bouncing-ball" to attempt(projectId = PROJECT_1.uppercase()),
            "bouncing-ball" to attempt(step = 0, stepCount = 0),
            "bouncing-ball" to attempt(step = 3, stepCount = 3),
            "bouncing-ball" to attempt(startedAtMillis = -1),
        )
        for ((id, attempt) in cases) {
            val error = runCatching { store.start(id, attempt) }.exceptionOrNull()
            assertTrue("$id $attempt: $error", error is IllegalArgumentException)
        }
        assertNull(dataStore.data.first()[progressKey])
    }

    @Test
    fun corruptValue_warnsOnceAcrossOtherKeyChanges() = runTest {
        val dataStore = realDataStore()
        dataStore.edit { it[progressKey] = "{" }
        val store = DataStoreLessonProgressStore(dataStore, logger)
        val seen = mutableListOf<LessonProgress>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { store.progress.collect { seen += it } }
        runCurrent()

        val sampleCount = intPreferencesKey("sample_count")
        for (count in 1..3) {
            dataStore.edit { it[sampleCount] = count }
            runCurrent()
        }

        assertEquals(listOf(LessonProgress()), seen)
        assertEquals(1, warnings().size)
    }

    @Test
    fun corruptValue_readsEmptyAndLogs() = runTest {
        val dataStore = realDataStore()
        val newer = editedSample("progress-v1.json") { it["formatVersion"] = JsonPrimitive(2) }
        val stepTooFar = editedSample("progress-v1.json") { root ->
            val inProgress = root.getValue("inProgress").jsonObject.toMutableMap()
            val attempt = inProgress.getValue("squash-and-stretch").jsonObject.toMutableMap()
            attempt["step"] = JsonPrimitive(6)
            inProgress["squash-and-stretch"] = JsonObject(attempt)
            root["inProgress"] = JsonObject(inProgress)
        }
        for (value in listOf("{", newer, stepTooFar)) {
            dataStore.edit { it[progressKey] = value }
            val recorder = RecordingAppLogger()
            val store = DataStoreLessonProgressStore(dataStore, recorder)

            assertEquals(LessonProgress(), store.progress.first())
            val warning = warnings(recorder).single()
            assertEquals("Lessons", warning.tag)
            assertFalse(warning.message, warning.message.contains("6f1c2a3e"))
        }

        val store = DataStoreLessonProgressStore(dataStore, logger)
        store.start("bouncing-ball", attempt())
        assertEquals(LessonProgress(inProgress = mapOf("bouncing-ball" to attempt())), store.progress.first())
    }

    @Test
    fun readIOException_retries() = runTest {
        val sample = LessonProgress(
            completed = mapOf("bouncing-ball" to CompletedLesson(PROJECT_2)),
            inProgress = mapOf("squash-and-stretch" to LessonAttempt("v1", 2, 6, PROJECT_1, 1763769600000)),
        )
        val flaky = FlakyDataStore(
            failuresBeforeSuccess = 2,
            stored = preferencesOf(progressKey to resourceText("progress-v1.json")),
        )
        val store = DataStoreLessonProgressStore(flaky, logger)
        val seen = mutableListOf<LessonProgress>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { store.progress.collect { seen += it } }

        assertEquals(LessonProgress(), seen.first())
        advanceTimeBy(3_000)
        runCurrent()
        assertEquals(sample, seen.last())
        assertEquals(2, warnings().size)
    }

    @Test
    fun writeIOException_throws() = runTest {
        val store = DataStoreLessonProgressStore(UnwritableDataStore(), logger)
        val errors = listOf(
            runCatching { store.start("bouncing-ball", attempt()) }.exceptionOrNull(),
            runCatching { store.setStep("bouncing-ball", 1) }.exceptionOrNull(),
            runCatching { store.complete("bouncing-ball", 2_000) }.exceptionOrNull(),
        )
        for (error in errors) assertTrue("$error", error is IOException)
    }

    @Test
    fun concurrentWrites_bothKept() = runTest {
        val store = DataStoreLessonProgressStore(realDataStore(), logger)
        store.start("bouncing-ball", attempt(stepCount = 3))
        store.start("squash-and-stretch", attempt(stepCount = 6, projectId = PROJECT_2))

        listOf(
            async { store.setStep("bouncing-ball", 1) },
            async { store.complete("squash-and-stretch", 5_000) },
        ).awaitAll()

        val progress = store.progress.first()
        assertEquals(1, progress.inProgress.getValue("bouncing-ball").step)
        assertEquals(CompletedLesson(PROJECT_2), progress.completed["squash-and-stretch"])
        assertFalse(progress.inProgress.containsKey("squash-and-stretch"))
    }

    @Test
    fun reopen_keepsProgress() = runTest {
        val folder = tempFolder.newFolder()
        val firstJob = Job()
        val first = testDataStore(folder, CoroutineScope(StandardTestDispatcher(testScheduler) + firstJob))
        val writer = DataStoreLessonProgressStore(first, logger)
        writer.start("bouncing-ball", attempt(stepCount = 3))
        writer.setStep("bouncing-ball", 1)
        writer.start("squash-and-stretch", attempt(projectId = PROJECT_2))
        writer.complete("squash-and-stretch", 9_000)
        val written = writer.progress.first()
        firstJob.cancelAndJoin()

        val secondJob = Job()
        try {
            val second = testDataStore(folder, CoroutineScope(StandardTestDispatcher(testScheduler) + secondJob))
            assertEquals(written, DataStoreLessonProgressStore(second, RecordingAppLogger()).progress.first())
        } finally {
            secondJob.cancel()
        }
        assertTrue(File(folder, "test.preferences_pb").isFile)
    }

    @Test
    fun keyName_isLessonsProgress() = runTest {
        val dataStore = realDataStore()
        DataStoreLessonProgressStore(dataStore, logger).start("bouncing-ball", attempt())
        val raw = dataStore.data.first()[stringPreferencesKey("lessons_progress")]
        assertTrue("$raw", raw != null && raw.contains("bouncing-ball"))
    }

    private companion object {
        const val PROJECT_1 = "6f1c2a3e-0000-4000-8000-000000000001"
        const val PROJECT_2 = "6f1c2a3e-0000-4000-8000-000000000002"
        const val PROJECT_3 = "6f1c2a3e-0000-4000-8000-000000000003"
    }
}
