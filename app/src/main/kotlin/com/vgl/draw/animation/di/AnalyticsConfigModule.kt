package com.vgl.draw.animation.di

import com.vgl.draw.animation.BuildConfig
import com.vgl.draw.animation.core.analytics.AppsFlyerConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** The AppsFlyer dev key of the build flavor, set in app/build.gradle.kts: none in staging (ADR-0027, decision 2). */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsConfigModule {
    @Provides
    fun appsFlyerConfig(): AppsFlyerConfig = AppsFlyerConfig(BuildConfig.APPSFLYER_DEV_KEY)
}
