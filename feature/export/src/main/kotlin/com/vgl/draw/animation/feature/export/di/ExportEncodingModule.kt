package com.vgl.draw.animation.feature.export.di

import android.content.Context
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.project.render.FrameRenderer
import com.vgl.draw.animation.feature.export.encode.DefaultGifPngExporter
import com.vgl.draw.animation.feature.export.encode.GifPngExporter
import com.vgl.draw.animation.feature.export.encode.WatermarkPainter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal object ExportEncodingModule {
    @Provides
    @Singleton
    fun watermarkPainter(@ApplicationContext context: Context): WatermarkPainter = WatermarkPainter.create(context)

    @Provides
    fun gifPngExporter(renderer: FrameRenderer, watermark: WatermarkPainter, logger: AppLogger): GifPngExporter =
        // Encoding uses the CPU; the renderer reads files on its own dispatcher.
        DefaultGifPngExporter(renderer, watermark, Dispatchers.Default, logger)
}
