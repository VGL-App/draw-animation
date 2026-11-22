package com.vgl.draw.animation.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Device settings shared by several features (plan T-005). */
class DataStoreDeviceSettingsRepositoryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val logger = RecordingAppLogger()

    // Raw keys written here on purpose, not taken from the implementation: renaming a key must turn tests red.
    private val frameRateKey = intPreferencesKey("settings_default_frame_rate")
    private val aspectRatioKey = stringPreferencesKey("settings_default_aspect_ratio")
    private val stylusPressureKey = booleanPreferencesKey("settings_stylus_pressure")
    private val firstStrokeKey = booleanPreferencesKey("first_stroke_drawn")

    private fun repository(store: DataStore<Preferences>) = DataStoreDeviceSettingsRepository(store, logger)

    @Test
    fun emptyStore_returnsDefaults_untilWritten() = runTest {
        val repository = repository(MemoryDataStore())
        assertEquals(12, repository.defaultFrameRate.first())
        assertEquals("9:16", repository.defaultAspectRatio.first())
        assertEquals(true, repository.stylusPressure.first())
        assertEquals(false, repository.firstStrokeDrawn.first())

        // Control: a constant implementation would fail here.
        repository.setDefaultFrameRate(24)
        repository.setDefaultAspectRatio("16:9")
        repository.setStylusPressure(false)
        repository.markFirstStrokeDrawn()
        assertEquals(24, repository.defaultFrameRate.first())
        assertEquals("16:9", repository.defaultAspectRatio.first())
        assertEquals(false, repository.stylusPressure.first())
        assertEquals(true, repository.firstStrokeDrawn.first())
    }

    @Test
    fun values_matchSpec() {
        assertEquals(listOf(6, 8, 12, 15, 24, 30), DeviceSettingsValues.FRAME_RATES)
        assertEquals(listOf("16:9", "9:16", "1:1"), DeviceSettingsValues.ASPECT_RATIOS)
        assertEquals(12, DeviceSettingsValues.DEFAULT_FRAME_RATE)
        assertEquals("9:16", DeviceSettingsValues.DEFAULT_ASPECT_RATIO)
        assertEquals(true, DeviceSettingsValues.DEFAULT_STYLUS_PRESSURE)
        assertTrue(DeviceSettingsValues.DEFAULT_FRAME_RATE in DeviceSettingsValues.FRAME_RATES)
        assertTrue(DeviceSettingsValues.DEFAULT_ASPECT_RATIO in DeviceSettingsValues.ASPECT_RATIOS)
    }

    @Test
    fun setDefaultFrameRate_eachAllowedValue_readsBack() = runTest {
        val repository = repository(MemoryDataStore())
        for (value in listOf(6, 8, 12, 15, 24, 30)) {
            repository.setDefaultFrameRate(value)
            assertEquals(value, repository.defaultFrameRate.first())
        }
    }

    @Test
    fun setDefaultAspectRatio_eachAllowedValue_readsBack() = runTest {
        val repository = repository(MemoryDataStore())
        for (value in listOf("16:9", "9:16", "1:1")) {
            repository.setDefaultAspectRatio(value)
            assertEquals(value, repository.defaultAspectRatio.first())
        }
    }

    @Test
    fun setStylusPressure_offThenOn_readsBack() = runTest {
        val repository = repository(MemoryDataStore())
        repository.setStylusPressure(false)
        assertEquals(false, repository.stylusPressure.first())
        repository.setStylusPressure(true)
        assertEquals(true, repository.stylusPressure.first())
    }

    @Test
    fun setDefaultFrameRate_notAllowed_throwsAndKeepsValue() = runTest {
        val store = MemoryDataStore()
        val repository = repository(store)
        repository.setDefaultFrameRate(24)
        for (value in listOf(10, 0, -1, 60)) {
            val e = runCatching { repository.setDefaultFrameRate(value) }.exceptionOrNull()
            assertTrue("$value: $e", e is IllegalArgumentException)
        }
        assertEquals(24, repository.defaultFrameRate.first())
        assertEquals(24, store.state.value[frameRateKey])
    }

    @Test
    fun setDefaultAspectRatio_notAllowed_throwsAndKeepsValue() = runTest {
        val store = MemoryDataStore()
        val repository = repository(store)
        repository.setDefaultAspectRatio("1:1")
        for (value in listOf("4:5", "", "9:16 ", "16x9")) {
            val e = runCatching { repository.setDefaultAspectRatio(value) }.exceptionOrNull()
            assertTrue("'$value': $e", e is IllegalArgumentException)
        }
        assertEquals("1:1", repository.defaultAspectRatio.first())
        assertEquals("1:1", store.state.value[aspectRatioKey])
    }

    @Test
    fun storedInvalidValue_readsDefault() = runTest {
        val invalid = repository(MemoryDataStore(preferencesOf(frameRateKey to 10, aspectRatioKey to "4:5")))
        assertEquals(12, invalid.defaultFrameRate.first())
        assertEquals("9:16", invalid.defaultAspectRatio.first())

        // Control: valid values set with the raw keys are read back (this also pins the key names on read).
        val valid = repository(
            MemoryDataStore(
                preferencesOf(
                    frameRateKey to 24,
                    aspectRatioKey to "1:1",
                    stylusPressureKey to false,
                    firstStrokeKey to true,
                ),
            ),
        )
        assertEquals(24, valid.defaultFrameRate.first())
        assertEquals("1:1", valid.defaultAspectRatio.first())
        assertEquals(false, valid.stylusPressure.first())
        assertEquals(true, valid.firstStrokeDrawn.first())
    }

    @Test
    fun writes_useStableKeys() = runTest {
        val store = MemoryDataStore()
        val repository = repository(store)
        repository.setDefaultFrameRate(24)
        repository.setDefaultAspectRatio("16:9")
        repository.setStylusPressure(false)
        repository.markFirstStrokeDrawn()
        val stored = store.state.value
        assertEquals(24, stored[frameRateKey])
        assertEquals("16:9", stored[aspectRatioKey])
        assertEquals(false, stored[stylusPressureKey])
        assertEquals(true, stored[firstStrokeKey])
        assertEquals(4, stored.asMap().size)
    }

    @Test
    fun write_keepsOtherKeys() = runTest {
        val adsKey = booleanPreferencesKey("ads_first_open_done")
        val store = MemoryDataStore(preferencesOf(adsKey to true, aspectRatioKey to "1:1"))
        val repository = repository(store)
        repository.setDefaultFrameRate(24)
        repository.markFirstStrokeDrawn()
        assertEquals(true, store.state.value[adsKey])
        assertEquals("1:1", repository.defaultAspectRatio.first())
    }

    @Test
    fun markFirstStrokeDrawn_isOneWayAndIdempotent() = runTest {
        val repository = repository(MemoryDataStore())
        assertEquals(false, repository.firstStrokeDrawn.first())
        assertTrue(repository.markFirstStrokeDrawn())
        assertEquals(true, repository.firstStrokeDrawn.first())
        assertTrue(repository.markFirstStrokeDrawn())
        assertEquals(true, repository.firstStrokeDrawn.first())
        repository.setStylusPressure(false)
        assertEquals(true, repository.firstStrokeDrawn.first())
    }

    @Test
    fun firstStrokeDrawn_emitsWhileCollecting() = runTest {
        val repository = repository(MemoryDataStore())
        repository.firstStrokeDrawn.test {
            assertEquals(false, awaitItem())
            repository.markFirstStrokeDrawn()
            assertEquals(true, awaitItem())
            repository.markFirstStrokeDrawn()
            expectNoEvents()
        }
    }

    @Test
    fun defaultFrameRate_emitsWhileCollecting() = runTest {
        val repository = repository(MemoryDataStore())
        repository.defaultFrameRate.test {
            assertEquals(12, awaitItem())
            repository.setDefaultFrameRate(24)
            assertEquals(24, awaitItem())
            repository.setDefaultFrameRate(24)
            repository.setStylusPressure(false)
            expectNoEvents()
        }
    }

    @Test
    fun readFailsOnce_emitsDefaultThenStoredValue() = runTest {
        val stored = preferencesOf(
            frameRateKey to 24,
            aspectRatioKey to "16:9",
            stylusPressureKey to false,
            firstStrokeKey to true,
        )

        val frameRateLogger = RecordingAppLogger()
        val frameRate = DataStoreDeviceSettingsRepository(FlakyDataStore(1, stored), frameRateLogger)
        assertEquals(listOf(12, 24), frameRate.defaultFrameRate.take(2).toList())
        assertOneIoWarning(frameRateLogger)

        val aspectRatioLogger = RecordingAppLogger()
        val aspectRatio = DataStoreDeviceSettingsRepository(FlakyDataStore(1, stored), aspectRatioLogger)
        assertEquals(listOf("9:16", "16:9"), aspectRatio.defaultAspectRatio.take(2).toList())
        assertOneIoWarning(aspectRatioLogger)

        val stylusLogger = RecordingAppLogger()
        val stylus = DataStoreDeviceSettingsRepository(FlakyDataStore(1, stored), stylusLogger)
        assertEquals(listOf(true, false), stylus.stylusPressure.take(2).toList())
        assertOneIoWarning(stylusLogger)

        val markLogger = RecordingAppLogger()
        val mark = DataStoreDeviceSettingsRepository(FlakyDataStore(1, stored), markLogger)
        assertEquals(listOf(false, true), mark.firstStrokeDrawn.take(2).toList())
        assertOneIoWarning(markLogger)
    }

    @Test
    fun readAlwaysFails_returnsDefaultsAndLogs() = runTest {
        val repository = repository(UnreadableDataStore())
        assertEquals(12, repository.defaultFrameRate.first())
        assertEquals("9:16", repository.defaultAspectRatio.first())
        assertEquals(true, repository.stylusPressure.first())
        assertEquals(false, repository.firstStrokeDrawn.first())

        val freshLogger = RecordingAppLogger()
        DataStoreDeviceSettingsRepository(UnreadableDataStore(), freshLogger).defaultFrameRate.first()
        assertOneIoWarning(freshLogger)
    }

    @Test
    fun readFailsWithOtherError_throws() = runTest {
        val repository = repository(BrokenDataStore())
        val e = runCatching { repository.defaultFrameRate.first() }.exceptionOrNull()
        assertTrue("$e", e is IllegalStateException)
        assertTrue(logger.entries.none { it.level == LogLevel.WARN })
    }

    @Test
    fun setters_writeFails_throwIOException() = runTest {
        val repository = repository(UnwritableDataStore())
        val frameRate = runCatching { repository.setDefaultFrameRate(24) }.exceptionOrNull()
        assertTrue("$frameRate", frameRate is IOException)
        val aspectRatio = runCatching { repository.setDefaultAspectRatio("16:9") }.exceptionOrNull()
        assertTrue("$aspectRatio", aspectRatio is IOException)
        val stylus = runCatching { repository.setStylusPressure(false) }.exceptionOrNull()
        assertTrue("$stylus", stylus is IOException)
        // The value is checked before writing.
        val invalid = runCatching { repository.setDefaultFrameRate(10) }.exceptionOrNull()
        assertTrue("$invalid", invalid is IllegalArgumentException)
    }

    @Test
    fun markFirstStrokeDrawn_writeFails_returnsFalseAndLogs() = runTest {
        val failingLogger = RecordingAppLogger()
        assertFalse(DataStoreDeviceSettingsRepository(UnwritableDataStore(), failingLogger).markFirstStrokeDrawn())
        assertOneIoWarning(failingLogger)

        // Control: a writable store stores the mark without a warning.
        val okLogger = RecordingAppLogger()
        assertTrue(DataStoreDeviceSettingsRepository(MemoryDataStore(), okLogger).markFirstStrokeDrawn())
        assertTrue(okLogger.entries.none { it.level == LogLevel.WARN })
    }

    @Test
    fun realFile_valuesSurviveNewInstance() = runTest {
        val file = File(tempFolder.root, "app_preferences.preferences_pb")
        val job1 = Job()
        val job2 = Job()
        val jobControl = Job()
        try {
            val first = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job1)) { file }
            val writer = DataStoreDeviceSettingsRepository(first, RecordingAppLogger())
            writer.setDefaultFrameRate(24)
            writer.setDefaultAspectRatio("16:9")
            writer.setStylusPressure(false)
            assertTrue(writer.markFirstStrokeDrawn())
            job1.cancelAndJoin()

            val second = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job2)) { file }
            val reader = DataStoreDeviceSettingsRepository(second, RecordingAppLogger())
            assertEquals(24, reader.defaultFrameRate.first())
            assertEquals("16:9", reader.defaultAspectRatio.first())
            assertEquals(false, reader.stylusPressure.first())
            assertEquals(true, reader.firstStrokeDrawn.first())
            job2.cancelAndJoin()

            // Control: a file never written reads the defaults.
            val otherFile = File(tempFolder.root, "other.preferences_pb")
            val control = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + jobControl)) {
                otherFile
            }
            val empty = DataStoreDeviceSettingsRepository(control, RecordingAppLogger())
            assertEquals(12, empty.defaultFrameRate.first())
            assertEquals("9:16", empty.defaultAspectRatio.first())
            assertEquals(true, empty.stylusPressure.first())
            assertEquals(false, empty.firstStrokeDrawn.first())
            jobControl.cancelAndJoin()
        } finally {
            job1.cancel()
            job2.cancel()
            jobControl.cancel()
        }
    }

    private fun assertOneIoWarning(recorder: RecordingAppLogger) {
        val warnings = recorder.entries.filter { it.level == LogLevel.WARN }
        assertEquals(1, warnings.size)
        assertEquals("Settings", warnings.single().tag)
        assertTrue("${warnings.single().error}", warnings.single().error is IOException)
    }
}
