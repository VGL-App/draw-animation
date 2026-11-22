package com.vgl.draw.animation.core.lessons.content

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vgl.draw.animation.core.assets.AssetFailureReason
import com.vgl.draw.animation.core.assets.AssetResult
import com.vgl.draw.animation.core.lessons.FakeRemoteAssets
import com.vgl.draw.animation.core.lessons.FakeRemoteSettings
import com.vgl.draw.animation.core.lessons.LessonsSettings
import com.vgl.draw.animation.core.lessons.UnreadableDataStore
import com.vgl.draw.animation.core.lessons.UnwritableDataStore
import com.vgl.draw.animation.core.lessons.editItem
import com.vgl.draw.animation.core.lessons.editedSample
import com.vgl.draw.animation.core.lessons.progress.LessonAttempt
import com.vgl.draw.animation.core.lessons.progress.LessonProgress
import com.vgl.draw.animation.core.lessons.progress.packVersionFor
import com.vgl.draw.animation.core.lessons.resourceText
import com.vgl.draw.animation.core.lessons.samplePackEntries
import com.vgl.draw.animation.core.lessons.testDataStore
import com.vgl.draw.animation.core.lessons.zipOf
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import java.io.File
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Lesson index and packs loaded through RemoteAssets (plan T-048; spec lessons KB-2, KB-3, KB-4). */
class DefaultLessonContentTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val logger = RecordingAppLogger()
    private val settings = FakeRemoteSettings()

    // Raw key written here on purpose, not taken from the implementation: renaming it must turn tests red.
    private val savedVersionKey = stringPreferencesKey("lessons_index_version")

    private val sampleIndex: LessonIndex by lazy {
        (LessonIndexFormat.decode(resourceText("index-v1.json"), "v1") as IndexDecodeResult.Ok).index
    }

    /** The sample index with the title of its first lesson changed: the index of content version v2. */
    private val v2IndexText: String by lazy {
        editedSample("index-v1.json") { root -> root.editItem("lessons", 0) { it["title"] = JsonPrimitive("Walk 2") } }
    }
    private val v2Index: LessonIndex by lazy {
        (LessonIndexFormat.decode(v2IndexText, "v2") as IndexDecodeResult.Ok).index
    }

    private fun TestScope.realDataStore(): DataStore<Preferences> =
        testDataStore(tempFolder.newFolder(), backgroundScope)

    private fun TestScope.content(
        assets: FakeRemoteAssets,
        dataStore: DataStore<Preferences> = realDataStore(),
        packRoot: File = tempFolder.newFolder(),
        recorder: RecordingAppLogger = logger,
    ) = DefaultLessonContent(
        assets = assets,
        settings = settings,
        dataStore = dataStore,
        extractor = PackExtractor(packRoot),
        ioDispatcher = StandardTestDispatcher(testScheduler),
        logger = recorder,
    )

    private fun samples() = FakeRemoteAssets.withSamples(tempFolder.newFolder())

    private fun warnings(recorder: RecordingAppLogger = logger) =
        recorder.entries.filter { it.level == LogLevel.WARN && it.tag == "Lessons" }

    @Test
    fun library_firstOpen_emitsLoadingThenReady() = runTest {
        val dataStore = realDataStore()
        val states = content(samples(), dataStore).library().take(2).toList()

        assertEquals(listOf(LessonLibraryState.Loading, LessonLibraryState.Ready("v1", sampleIndex)), states)
        assertEquals("v1", dataStore.data.first()[savedVersionKey])
    }

    @Test
    fun library_savedVersion_emitsReadyWithoutLoading() = runTest {
        val dataStore = realDataStore()
        dataStore.edit { it[savedVersionKey] = "v1" }
        val assets = samples()
        val states = mutableListOf<LessonLibraryState>()
        withTimeoutOrNull(2_000) { content(assets, dataStore).library().toList(states) }

        assertEquals(listOf(LessonLibraryState.Ready("v1", sampleIndex)), states)
        assertEquals(1, assets.calls.count { it == "lessons/v1/index.json" })

        // Control: with nothing saved, the first state is Loading.
        assertEquals(LessonLibraryState.Loading, content(samples()).library().first())
    }

    @Test
    fun library_newVersion_keepsOldUntilLoaded() = runTest {
        val dataStore = realDataStore()
        dataStore.edit { it[savedVersionKey] = "v1" }
        settings.set(LessonsSettings.CONTENT_VERSION, "v2")
        val assets = samples()
        val gate = CompletableDeferred<AssetResult>()
        assets.blocks["lessons/v2/index.json"] = gate
        val states = mutableListOf<LessonLibraryState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            content(assets, dataStore).library().collect { states += it }
        }
        runCurrent()
        assertEquals(listOf(LessonLibraryState.Ready("v1", sampleIndex)), states)

        val v2File = File(tempFolder.newFolder(), "index.json").apply { writeText(v2IndexText) }
        gate.complete(AssetResult.Ready(v2File))
        runCurrent()

        assertEquals(
            listOf(LessonLibraryState.Ready("v1", sampleIndex), LessonLibraryState.Ready("v2", v2Index)),
            states,
        )
        assertEquals("v2", dataStore.data.first()[savedVersionKey])
    }

    @Test
    fun library_newVersionFails_keepsOldAndLogs() = runTest {
        val dataStore = realDataStore()
        dataStore.edit { it[savedVersionKey] = "v1" }
        settings.set(LessonsSettings.CONTENT_VERSION, "v2")
        val assets = samples()
        assets.results["lessons/v2/index.json"] = AssetResult.Unavailable(AssetFailureReason.NETWORK)
        val states = mutableListOf<LessonLibraryState>()
        withTimeoutOrNull(2_000) { content(assets, dataStore).library().toList(states) }

        assertEquals(listOf(LessonLibraryState.Ready("v1", sampleIndex)), states)
        assertEquals(1, warnings().count { it.message.contains("v2") && it.message.contains("NETWORK") })
        assertEquals("v1", dataStore.data.first()[savedVersionKey])
    }

    @Test
    fun library_keyChangesWhileCollecting_emitsNewVersion() = runTest {
        val folder = tempFolder.newFolder()
        val assets = FakeRemoteAssets.withSamples(folder)
        assets.serve(folder, "lessons/v2/index.json", v2IndexText.toByteArray())
        val states = mutableListOf<LessonLibraryState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            content(assets).library().collect { states += it }
        }
        runCurrent()
        settings.set(LessonsSettings.CONTENT_VERSION, "v2")
        runCurrent()
        settings.set(LessonsSettings.CONTENT_VERSION, "v1")
        runCurrent()

        assertEquals(
            listOf(
                LessonLibraryState.Loading,
                LessonLibraryState.Ready("v1", sampleIndex),
                LessonLibraryState.Ready("v2", v2Index),
                LessonLibraryState.Ready("v1", sampleIndex),
            ),
            states,
        )
    }

    @Test
    fun library_firstOpenFails_emitsUnavailable() = runTest {
        val cases = listOf(
            AssetFailureReason.NETWORK to LessonLoadFailure.NETWORK,
            AssetFailureReason.NOT_FOUND to LessonLoadFailure.NOT_FOUND,
            AssetFailureReason.OTHER to LessonLoadFailure.OTHER,
        )
        for ((reason, failure) in cases) {
            val assets = FakeRemoteAssets()
            assets.results["lessons/v1/index.json"] = AssetResult.Unavailable(reason)
            assertEquals(
                "$reason",
                listOf(LessonLibraryState.Loading, LessonLibraryState.Unavailable(failure)),
                content(assets).library().take(2).toList(),
            )
        }

        val broken = FakeRemoteAssets().apply {
            serve(tempFolder.newFolder(), "lessons/v1/index.json", "{".toByteArray())
        }
        assertEquals(
            listOf(LessonLibraryState.Loading, LessonLibraryState.Unavailable(LessonLoadFailure.INVALID)),
            content(broken).library().take(2).toList(),
        )
    }

    @Test
    fun library_collectAgain_retries() = runTest {
        val assets = FakeRemoteAssets()
        assets.results["lessons/v1/index.json"] = AssetResult.Unavailable(AssetFailureReason.NETWORK)
        val content = content(assets)
        assertEquals(
            listOf(LessonLibraryState.Loading, LessonLibraryState.Unavailable(LessonLoadFailure.NETWORK)),
            content.library().take(2).toList(),
        )

        assets.serve(tempFolder.newFolder(), "lessons/v1/index.json", resourceText("index-v1.json").toByteArray())
        assertEquals(
            listOf(LessonLibraryState.Loading, LessonLibraryState.Ready("v1", sampleIndex)),
            content.library().take(2).toList(),
        )
        assertEquals(2, assets.calls.count { it == "lessons/v1/index.json" })
    }

    @Test
    fun library_invalidKey_usesDefaultAndLogs() = runTest {
        settings.set(LessonsSettings.CONTENT_VERSION, "v2/")
        val assets = samples()
        val states = content(assets).library().take(2).toList()

        assertEquals(listOf(LessonLibraryState.Loading, LessonLibraryState.Ready("v1", sampleIndex)), states)
        assertEquals(listOf("lessons/v1/index.json"), assets.calls)
        assertEquals(1, warnings().size)
    }

    @Test
    fun library_savedVersionUnusable_fallsBackToKey() = runTest {
        val dataStore = realDataStore()
        dataStore.edit { it[savedVersionKey] = "v1" }
        settings.set(LessonsSettings.CONTENT_VERSION, "v2")
        val folder = tempFolder.newFolder()
        val assets = FakeRemoteAssets()
        assets.results["lessons/v1/index.json"] = AssetResult.Unavailable(AssetFailureReason.NETWORK)
        assets.serve(folder, "lessons/v2/index.json", v2IndexText.toByteArray())

        assertEquals(
            listOf(LessonLibraryState.Loading, LessonLibraryState.Ready("v2", v2Index)),
            content(assets, dataStore).library().take(2).toList(),
        )
    }

    @Test
    fun library_saveVersionFails_stillReady() = runTest {
        assertEquals(
            listOf(LessonLibraryState.Loading, LessonLibraryState.Ready("v1", sampleIndex)),
            content(samples(), UnwritableDataStore()).library().take(2).toList(),
        )
        assertEquals(1, warnings().size)

        assertEquals(
            listOf(LessonLibraryState.Loading, LessonLibraryState.Ready("v1", sampleIndex)),
            content(samples(), UnreadableDataStore()).library().take(2).toList(),
        )
    }

    @Test
    fun thumbnail_readyOrNull() = runTest {
        val folder = tempFolder.newFolder()
        val assets = FakeRemoteAssets()
        assets.serve(folder, "lessons/v1/bouncing-ball/thumb.webp", byteArrayOf(1, 2))
        val entry = sampleIndex.entry("bouncing-ball")!!
        val content = content(assets)

        assertEquals(File(folder, "served/lessons/v1/bouncing-ball/thumb.webp"), content.thumbnail("v1", entry))

        assets.results["lessons/v1/bouncing-ball/thumb.webp"] = AssetResult.Unavailable(AssetFailureReason.NETWORK)
        assertEquals(null, content.thumbnail("v1", entry))
    }

    @Test
    fun loadPack_sample_returnsReadyPack() = runTest {
        val assets = samples()
        val packRoot = tempFolder.newFolder()
        val result = content(assets, packRoot = packRoot).loadPack("v1", "bouncing-ball")

        assertTrue("$result", result is LessonPackResult.Ready)
        val pack = (result as LessonPackResult.Ready).pack
        assertEquals("bouncing-ball", pack.lessonId)
        assertEquals("v1", pack.contentVersion)
        assertEquals(3, pack.steps.size)
        assertEquals(File(packRoot, "v1/bouncing-ball"), pack.dir)
        assertTrue(assets.calls.containsAll(listOf("lessons/v1/index.json", "lessons/v1/bouncing-ball/lesson.zip")))
    }

    @Test
    fun loadPack_failures() = runTest {
        val pack = "lessons/v1/bouncing-ball/lesson.zip"

        suspend fun failure(
            version: String = "v1",
            lessonId: String = "bouncing-ball",
            setup: (FakeRemoteAssets, File) -> Unit,
        ): LessonLoadFailure? {
            val folder = tempFolder.newFolder()
            val assets = FakeRemoteAssets.withSamples(folder)
            setup(assets, folder)
            val recorder = RecordingAppLogger()
            val result = content(assets, recorder = recorder).loadPack(version, lessonId)
            val warning = warnings(recorder).single()
            assertFalse(warning.message, warning.message.contains("Trace the red circle"))
            assertFalse(warning.message, warning.message.contains("Draw the ball"))
            return (result as? LessonPackResult.Unavailable)?.failure
        }

        assertEquals(
            LessonLoadFailure.NETWORK,
            failure { assets, _ -> assets.results[pack] = AssetResult.Unavailable(AssetFailureReason.NETWORK) },
        )
        assertEquals(LessonLoadFailure.NOT_FOUND, failure(lessonId = "no-such-lesson") { _, _ -> })
        assertEquals(
            LessonLoadFailure.NETWORK,
            failure { assets, _ ->
                assets.results["lessons/v1/index.json"] = AssetResult.Unavailable(AssetFailureReason.NETWORK)
            },
        )
        assertEquals(
            LessonLoadFailure.INVALID,
            failure { assets, folder ->
                val entries = linkedMapOf(
                    "lesson.json" to samplePackEntries().getValue("lesson.json"),
                    "../evil.txt" to "evil".toByteArray(),
                )
                assets.results[pack] = AssetResult.Ready(zipOf(File(folder, "evil.zip"), entries))
            },
        )
        assertEquals(
            LessonLoadFailure.INVALID,
            failure { assets, folder ->
                val entries = LinkedHashMap(samplePackEntries())
                entries["lesson.json"] =
                    editedSample("lesson-v1.json") { it["id"] = JsonPrimitive("other") }.toByteArray()
                assets.results[pack] = AssetResult.Ready(zipOf(File(folder, "other.zip"), entries))
            },
        )
        assertEquals(LessonLoadFailure.INVALID, failure(version = "v2/") { _, _ -> })
        assertEquals(
            LessonLoadFailure.INVALID,
            failure { assets, folder ->
                val index = editedSample("index-v1.json") { root ->
                    root.editItem("lessons", 1) { it["stepCount"] = JsonPrimitive(4) }
                }
                assets.serve(folder, "lessons/v1/index.json", index.toByteArray())
            },
        )

        // Retry with the same object after a network error.
        val folder = tempFolder.newFolder()
        val assets = FakeRemoteAssets.withSamples(folder)
        val sampleZip = assets.results.getValue(pack)
        assets.results[pack] = AssetResult.Unavailable(AssetFailureReason.NETWORK)
        val recorder = RecordingAppLogger()
        val content = content(assets, recorder = recorder)
        assertEquals(
            LessonPackResult.Unavailable(LessonLoadFailure.NETWORK),
            content.loadPack("v1", "bouncing-ball"),
        )
        assets.results[pack] = sampleZip
        assertTrue(content.loadPack("v1", "bouncing-ball") is LessonPackResult.Ready)
        assertEquals(
            1,
            recorder.entries.count { it.level == LogLevel.INFO && it.message.contains("v1/bouncing-ball") },
        )
    }

    @Test
    fun loadPack_oldVersion_usesOldPaths() = runTest {
        settings.set(LessonsSettings.CONTENT_VERSION, "v2")
        val folder = tempFolder.newFolder()
        val assets = FakeRemoteAssets.withSamples(folder)
        assets.serve(folder, "lessons/v2/index.json", v2IndexText.toByteArray())
        assets.results["lessons/v2/bouncing-ball/lesson.zip"] =
            assets.results.getValue("lessons/v1/bouncing-ball/lesson.zip")
        val progress = LessonProgress(
            inProgress = mapOf("bouncing-ball" to LessonAttempt("v1", 1, 3, "6f1c2a3e-0000-4000-8000-000000000001", 0)),
        )

        val result = content(assets).loadPack(packVersionFor("bouncing-ball", progress, "v2"), "bouncing-ball")

        assertTrue("$result", result is LessonPackResult.Ready)
        assertEquals("v1", (result as LessonPackResult.Ready).pack.contentVersion)
        assertTrue("${assets.calls}", assets.calls.isNotEmpty() && assets.calls.all { it.startsWith("lessons/v1/") })
    }

    @Test
    fun loadPack_notAZip_returnsInvalid() = runTest {
        val folder = tempFolder.newFolder()
        val assets = FakeRemoteAssets.withSamples(folder)
        assets.serve(folder, "lessons/v1/bouncing-ball/lesson.zip", "hello".toByteArray())

        assertEquals(
            LessonPackResult.Unavailable(LessonLoadFailure.INVALID),
            content(assets).loadPack("v1", "bouncing-ball"),
        )
    }

    @Test
    fun savedVersionKey_isLessonsIndexVersion() = runTest {
        val dataStore = realDataStore()
        content(samples(), dataStore).library().take(2).toList()
        assertEquals("v1", dataStore.data.first()[stringPreferencesKey("lessons_index_version")])
    }
}
