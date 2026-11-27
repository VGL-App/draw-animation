package com.vgl.draw.animation.feature.projects.di

import com.vgl.draw.animation.feature.projects.data.DefaultNewProjectRepository
import com.vgl.draw.animation.feature.projects.data.DefaultProjectsRepository
import com.vgl.draw.animation.feature.projects.data.NewProjectRepository
import com.vgl.draw.animation.feature.projects.data.ProjectsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** The dispatcher the project list and the Create screen read, decode and prepare photos on; tests pass their own. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ProjectsIoDispatcher

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProjectsModule {
    @Binds
    abstract fun bindProjectsRepository(impl: DefaultProjectsRepository): ProjectsRepository

    @Binds
    abstract fun bindNewProjectRepository(impl: DefaultNewProjectRepository): NewProjectRepository

    companion object {
        @Provides
        @ProjectsIoDispatcher
        fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
