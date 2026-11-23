package com.vgl.draw.animation.core.project.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.vgl.draw.animation.core.project.model.Project
import com.vgl.draw.animation.core.project.model.ProjectId
import com.vgl.draw.animation.core.project.storage.ProjectFile
import com.vgl.draw.animation.core.project.storage.ProjectStore
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/** Keeps preferences in memory; [state] lets a test set or read raw keys. */
internal class MemoryPreferencesDataStore(initial: Preferences = emptyPreferences()) : DataStore<Preferences> {
    val state = MutableStateFlow(initial)
    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

/** A preferences file that can never be read nor written (I/O error). */
internal class UnreadablePreferencesDataStore : DataStore<Preferences> {
    override val data: Flow<Preferences> = flow { throw IOException("disk read failed") }

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        throw IOException("disk write failed")
}

/** A preferences file that is read empty but cannot be written. */
internal class UnwritablePreferencesDataStore : DataStore<Preferences> {
    override val data: Flow<Preferences> = MutableStateFlow(emptyPreferences())

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        throw IOException("disk write failed")
}

/**
 * Wraps a real store and fails on purpose. Nothing is counted nor thrown before [arm], so a test prepares its data
 * through this wrapper (or the inner store) and calls [arm] right before the operation under test. Turns count from 1.
 */
internal class FailingProjectStore(private val delegate: ProjectStore) : ProjectStore by delegate {
    /** Throws `IOException(writeFileMessage)` at this turn of [writeFile]. */
    var failWriteFileAt: Int? = null
    var writeFileMessage: String = "Could not replace a project file"

    /** Throws `IOException(writeMessage)` at this turn of [write]. */
    var failWriteAt: Int? = null
    var writeMessage: String = "Could not replace a project file"

    /** [write] at this turn completes [writeReached], then waits for [releaseWrite] before writing. */
    var pauseWriteAt: Int? = null
    val writeReached = CompletableDeferred<Unit>()
    val releaseWrite = CompletableDeferred<Unit>()

    var failDelete: Boolean = false
    var failDeleteIncomplete: Boolean = false

    /** Calls of [deleteIncomplete], counted even before [arm]. */
    var deleteIncompleteCalls: Int = 0
        private set

    private var armed = false
    private var writeFileTurns = 0
    private var writeTurns = 0

    fun arm() {
        armed = true
    }

    override suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray) {
        if (armed) {
            writeFileTurns++
            if (writeFileTurns == failWriteFileAt) throw IOException(writeFileMessage)
        }
        delegate.writeFile(id, file, bytes)
    }

    override suspend fun write(project: Project) {
        if (armed) {
            writeTurns++
            if (writeTurns == failWriteAt) throw IOException(writeMessage)
            if (writeTurns == pauseWriteAt) {
                writeReached.complete(Unit)
                releaseWrite.await()
            }
        }
        delegate.write(project)
    }

    override suspend fun delete(id: ProjectId) {
        if (armed && failDelete) throw IOException("Could not delete a project folder")
        delegate.delete(id)
    }

    override suspend fun deleteIncomplete(): Int {
        deleteIncompleteCalls++
        if (armed && failDeleteIncomplete) throw IOException("Could not delete a project folder")
        return delegate.deleteIncomplete()
    }
}

/** A counter in memory; [failIncrement] makes [increment] throw like a full disk. */
internal class MemoryCounter : CreatedProjectsCounter {
    val value = MutableStateFlow(0)
    var failIncrement: Boolean = false

    override val count: Flow<Int> = value

    override suspend fun increment() {
        if (failIncrement) throw IOException("disk write failed")
        value.value += 1
    }
}

/** A clock the test moves by setting [now]. */
internal class FakeClock(var now: Long = 1_000L) : () -> Long {
    override fun invoke(): Long = now
}
