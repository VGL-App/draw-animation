package com.vgl.draw.animation.di

import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.config.LocallyEnabledFlags
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Flags turned on in staging builds only, so the team can test features before they are approved.
 * Production builds have no such module: every flag there keeps its default (off) until remote config turns it on.
 * The lessons flag is turned on once sample lesson content is available (T-054, lessons spec section 8).
 */
@Module
@InstallIn(SingletonComponent::class)
object StagingFeatureFlagsModule {
    @Provides
    @IntoSet
    @LocallyEnabledFlags
    fun sample(): FeatureFlag = FeatureFlag.SAMPLE

    @Provides
    @IntoSet
    @LocallyEnabledFlags
    fun loading(): FeatureFlag = FeatureFlag.LOADING

    @Provides
    @IntoSet
    @LocallyEnabledFlags
    fun onboarding(): FeatureFlag = FeatureFlag.ONBOARDING

    @Provides
    @IntoSet
    @LocallyEnabledFlags
    fun projects(): FeatureFlag = FeatureFlag.PROJECTS

    @Provides
    @IntoSet
    @LocallyEnabledFlags
    fun settings(): FeatureFlag = FeatureFlag.SETTINGS

    @Provides
    @IntoSet
    @LocallyEnabledFlags
    fun editor(): FeatureFlag = FeatureFlag.EDITOR

    @Provides
    @IntoSet
    @LocallyEnabledFlags
    fun export(): FeatureFlag = FeatureFlag.EXPORT
}
