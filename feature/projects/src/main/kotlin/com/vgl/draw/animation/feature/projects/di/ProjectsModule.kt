package com.vgl.draw.animation.feature.projects.di

import com.vgl.draw.animation.feature.projects.data.DefaultProjectsRepository
import com.vgl.draw.animation.feature.projects.data.ProjectsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** The dispatcher the project list reads and decodes thumbnails on; tests pass their own. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ProjectsIoDispatcher

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProjectsModule {
    @Binds
    abstract fun bindProjectsRepository(impl: DefaultProjectsRepository): ProjectsRepository

    companion object {
        @Provides
        @ProjectsIoDispatcher
        fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
