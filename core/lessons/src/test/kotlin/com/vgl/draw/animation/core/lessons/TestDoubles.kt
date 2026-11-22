package com.vgl.draw.animation.core.lessons

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.vgl.draw.animation.core.assets.AssetFailureReason
import com.vgl.draw.animation.core.assets.AssetPath
import com.vgl.draw.animation.core.assets.AssetResult
import com.vgl.draw.animation.core.assets.RemoteAssets
import com.vgl.draw.animation.core.config.RemoteKey
import com.vgl.draw.animation.core.config.RemoteSettings
import java.io.File
import java.io.IOException
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/** Text of a sample file of src/test/resources, copied word for word from plan T-048, section 3.1. */
internal fun resourceText(name: String): String =
    checkNotNull(object {}.javaClass.getResource("/$name")) { "Missing test resource $name" }.readText()

/** The sample file [name] as a JSON object, changed by [change]: "tệp mẫu sửa X" of plan T-048, section 4. */
internal fun editedSample(name: String, change: (MutableMap<String, JsonElement>) -> Unit): String {
    val map = Json.parseToJsonElement(resourceText(name)).jsonObject.toMutableMap()
    change(map)
    return JsonObject(map).toString()
}

/** Changes the element at [index] of the array [key] of a JSON object map. */
internal fun MutableMap<String, JsonElement>.editItem(
    key: String,
    index: Int,
    change: (MutableMap<String, JsonElement>) -> Unit,
) {
    val items = (this.getValue(key) as JsonArray).toMutableList()
    val item = items[index].jsonObject.toMutableMap()
    change(item)
    items[index] = JsonObject(item)
    this[key] = JsonArray(items)
}

/**
 * Serves files by path. [results] maps a path to its result (by default [AssetResult.Unavailable] NOT_FOUND);
 * [calls] lists every path asked; a path in [blocks] waits for its deferred result.
 */
internal class FakeRemoteAssets : RemoteAssets {
    val results = mutableMapOf<String, AssetResult>()
    val blocks = mutableMapOf<String, CompletableDeferred<AssetResult>>()
    private val recorded = mutableListOf<String>()
    val calls: List<String> get() = synchronized(recorded) { recorded.toList() }

    override suspend fun load(path: AssetPath): AssetResult {
        synchronized(recorded) { recorded += path.value }
        blocks[path.value]?.let { return it.await() }
        return results[path.value] ?: AssetResult.Unavailable(AssetFailureReason.NOT_FOUND)
    }

    /** Serves [content] at [path] from a file written in [folder]. */
    fun serve(folder: File, path: String, content: ByteArray) {
        val file = File(folder, "served/$path")
        file.parentFile?.mkdirs()
        file.writeBytes(content)
        results[path] = AssetResult.Ready(file)
    }

    companion object {
        /** The sample index at `lessons/v1/index.json` and the sample pack at `lessons/v1/bouncing-ball/lesson.zip`. */
        fun withSamples(folder: File): FakeRemoteAssets = FakeRemoteAssets().apply {
            serve(folder, "lessons/v1/index.json", resourceText("index-v1.json").toByteArray())
            val zip = File(folder, "sample-pack.zip")
            zipOf(zip, samplePackEntries())
            results["lessons/v1/bouncing-ball/lesson.zip"] = AssetResult.Ready(zip)
        }
    }
}

/** Remote values held in one [MutableStateFlow] per key, starting at the key's default. */
internal class FakeRemoteSettings : RemoteSettings {
    private val flows = mutableMapOf<String, MutableStateFlow<Any>>()

    private fun flowOf(key: RemoteKey<*>): MutableStateFlow<Any> =
        synchronized(flows) { flows.getOrPut(key.name) { MutableStateFlow(key.default) } }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> value(key: RemoteKey<T>): Flow<T> = flowOf(key) as Flow<T>

    fun <T : Any> set(key: RemoteKey<T>, value: T) {
        flowOf(key).value = value
    }
}

/** A real preferences DataStore on a file of [folder], run in [scope]. */
internal fun testDataStore(folder: File, scope: CoroutineScope): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(scope = scope) { File(folder, "test.preferences_pb") }

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

/**
 * Writes a zip of [entries] (name to bytes, in order) to [file]. A name ending with "/" is a directory entry.
 * [method] is [ZipEntry.DEFLATED] or [ZipEntry.STORED].
 */
internal fun zipOf(file: File, entries: Map<String, ByteArray>, method: Int = ZipEntry.DEFLATED): File {
    file.parentFile?.mkdirs()
    ZipOutputStream(file.outputStream()).use { zip ->
        for ((name, bytes) in entries) {
            val entry = ZipEntry(name)
            entry.method = method
            if (method == ZipEntry.STORED) {
                entry.size = bytes.size.toLong()
                entry.compressedSize = bytes.size.toLong()
                entry.crc = CRC32().apply { update(bytes) }.value
            }
            zip.putNextEntry(entry)
            zip.write(bytes)
            zip.closeEntry()
        }
    }
    return file
}

/** Entries of the sample pack: lesson-v1.json and a few images of a few different bytes (never decoded). */
internal fun samplePackEntries(): Map<String, ByteArray> = linkedMapOf(
    "lesson.json" to resourceText("lesson-v1.json").toByteArray(),
    "cells/1/0.png" to byteArrayOf(1, 2, 3),
    "cells/3/1.png" to byteArrayOf(4, 5, 6),
    "guides/0.png" to byteArrayOf(7, 8, 9),
    "guides/1.png" to byteArrayOf(10, 11, 12),
    "demo/0/0.png" to byteArrayOf(13, 14, 15),
    "demo/0/1.png" to byteArrayOf(16, 17, 18),
)
