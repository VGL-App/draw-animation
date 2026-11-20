package com.vgl.draw.animation.core.network.di

import android.content.Context
import android.os.SystemClock
import com.vgl.draw.animation.core.network.AndroidConnectivitySource
import com.vgl.draw.animation.core.network.ConnectivitySource
import com.vgl.draw.animation.core.network.DefaultInternetConnectivity
import com.vgl.draw.animation.core.network.InternetConnectivity
import com.vgl.draw.animation.core.network.NetworkClock
import com.vgl.draw.animation.core.network.NetworkDialogController
import com.vgl.draw.animation.core.network.NetworkFailureReporter
import com.vgl.draw.animation.core.network.NetworkStatus
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** The scope in which the network dialog controller runs, for the whole life of the app. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class NetworkScope

/** One controller serves every module: :app draws its state, other modules read or report through interfaces. */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkBindingsModule {
    @Binds
    abstract fun networkStatus(controller: NetworkDialogController): NetworkStatus

    @Binds
    abstract fun networkFailureReporter(controller: NetworkDialogController): NetworkFailureReporter

    @Binds
    abstract fun internetConnectivity(connectivity: DefaultInternetConnectivity): InternetConnectivity
}

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun connectivitySource(@ApplicationContext context: Context): ConnectivitySource =
        AndroidConnectivitySource(context)

    @Provides
    @Singleton
    @NetworkScope
    fun networkScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    fun networkClock(): NetworkClock = NetworkClock { SystemClock.elapsedRealtime() }
}
