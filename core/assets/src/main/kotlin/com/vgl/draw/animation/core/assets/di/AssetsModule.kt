package com.vgl.draw.animation.core.assets.di

import android.content.Context
import com.vgl.draw.animation.core.assets.AssetsConfig
import com.vgl.draw.animation.core.assets.DefaultRemoteAssets
import com.vgl.draw.animation.core.assets.HttpAssetDownloader
import com.vgl.draw.animation.core.assets.RemoteAssets
import com.vgl.draw.animation.core.assets.downloadFailureLogger
import com.vgl.draw.animation.core.logging.AppLogger
import com.vgl.draw.animation.core.network.NetworkFailureReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

/** :app provides [AssetsConfig] for its build flavor. */
@Module
@InstallIn(SingletonComponent::class)
internal object AssetsModule {
    @Provides
    @Singleton
    fun remoteAssets(
        @ApplicationContext context: Context,
        config: AssetsConfig,
        logger: AppLogger,
        networkFailures: NetworkFailureReporter,
    ): RemoteAssets = DefaultRemoteAssets(
        config = config,
        // Not the cache dir: the system may clear that, and files must stay available offline.
        // Not backed up: they can always be downloaded again.
        cacheDir = File(context.noBackupFilesDir, "r2-assets"),
        downloader = HttpAssetDownloader(),
        ioDispatcher = Dispatchers.IO,
        onDownloadFailed = downloadFailureLogger(logger),
        networkFailures = networkFailures,
    )
}
