package com.vgl.draw.animation.core.project.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.vgl.draw.animation.core.logging.AppLogger
import java.io.IOException
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen

/** How many projects the user ever created from the Create project screen. Never decreases. */
internal interface CreatedProjectsCounter {
    /** 0 when nothing is stored, the stored value is negative, or the file cannot be read. */
    val count: Flow<Int>

    /** Throws [IOException] when the value cannot be written. */
    suspend fun increment()
}

/**
 * [CreatedProjectsCounter] on the shared `app_preferences` DataStore. The key name is a contract with data already
 * stored on users' devices: renaming it restarts the default names at "My animation 1".
 */
internal class DataStoreCreatedProjectsCounter(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger,
) : CreatedProjectsCounter {
    // A read error must never crash or freeze a screen: emit empty data (0), wait, then read again.
    override val count: Flow<Int> = dataStore.data
        .retryWhen { cause, attempt ->
            if (cause !is IOException) return@retryWhen false
            logger.warn(TAG, "Created projects count not read, retry ${attempt + 1}", cause)
            emit(emptyPreferences())
            delay(retryDelayMillis(attempt))
            true
        }
        .map { it[KEY]?.coerceAtLeast(0) ?: 0 }
        .distinctUntilChanged()

    override suspend fun increment() {
        dataStore.edit { it[KEY] = (it[KEY] ?: 0).coerceAtLeast(0) + 1 }
    }

    private companion object {
        const val TAG = "ProjectRepo"
        val KEY = intPreferencesKey("projects_created_count")
        const val MAX_RETRY_DELAY_MILLIS = 30_000L

        fun retryDelayMillis(attempt: Long): Long =
            min(1_000L shl attempt.coerceAtMost(5).toInt(), MAX_RETRY_DELAY_MILLIS)
    }
}
