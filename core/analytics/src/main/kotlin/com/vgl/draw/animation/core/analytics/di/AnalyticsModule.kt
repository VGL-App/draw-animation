package com.vgl.draw.animation.core.analytics.di

import com.vgl.draw.animation.core.analytics.AdFunnel
import com.vgl.draw.animation.core.analytics.AdMetrics
import com.vgl.draw.animation.core.analytics.Analytics
import com.vgl.draw.animation.core.analytics.AndroidCountrySource
import com.vgl.draw.animation.core.analytics.AppsFlyerLibSink
import com.vgl.draw.animation.core.analytics.AppsFlyerSink
import com.vgl.draw.animation.core.analytics.AppsFlyerStartup
import com.vgl.draw.animation.core.analytics.AppsFlyerTracker
import com.vgl.draw.animation.core.analytics.CountrySource
import com.vgl.draw.animation.core.analytics.FanOutAdMetrics
import com.vgl.draw.animation.core.analytics.FirebaseAdMetrics
import com.vgl.draw.animation.core.analytics.FirebaseAnalyticsLogger
import com.vgl.draw.animation.core.logging.AppLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** The analytics of the app; [com.vgl.draw.animation.core.analytics.AppsFlyerConfig] comes from :app (ADR-0027). */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {
    @Binds
    abstract fun bindAnalytics(impl: FirebaseAnalyticsLogger): Analytics

    @Binds
    abstract fun bindAdFunnel(impl: AppsFlyerTracker): AdFunnel

    @Binds
    abstract fun bindAppsFlyerStartup(impl: AppsFlyerTracker): AppsFlyerStartup

    @Binds
    abstract fun bindAppsFlyerSink(impl: AppsFlyerLibSink): AppsFlyerSink

    @Binds
    abstract fun bindCountrySource(impl: AndroidCountrySource): CountrySource
}

/** Every ad metric goes to Firebase, then to AppsFlyer (section 8.3); a failing one never stops the other. */
@Module
@InstallIn(SingletonComponent::class)
internal object AdMetricsModule {
    @Provides
    fun adMetrics(firebase: FirebaseAdMetrics, appsFlyer: AppsFlyerTracker, logger: AppLogger): AdMetrics =
        FanOutAdMetrics(listOf(firebase, appsFlyer), logger)
}
