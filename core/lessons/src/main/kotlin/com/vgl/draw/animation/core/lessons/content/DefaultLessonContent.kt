package com.vgl.draw.animation.core.lessons.content

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vgl.draw.animation.core.assets.AssetFailureReason
import com.vgl.draw.animation.core.assets.AssetPath
import com.vgl.draw.animation.core.assets.AssetResult
import com.vgl.draw.animation.core.assets.RemoteAssets
import com.vgl.draw.animation.core.config.RemoteSettings
import com.vgl.draw.animation.core.lessons.LessonsSettings
import com.vgl.draw.animation.core.lessons.contentVersionOrDefault
import com.vgl.draw.animation.core.lessons.isContentVersion
import com.vgl.draw.animation.core.logging.AppLogger
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Loads the lesson content through [RemoteAssets] (plan T-048, section 3.1): `lessons/<version>/index.json`, the
 * thumbnails and the packs, extracted by [extractor]. The version of the last index shown is kept in [dataStore], so
 * the next opening shows it at once while the current version loads in the background (spec lessons, KB-2).
 */
internal class DefaultLessonContent(
    private val assets: RemoteAssets,
    private val settings: RemoteSettings,
    private val dataStore: DataStore<Preferences>,
    private val extractor: PackExtractor,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
) : LessonContent {
    override fun library(): Flow<LessonLibraryState> = flow {
        var shown: String? = null
        val saved = readSavedVersion()
        if (saved != null && isContentVersion(saved)) {
            when (val loaded = loadIndex(saved)) {
                is IndexLoad.Loaded -> {
                    emit(LessonLibraryState.Ready(saved, loaded.index))
                    shown = saved
                }
                is IndexLoad.Failed -> logger.warn(TAG, "Saved lesson index $saved not loaded: ${loaded.describe()}")
            }
        }
        if (shown == null) emit(LessonLibraryState.Loading)
        settings.value(LessonsSettings.CONTENT_VERSION)
            .map { raw ->
                val version = contentVersionOrDefault(raw)
                // The raw value is not logged: it is whatever was typed on Firebase.
                if (version != raw) logger.warn(TAG, "Invalid lessons_content_version, using $version")
                version
            }
            .distinctUntilChanged()
            .collect { version ->
                if (version == shown) return@collect
                when (val loaded = loadIndex(version)) {
                    is IndexLoad.Loaded -> {
                        // Saved before emitting, so a collector that stops after this value still finds it saved.
                        saveVersion(version)
                        logger.info(TAG, "Lesson index $version shown")
                        emit(LessonLibraryState.Ready(version, loaded.index))
                        shown = version
                    }
                    is IndexLoad.Failed -> {
                        logger.warn(TAG, "Lesson index $version not loaded: ${loaded.describe()}")
                        // With an index shown, keep it: the new version is tried again on the next change or opening.
                        if (shown == null) emit(LessonLibraryState.Unavailable(loaded.failure))
                    }
                }
            }
    }

    override suspend fun thumbnail(version: String, entry: LessonEntry): File? {
        if (!isContentVersion(version) || entry.thumb != "${entry.id}/thumb.webp" || !isContentId(entry.id)) return null
        return (assets.load(AssetPath("lessons/$version/${entry.thumb}")) as? AssetResult.Ready)?.file
    }

    override suspend fun loadPack(version: String, lessonId: String): LessonPackResult {
        if (!isContentVersion(version)) return packFailure("?", lessonId, LessonLoadFailure.INVALID, "invalid version")
        val entry = when (val loaded = loadIndex(version)) {
            is IndexLoad.Failed -> return packFailure(version, lessonId, loaded.failure, "index: ${loaded.reason}")
            is IndexLoad.Loaded -> loaded.index.entry(lessonId)
                ?: return packFailure(version, lessonId, LessonLoadFailure.NOT_FOUND, "not in the index")
        }
        val zip = when (val result = assets.load(AssetPath("lessons/$version/${entry.pack}"))) {
            is AssetResult.Unavailable ->
                return packFailure(version, entry.id, result.reason.toLoadFailure(), "pack not downloaded")
            is AssetResult.Ready -> result.file
        }
        val decoded = try {
            withContext(ioDispatcher) {
                val dir = extractor.extract(zip, version, entry.id)
                LessonPackFormat.decode(File(dir, "lesson.json").readText(), entry.id, version, dir)
            }
        } catch (e: InvalidPackException) {
            return packFailure(version, entry.id, LessonLoadFailure.INVALID, e.message.orEmpty())
        } catch (e: IOException) {
            return packFailure(version, entry.id, LessonLoadFailure.OTHER, "disk error ${e.javaClass.simpleName}")
        }
        val pack = when (decoded) {
            is PackDecodeResult.Invalid -> return packFailure(
                version,
                entry.id,
                LessonLoadFailure.INVALID,
                decoded.reason,
            )
            is PackDecodeResult.Ok -> decoded.pack
        }
        // The lesson card and the editor must not show two different counts.
        if (pack.steps.size != entry.stepCount) {
            return packFailure(version, entry.id, LessonLoadFailure.INVALID, "step count differs from the index")
        }
        if (pack.frames.size != entry.frameCount) {
            return packFailure(version, entry.id, LessonLoadFailure.INVALID, "frame count differs from the index")
        }
        logger.info(TAG, "Lesson pack $version/${entry.id} opened")
        return LessonPackResult.Ready(pack)
    }

    private fun packFailure(
        version: String,
        lessonId: String,
        failure: LessonLoadFailure,
        reason: String,
    ): LessonPackResult {
        // The lesson id is only logged once known to be a content id (no user text).
        val id = if (isContentId(lessonId)) lessonId else "?"
        logger.warn(TAG, "Lesson pack $version/$id not opened: $failure ($reason)")
        return LessonPackResult.Unavailable(failure)
    }

    private suspend fun loadIndex(version: String): IndexLoad {
        val file = when (val result = assets.load(AssetPath("lessons/$version/index.json"))) {
            is AssetResult.Unavailable -> return IndexLoad.Failed(result.reason.toLoadFailure(), "not downloaded")
            is AssetResult.Ready -> result.file
        }
        val text = try {
            withContext(ioDispatcher) { file.readText() }
        } catch (e: IOException) {
            return IndexLoad.Failed(LessonLoadFailure.OTHER, "not read: ${e.javaClass.simpleName}")
        }
        return when (val decoded = LessonIndexFormat.decode(text, version)) {
            is IndexDecodeResult.Ok -> IndexLoad.Loaded(decoded.index)
            is IndexDecodeResult.Invalid -> IndexLoad.Failed(LessonLoadFailure.INVALID, decoded.reason)
        }
    }

    private suspend fun readSavedVersion(): String? = try {
        dataStore.data.first()[SAVED_VERSION_KEY]
    } catch (e: IOException) {
        logger.warn(TAG, "Saved lesson index version not read", e)
        null
    }

    private suspend fun saveVersion(version: String) {
        try {
            dataStore.edit { it[SAVED_VERSION_KEY] = version }
        } catch (e: IOException) {
            logger.warn(TAG, "Lesson index version not saved", e)
        }
    }

    private sealed interface IndexLoad {
        data class Loaded(val index: LessonIndex) : IndexLoad

        /** [reason] names the cause without any file content, for the log. */
        data class Failed(val failure: LessonLoadFailure, val reason: String) : IndexLoad {
            fun describe(): String = "$failure ($reason)"
        }
    }

    private companion object {
        const val TAG = "Lessons"

        /** A contract with data on users' devices: renaming it shows Loading once more after the update. */
        val SAVED_VERSION_KEY = stringPreferencesKey("lessons_index_version")

        fun AssetFailureReason.toLoadFailure(): LessonLoadFailure = when (this) {
            AssetFailureReason.NETWORK -> LessonLoadFailure.NETWORK
            AssetFailureReason.NOT_FOUND -> LessonLoadFailure.NOT_FOUND
            AssetFailureReason.OTHER -> LessonLoadFailure.OTHER
        }
    }
}
