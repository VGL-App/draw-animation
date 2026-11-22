package com.vgl.draw.animation.core.project.repository

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import com.vgl.draw.animation.core.logging.LogLevel
import com.vgl.draw.animation.core.logging.RecordingAppLogger
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Count of projects ever created, on the shared `app_preferences` DataStore (plan T-002). */
class CreatedProjectsCounterTest {
    private val logger = RecordingAppLogger()

    // Raw key written here on purpose, not taken from the implementation: renaming it must turn tests red.
    private val key = intPreferencesKey("projects_created_count")

    @Test
    fun count_default_zero() = runTest {
        val counter = DataStoreCreatedProjectsCounter(MemoryPreferencesDataStore(), logger)
        assertEquals(0, counter.count.first())
    }

    @Test
    fun count_incrementThenRead_one() = runTest {
        val store = MemoryPreferencesDataStore()
        val counter = DataStoreCreatedProjectsCounter(store, logger)

        counter.increment()

        assertEquals(1, counter.count.first())
        assertEquals(1, store.state.value[key])
    }

    @Test
    fun count_negativeStored_readsZero() = runTest {
        val store = MemoryPreferencesDataStore(preferencesOf(key to -3))
        val counter = DataStoreCreatedProjectsCounter(store, logger)
        assertEquals(0, counter.count.first())

        counter.increment()

        assertEquals(1, counter.count.first())
    }

    @Test
    fun count_unreadable_emitsZeroAndLogs() = runTest {
        val counter = DataStoreCreatedProjectsCounter(UnreadablePreferencesDataStore(), logger)

        assertEquals(0, counter.count.first())
        assertTrue(logger.entries.any { it.level == LogLevel.WARN })
    }

    @Test
    fun increment_unwritable_throwsIOException() = runTest {
        val counter = DataStoreCreatedProjectsCounter(UnwritablePreferencesDataStore(), logger)

        val e = runCatching { counter.increment() }.exceptionOrNull()

        assertTrue("$e", e is IOException)
    }
}
