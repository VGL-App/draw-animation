package com.vgl.draw.animation.core.project.di

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.render.BackgroundPalette
import com.vgl.draw.animation.core.project.render.DefaultProjectThumbnails
import com.vgl.draw.animation.core.project.render.FrameRenderer
import com.vgl.draw.animation.core.project.render.ProjectThumbnails
import com.vgl.draw.animation.core.project.render.StoreFrameRenderer
import com.vgl.draw.animation.core.project.storage.ProjectStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal object RenderModule {
    @Provides
    @Singleton
    fun frameRenderer(store: ProjectStore, logger: AppLogger): FrameRenderer = StoreFrameRenderer(
        store = store,
        palette = BackgroundPalette.Default,
        // Drawing uses the CPU; reading files already runs on the store's dispatcher.
        renderDispatcher = Dispatchers.Default,
        logger = logger,
    )

    @Provides
    @Singleton
    fun projectThumbnails(store: ProjectStore, renderer: FrameRenderer, logger: AppLogger): ProjectThumbnails =
        DefaultProjectThumbnails(store, renderer, logger)
}
