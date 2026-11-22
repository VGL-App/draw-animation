package com.vgl.draw.animation.core.lessons.content

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vgl.draw.animation.core.assets.RemoteAssets
import com.vgl.draw.animation.core.config.RemoteSettings
import com.vgl.draw.animation.core.logging.AppLogger
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

internal class DefaultLessonContent(
    private val assets: RemoteAssets,
    private val settings: RemoteSettings,
    private val dataStore: DataStore<Preferences>,
    private val extractor: PackExtractor,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: AppLogger,
) : LessonContent {
    override fun library(): Flow<LessonLibraryState> = TODO()

    override suspend fun thumbnail(version: String, entry: LessonEntry): File? = TODO()

    override suspend fun loadPack(version: String, lessonId: String): LessonPackResult = TODO()
}
