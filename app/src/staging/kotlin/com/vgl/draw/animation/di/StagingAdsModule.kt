package com.vgl.draw.animation.di

import com.vgl.draw.animation.BuildConfig
import com.vgl.draw.animation.core.ads.AdUnitIds
import com.vgl.draw.animation.core.ads.AdsDebugOptions
import com.vgl.draw.animation.core.ads.ConsentDebugOptions
import com.vgl.draw.animation.core.ads.ConsentShortcutConfig
import com.vgl.draw.animation.core.ads.GoogleTestAdUnitIds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Staging shows Google's test ads only (ADR-0007) and treats every device as being in the EEA,
 * so the team can test the consent form from anywhere (ADR-0045). A developer's build may also treat their own phones
 * as test devices and logs the networks of every load (plan T-058).
 */
@Module
@InstallIn(SingletonComponent::class)
object StagingAdsModule {
    @Provides
    fun adUnitIds(): AdUnitIds = GoogleTestAdUnitIds

    @Provides
    fun consentDebugOptions(): ConsentDebugOptions = ConsentDebugOptions.EEA_TEST_DEVICE

    /**
     * The build half of the ERROR_ALLOWED switch is on, so testers can try ERROR_ALLOWED by switching
     * is_enable_consent_shortcut on in the staging Firebase project; the remote key stays off by default (S-12).
     * CACHED_EARLY needs no switch (ADR-0045).
     */
    @Provides
    fun consentShortcutConfig(): ConsentShortcutConfig = ConsentShortcutConfig(enabledInBuild = true)

    /**
     * The developer's own phones as AdMob test devices (key ads.testDeviceIds of local.properties, empty on CI) and
     * one log line per network of each load (plan T-058, KB-28).
     */
    @Provides
    fun adsDebugOptions(): AdsDebugOptions = AdsDebugOptions.staging(BuildConfig.ADS_TEST_DEVICE_IDS)
}
