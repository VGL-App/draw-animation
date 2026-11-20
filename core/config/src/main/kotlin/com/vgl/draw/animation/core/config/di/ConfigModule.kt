package com.vgl.draw.animation.core.config.di

import com.vgl.draw.animation.core.config.DefaultFeatureFlags
import com.vgl.draw.animation.core.config.DefaultRemoteSettings
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.config.FeatureFlags
import com.vgl.draw.animation.core.config.FirebaseRemoteConfigRefresher
import com.vgl.draw.animation.core.config.FirebaseRemoteFlagSource
import com.vgl.draw.animation.core.config.LocalRemoteValue
import com.vgl.draw.animation.core.config.LocalRemoteValues
import com.vgl.draw.animation.core.config.LocallyEnabledFlags
import com.vgl.draw.animation.core.config.RemoteConfigRefresher
import com.vgl.draw.animation.core.config.RemoteFlagSource
import com.vgl.draw.animation.core.config.RemoteSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ConfigModule {
    @Binds
    abstract fun bindFeatureFlags(impl: DefaultFeatureFlags): FeatureFlags

    @Binds
    abstract fun bindRemoteSettings(impl: DefaultRemoteSettings): RemoteSettings

    @Binds
    abstract fun bindRemoteFlagSource(impl: FirebaseRemoteFlagSource): RemoteFlagSource

    @Binds
    abstract fun bindRemoteConfigRefresher(impl: FirebaseRemoteConfigRefresher): RemoteConfigRefresher

    /** Declares the set so it exists (empty) in builds that enable nothing locally, such as production. */
    @Multibinds
    @LocallyEnabledFlags
    abstract fun locallyEnabledFlags(): Set<FeatureFlag>

    /** Declares the set so it exists (empty) in builds that give no key a local value, such as production. */
    @Multibinds
    @LocalRemoteValues
    abstract fun localRemoteValues(): Set<LocalRemoteValue>
}
