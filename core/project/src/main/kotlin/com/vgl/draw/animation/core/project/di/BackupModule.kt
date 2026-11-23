package com.vgl.draw.animation.core.project.di

import android.content.Context
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.backup.ContentResolverBackupStreams
import com.vgl.draw.animation.core.project.backup.DefaultProjectBackup
import com.vgl.draw.animation.core.project.backup.ProjectBackup
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.storage.ProjectStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal object BackupModule {
    @Provides
    @Singleton
    fun projectBackup(
        @ApplicationContext context: Context,
        store: ProjectStore,
        repository: ProjectRepository,
        logger: AppLogger,
    ): ProjectBackup = DefaultProjectBackup(
        store = store,
        repository = repository,
        streams = ContentResolverBackupStreams(context.contentResolver),
        clock = System::currentTimeMillis,
        ioDispatcher = Dispatchers.IO,
        logger = logger,
    )
}
