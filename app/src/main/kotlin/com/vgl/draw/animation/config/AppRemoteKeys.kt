package com.vgl.draw.animation.config

import com.vgl.draw.animation.core.ads.AdsSettings
import com.vgl.draw.animation.core.analytics.AppsFlyerSettings
import com.vgl.draw.animation.core.config.FeatureFlag
import com.vgl.draw.animation.core.config.RemoteKey
import com.vgl.draw.animation.core.lessons.LessonsSettings
import com.vgl.draw.animation.core.network.NetworkSettings

/**
 * Every Remote Config key of the app, by owning module (ADR-0046). A new core module with keys is added here.
 * :app is the only module that sees every core module (ADR-0001). docs/remote-config-keys.md and
 * docs/remote-config-template.json are generated from this list by RemoteConfigKeysTest.
 */
object AppRemoteKeys {
    val byModule: Map<String, List<RemoteKey<*>>> = linkedMapOf(
        ":core:config" to FeatureFlag.entries.map { it.remoteKey },
        ":core:ads" to AdsSettings.all(),
        ":core:analytics" to AppsFlyerSettings.all(),
        ":core:network" to NetworkSettings.all(),
        ":core:lessons" to LessonsSettings.all(),
    )
}
