package com.vgl.draw.animation.core.project.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.repository.CreatedProjectsCounter
import com.vgl.draw.animation.core.project.repository.DataStoreCreatedProjectsCounter
import com.vgl.draw.animation.core.project.repository.DefaultProjectRepository
import com.vgl.draw.animation.core.project.repository.ProjectRepository
import com.vgl.draw.animation.core.project.storage.ProjectStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ProjectRepositoryModule {
    @Provides
    @Singleton
    fun createdProjectsCounter(dataStore: DataStore<Preferences>, logger: AppLogger): CreatedProjectsCounter =
        DataStoreCreatedProjectsCounter(dataStore, logger)

    @Provides
    @Singleton
    fun projectRepository(store: ProjectStore, counter: CreatedProjectsCounter, logger: AppLogger): ProjectRepository =
        DefaultProjectRepository(store, counter, System::currentTimeMillis, logger)
}
