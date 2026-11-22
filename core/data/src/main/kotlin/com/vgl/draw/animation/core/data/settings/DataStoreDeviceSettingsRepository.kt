package com.vgl.draw.animation.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vgl.draw.animation.core.logging.AppLogger
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen

/**
 * [DeviceSettingsRepository] on the shared `app_preferences` DataStore. Key names are a contract with data already
 * stored on users' devices: renaming one loses the user's choice.
 */
internal class DataStoreDeviceSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger,
) : DeviceSettingsRepository {
    // A read error must never crash or freeze a screen: emit empty data (defaults), wait, then read again.
    // Corrupted files are handled once for the whole app in DataStoreModule.
    private val preferences: Flow<Preferences> = dataStore.data
        .retryWhen { cause, attempt ->
            if (cause !is IOException) return@retryWhen false
            logger.warn(TAG, "Device settings not read, retry ${attempt + 1}", cause)
            emit(emptyPreferences())
            delay(retryDelayMillis(attempt))
            true
        }

    override val defaultFrameRate: Flow<Int> = preferences
        .map { prefs ->
            prefs[FRAME_RATE_KEY]?.takeIf { it in DeviceSettingsValues.FRAME_RATES }
                ?: DeviceSettingsValues.DEFAULT_FRAME_RATE
        }
        .distinctUntilChanged()

    override val defaultAspectRatio: Flow<String> = preferences
        .map { prefs ->
            prefs[ASPECT_RATIO_KEY]?.takeIf { it in DeviceSettingsValues.ASPECT_RATIOS }
                ?: DeviceSettingsValues.DEFAULT_ASPECT_RATIO
        }
        .distinctUntilChanged()

    override val stylusPressure: Flow<Boolean> = preferences
        .map { prefs -> prefs[STYLUS_PRESSURE_KEY] ?: DeviceSettingsValues.DEFAULT_STYLUS_PRESSURE }
        .distinctUntilChanged()

    override val firstStrokeDrawn: Flow<Boolean> = preferences
        .map { prefs -> prefs[FIRST_STROKE_KEY] == true }
        .distinctUntilChanged()

    override suspend fun setDefaultFrameRate(frameRate: Int) {
        require(frameRate in DeviceSettingsValues.FRAME_RATES) { "Frame rate not allowed: $frameRate" }
        dataStore.edit { it[FRAME_RATE_KEY] = frameRate }
    }

    override suspend fun setDefaultAspectRatio(aspectRatio: String) {
        require(aspectRatio in DeviceSettingsValues.ASPECT_RATIOS) { "Aspect ratio not allowed: $aspectRatio" }
        dataStore.edit { it[ASPECT_RATIO_KEY] = aspectRatio }
    }

    override suspend fun setStylusPressure(enabled: Boolean) {
        dataStore.edit { it[STYLUS_PRESSURE_KEY] = enabled }
    }

    override suspend fun markFirstStrokeDrawn(): Boolean = try {
        // DataStore skips the disk write when the data does not change, so a repeated call costs no write.
        dataStore.edit { if (it[FIRST_STROKE_KEY] != true) it[FIRST_STROKE_KEY] = true }
        true
    } catch (e: IOException) {
        logger.warn(TAG, "First-stroke mark not written", e)
        false
    }

    private companion object {
        const val TAG = "Settings"
        val FRAME_RATE_KEY = intPreferencesKey("settings_default_frame_rate")
        val ASPECT_RATIO_KEY = stringPreferencesKey("settings_default_aspect_ratio")
        val STYLUS_PRESSURE_KEY = booleanPreferencesKey("settings_stylus_pressure")
        val FIRST_STROKE_KEY = booleanPreferencesKey("first_stroke_drawn")
        const val MAX_RETRY_DELAY_MILLIS = 30_000L

        fun retryDelayMillis(attempt: Long): Long =
            min(1_000L shl attempt.coerceAtMost(5).toInt(), MAX_RETRY_DELAY_MILLIS)
    }
}
