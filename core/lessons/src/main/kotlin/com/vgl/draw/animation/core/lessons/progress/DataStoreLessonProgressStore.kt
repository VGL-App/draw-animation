package com.vgl.draw.animation.core.lessons.progress

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vgl.draw.animation.core.lessons.content.LessonIndexFormat
import com.vgl.draw.animation.core.lessons.content.isContentId
import com.vgl.draw.animation.core.lessons.isContentVersion
import com.vgl.draw.animation.core.logging.AppLogger
import java.io.IOException
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * [LessonProgressStore] on the shared `app_preferences` DataStore: one key holding versioned JSON, so every change is
 * one `edit` transaction (plan T-048, section 3.1). The key name is a contract with data on users' devices.
 */
internal class DataStoreLessonProgressStore(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger,
) : LessonProgressStore {
    override val progress: Flow<LessonProgress> = dataStore.data
        .retryWhen { cause, attempt ->
            // A read error must never crash or freeze a screen: emit empty data, wait, then read again.
            if (cause !is IOException) return@retryWhen false
            logger.warn(TAG, "Lesson progress not read, retry ${attempt + 1}", cause)
            emit(emptyPreferences())
            delay(retryDelayMillis(attempt))
            true
        }
        .map { it[PROGRESS_KEY] }
        // Before decoding: a broken value is reported once, not on every change of another key of the file.
        .distinctUntilChanged()
        .map { raw ->
            when (val decoded = decode(raw)) {
                is Decoded.Ok -> decoded.progress
                is Decoded.Unreadable -> {
                    logger.warn(TAG, "Lesson progress unreadable: ${decoded.reason}")
                    LessonProgress()
                }
            }
        }

    override suspend fun start(lessonId: String, attempt: LessonAttempt) {
        require(isContentId(lessonId)) { "Invalid lesson id" }
        val error = attemptError(attempt)
        require(error == null) { "Invalid lesson attempt: $error" }
        update { it.copy(inProgress = it.inProgress + (lessonId to attempt)) }
    }

    override suspend fun setStep(lessonId: String, step: Int): Boolean {
        require(step >= 0) { "Step must not be negative" }
        var found = false
        update { current ->
            val attempt = current.inProgress[lessonId] ?: return@update current
            require(step < attempt.stepCount) { "Step $step is past the last step" }
            found = true
            current.copy(inProgress = current.inProgress + (lessonId to attempt.copy(step = step)))
        }
        return found
    }

    override suspend fun complete(lessonId: String, nowMillis: Long): Long? {
        var duration: Long? = null
        update { current ->
            val attempt = current.inProgress[lessonId] ?: return@update current
            duration = max(0L, nowMillis - attempt.startedAtMillis)
            LessonProgress(
                completed = current.completed + (lessonId to CompletedLesson(attempt.projectId)),
                inProgress = current.inProgress - lessonId,
            )
        }
        return duration
    }

    /** Applies [change] in one transaction; nothing is written when it returns the same progress. */
    private suspend fun update(change: (LessonProgress) -> LessonProgress) {
        dataStore.edit { prefs ->
            // A broken stored value was reported by the flow; it is overwritten from empty progress.
            val current = (decode(prefs[PROGRESS_KEY]) as? Decoded.Ok)?.progress ?: LessonProgress()
            val changed = change(current)
            if (changed != current) prefs[PROGRESS_KEY] = encode(changed)
        }
    }

    private sealed interface Decoded {
        data class Ok(val progress: LessonProgress) : Decoded

        /** [reason] never holds stored values (project ids). */
        data class Unreadable(val reason: String) : Decoded
    }

    private companion object {
        const val TAG = "Lessons"
        const val FORMAT_VERSION = 1
        const val MAX_RETRY_DELAY_MILLIS = 30_000L

        /** A contract with data on users' devices: renaming it loses the progress. */
        val PROGRESS_KEY = stringPreferencesKey("lessons_progress")

        /** Strict: an unknown field means a format this app does not know. */
        val json = Json { ignoreUnknownKeys = false }

        fun retryDelayMillis(attempt: Long): Long =
            min(1_000L shl attempt.coerceAtMost(5).toInt(), MAX_RETRY_DELAY_MILLIS)

        fun decode(raw: String?): Decoded {
            if (raw == null) return Decoded.Ok(LessonProgress())
            val stored = try {
                json.decodeFromString<ProgressJson>(raw)
            } catch (e: IllegalArgumentException) {
                // SerializationException is an IllegalArgumentException. Its message quotes the value: keep it out.
                return Decoded.Unreadable("malformed JSON or unknown field")
            }
            if (stored.formatVersion != FORMAT_VERSION) {
                return Decoded.Unreadable("format ${stored.formatVersion}")
            }
            val progress = LessonProgress(
                completed = stored.completed.mapValues { CompletedLesson(it.value.projectId) },
                inProgress = stored.inProgress.mapValues { (_, it) ->
                    LessonAttempt(it.contentVersion, it.step, it.stepCount, it.projectId, it.startedAtMillis)
                },
            )
            val ids = progress.completed.keys + progress.inProgress.keys
            if (ids.any { !isContentId(it) }) return Decoded.Unreadable("invalid lesson id")
            if (progress.completed.values.any { !isCanonicalUuid(it.projectId) }) {
                return Decoded.Unreadable("invalid project id of a completed lesson")
            }
            val attemptError = progress.inProgress.values.firstNotNullOfOrNull { attemptError(it) }
            if (attemptError != null) return Decoded.Unreadable(attemptError)
            return Decoded.Ok(progress)
        }

        /** Same input, same text: maps sorted by lesson id. */
        fun encode(progress: LessonProgress): String = json.encodeToString(
            ProgressJson.serializer(),
            ProgressJson(
                formatVersion = FORMAT_VERSION,
                completed = progress.completed.toSortedMap().mapValues { CompletedJson(it.value.projectId) },
                inProgress = progress.inProgress.toSortedMap().mapValues { (_, it) ->
                    AttemptJson(it.contentVersion, it.step, it.stepCount, it.projectId, it.startedAtMillis)
                },
            ),
        )

        /** The first broken rule of [attempt], or null; never quotes its values. */
        fun attemptError(attempt: LessonAttempt): String? = when {
            !isContentVersion(attempt.contentVersion) -> "invalid content version"
            attempt.stepCount !in 1..LessonIndexFormat.MAX_STEPS -> "step count out of range"
            attempt.step !in 0 until attempt.stepCount -> "step out of range"
            !isCanonicalUuid(attempt.projectId) -> "invalid project id"
            attempt.startedAtMillis < 0 -> "negative start time"
            else -> null
        }

        fun isCanonicalUuid(value: String): Boolean = try {
            UUID.fromString(value).toString() == value
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}

@Serializable
internal data class ProgressJson(
    val formatVersion: Int,
    val completed: Map<String, CompletedJson>,
    val inProgress: Map<String, AttemptJson>,
)

@Serializable
internal data class CompletedJson(val projectId: String)

@Serializable
internal data class AttemptJson(
    val contentVersion: String,
    val step: Int,
    val stepCount: Int,
    val projectId: String,
    val startedAtMillis: Long,
)
