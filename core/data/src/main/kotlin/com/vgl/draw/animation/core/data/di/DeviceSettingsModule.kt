package com.vgl.draw.animation.core.data.di

import com.vgl.draw.animation.core.data.settings.DataStoreDeviceSettingsRepository
import com.vgl.draw.animation.core.data.settings.DeviceSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DeviceSettingsModule {
    @Binds
    abstract fun bindDeviceSettingsRepository(impl: DataStoreDeviceSettingsRepository): DeviceSettingsRepository
}
