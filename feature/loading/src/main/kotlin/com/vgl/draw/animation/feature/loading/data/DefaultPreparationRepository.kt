package com.vgl.draw.animation.feature.loading.data

import com.vgl.draw.animation.core.assets.AssetPath
import com.vgl.draw.animation.core.assets.AssetResult
import com.vgl.draw.animation.core.assets.RemoteAssets
import com.vgl.draw.animation.core.config.RemoteConfigRefresher
import javax.inject.Inject

internal class DefaultPreparationRepository @Inject constructor(
    private val refresher: RemoteConfigRefresher,
    private val assets: RemoteAssets,
) : PreparationRepository {
    override suspend fun run(task: PreparationTask): Boolean = when (task) {
        PreparationTask.REMOTE_CONFIG -> refresher.refresh()
        PreparationTask.HELLO_FILE -> loadFile(HELLO_PATH)
        PreparationTask.CONTENT_FILE -> loadFile(CONTENT_PATH)
        PreparationTask.GOODBYE_FILE -> loadFile(GOODBYE_PATH)
    }

    private suspend fun loadFile(path: AssetPath): Boolean = assets.load(path) is AssetResult.Ready

    private companion object {
        val HELLO_PATH = AssetPath("test/v1/hello.txt")
        val CONTENT_PATH = AssetPath("test/v1/content.txt")
        val GOODBYE_PATH = AssetPath("test/v1/goodbye.txt")
    }
}
