package com.vgl.draw.animation.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/** Keeps preferences in memory; [state] lets a test set or read raw keys. */
internal class MemoryDataStore(initial: Preferences = emptyPreferences()) : DataStore<Preferences> {
    val state = MutableStateFlow(initial)
    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

/** A preferences file that can never be read nor written (I/O error). */
internal class UnreadableDataStore : DataStore<Preferences> {
    override val data: Flow<Preferences> = flow { throw IOException("disk read failed") }

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        throw IOException("disk write failed")
}

/** Fails to read [failuresBeforeSuccess] times, then serves [stored] (a transient I/O error). */
internal class FlakyDataStore(private var failuresBeforeSuccess: Int, private val stored: Preferences) :
    DataStore<Preferences> {
    override val data: Flow<Preferences> = flow {
        if (failuresBeforeSuccess > 0) {
            failuresBeforeSuccess--
            throw IOException("transient read failure")
        }
        emit(stored)
    }

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences = transform(stored)
}

/** A preferences file that is read empty but cannot be written. */
internal class UnwritableDataStore : DataStore<Preferences> {
    override val data: Flow<Preferences> = MutableStateFlow(emptyPreferences())

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        throw IOException("disk write failed")
}

/** A store whose read fails with an error that is not an I/O error (a programming error). */
internal class BrokenDataStore : DataStore<Preferences> {
    override val data: Flow<Preferences> = flow { throw IllegalStateException("broken store") }

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        throw IllegalStateException("broken store")
}
