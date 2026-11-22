package com.vgl.draw.animation.core.project.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vgl.draw.animation.core.logging.AppLogger
import kotlinx.coroutines.flow.Flow

internal interface CreatedProjectsCounter {
    val count: Flow<Int>

    suspend fun increment()
}

internal class DataStoreCreatedProjectsCounter(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger,
) : CreatedProjectsCounter {
    override val count: Flow<Int> get() = TODO()

    override suspend fun increment(): Unit = TODO()
}
