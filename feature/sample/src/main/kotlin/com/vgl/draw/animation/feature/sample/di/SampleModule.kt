package com.vgl.draw.animation.feature.sample.di

import com.vgl.draw.animation.feature.sample.data.CounterRepository
import com.vgl.draw.animation.feature.sample.data.DataStoreCounterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SampleModule {
    @Binds
    abstract fun bindCounterRepository(impl: DataStoreCounterRepository): CounterRepository
}
