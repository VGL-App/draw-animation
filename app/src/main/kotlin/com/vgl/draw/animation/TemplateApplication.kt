package com.vgl.draw.animation

import android.app.Application
import com.vgl.draw.animation.core.ads.AdsController
import com.vgl.draw.animation.core.ads.AppActivityFilter
import com.vgl.draw.animation.core.analytics.AppsFlyerStartup
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TemplateApplication : Application() {
    @Inject
    lateinit var ads: AdsController

    @Inject
    lateinit var appsFlyer: AppsFlyerStartup

    override fun onCreate() {
        super.onCreate()
        // AppsFlyer is initialized here and sends nothing yet; :core:ads starts it once consent allows (ADR-0027).
        appsFlyer.prepare()
        // :core:ads starts once, before any screen (HD-01). Only activities in the package of MainActivity count as the
        // app's; the SDK's own activities are ignored.
        ads.start(this, AppActivityFilter.inPackageOf(MainActivity::class.java))
    }
}
