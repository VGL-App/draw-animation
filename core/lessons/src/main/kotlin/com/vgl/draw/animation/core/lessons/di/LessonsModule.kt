package com.vgl.draw.animation.core.lessons.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vgl.draw.animation.core.assets.RemoteAssets
import com.vgl.draw.animation.core.config.RemoteSettings
import com.vgl.draw.animation.core.lessons.content.DefaultLessonContent
import com.vgl.draw.animation.core.lessons.content.LessonContent
import com.vgl.draw.animation.core.lessons.content.PackExtractor
import com.vgl.draw.animation.core.lessons.progress.DataStoreLessonProgressStore
import com.vgl.draw.animation.core.lessons.progress.LessonProgressStore
import com.vgl.draw.animation.core.logging.AppLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal object LessonsModule {
    @Provides
    @Singleton
    fun lessonContent(
        @ApplicationContext context: Context,
        assets: RemoteAssets,
        settings: RemoteSettings,
        dataStore: DataStore<Preferences>,
        logger: AppLogger,
    ): LessonContent = DefaultLessonContent(
        assets = assets,
        settings = settings,
        dataStore = dataStore,
        // Like :core:assets: not the cache dir (the system may clear it), not backed up (packs can be downloaded
        // again).
        extractor = PackExtractor(File(context.noBackupFilesDir, "lessons")),
        ioDispatcher = Dispatchers.IO,
        logger = logger,
    )

    @Provides
    @Singleton
    fun lessonProgressStore(dataStore: DataStore<Preferences>, logger: AppLogger): LessonProgressStore =
        DataStoreLessonProgressStore(dataStore, logger)
}
