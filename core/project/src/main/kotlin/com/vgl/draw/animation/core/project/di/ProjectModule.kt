package com.vgl.draw.animation.core.project.di

import android.content.Context
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.storage.FileProjectStore
import com.vgl.draw.animation.core.project.storage.ProjectStore
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
internal object ProjectModule {
    @Provides
    @Singleton
    fun projectStore(@ApplicationContext context: Context, logger: AppLogger): ProjectStore = FileProjectStore(
        // filesDir, not the cache dir: projects must stay until the user deletes them.
        root = File(context.filesDir, "projects"),
        ioDispatcher = Dispatchers.IO,
        logger = logger,
    )
}
