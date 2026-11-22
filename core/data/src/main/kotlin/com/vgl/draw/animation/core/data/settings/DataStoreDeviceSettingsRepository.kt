package com.vgl.draw.animation.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vgl.draw.animation.core.logging.AppLogger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class DataStoreDeviceSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger,
) : DeviceSettingsRepository {
    override val defaultFrameRate: Flow<Int> get() = TODO()
    override val defaultAspectRatio: Flow<String> get() = TODO()
    override val stylusPressure: Flow<Boolean> get() = TODO()
    override val firstStrokeDrawn: Flow<Boolean> get() = TODO()

    override suspend fun setDefaultFrameRate(frameRate: Int): Unit = TODO()

    override suspend fun setDefaultAspectRatio(aspectRatio: String): Unit = TODO()

    override suspend fun setStylusPressure(enabled: Boolean): Unit = TODO()

    override suspend fun markFirstStrokeDrawn(): Boolean = TODO()
}
