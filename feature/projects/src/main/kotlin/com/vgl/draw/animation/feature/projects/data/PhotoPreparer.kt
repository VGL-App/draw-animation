package com.vgl.draw.animation.feature.projects.data

import android.content.Context
import com.vgl.draw.animation.core.logging.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Test-first skeleton (T-011): filled in by the code commit. */
internal class PhotoPreparer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AppLogger,
) {
    fun prepare(uri: String): PreparedPhoto? = null
}

internal data class PhotoTransform(val rotationDegrees: Int, val mirrorHorizontally: Boolean)

internal fun photoSampleSize(width: Int, height: Int): Int = 1

internal fun photoTargetSize(width: Int, height: Int): Pair<Int, Int> = width to height

internal fun exifTransform(orientation: Int): PhotoTransform = PhotoTransform(0, false)

internal fun orientationOrDefault(logger: AppLogger, read: () -> Int): Int = 0
