package com.vgl.draw.animation.core.lessons.progress

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vgl.draw.animation.core.logging.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

internal class DataStoreLessonProgressStore(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger,
) : LessonProgressStore {
    override val progress: Flow<LessonProgress> get() = TODO()

    override suspend fun start(lessonId: String, attempt: LessonAttempt): Unit = TODO()

    override suspend fun setStep(lessonId: String, step: Int): Boolean = TODO()

    override suspend fun complete(lessonId: String, nowMillis: Long): Long? = TODO()
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
