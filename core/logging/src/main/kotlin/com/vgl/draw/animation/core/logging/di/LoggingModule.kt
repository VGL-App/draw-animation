package com.vgl.draw.animation.core.logging.di

import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.logging.DefaultAppLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LoggingModule {
    @Binds
    abstract fun bindAppLogger(impl: DefaultAppLogger): AppLogger
}
