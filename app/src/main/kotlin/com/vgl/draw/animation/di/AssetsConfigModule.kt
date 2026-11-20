package com.vgl.draw.animation.di

import com.vgl.draw.animation.BuildConfig
import com.vgl.draw.animation.core.assets.AssetsConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** The R2 address of the build flavor, set in app/build.gradle.kts (ADR-0007, ADR-0015). */
@Module
@InstallIn(SingletonComponent::class)
object AssetsConfigModule {
    @Provides
    fun assetsConfig(): AssetsConfig = AssetsConfig(BuildConfig.ASSETS_BASE_URL)
}
