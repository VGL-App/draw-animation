package com.vgl.draw.animation.core.data.settings

import kotlinx.coroutines.flow.Flow

/**
 * Settings kept on this device and read by several features: the defaults of a new project (frame rate and aspect
 * ratio), the default stylus pressure for brushes not tuned on their own, and the "first stroke drawn" mark.
 *
 * Values are raw (Int, String, Boolean) so `:core:data` does not depend on `:core:project`; a feature converts them to
 * the project types where it uses them. Values stay until the app data is cleared; there is no way to delete them, so
 * turning the Settings feature flag off never loses what the user chose.
 *
 * Reads never fail: on a read error each flow emits its default (the mark emits `false`) and the read is retried.
 *
 * The drawing screen reads [firstStrokeDrawn] and calls [markFirstStrokeDrawn] only while it is still `false`, not on
 * every stroke, and at most once each time the drawing screen is opened. When the call returns `false`, it is not
 * called again until the drawing screen is opened again (if the file can never be read, the flow stays `false` and
 * every call fails and logs).
 */
interface DeviceSettingsRepository {
    /** One of [DeviceSettingsValues.FRAME_RATES]; [DeviceSettingsValues.DEFAULT_FRAME_RATE] when nothing valid is stored. */
    val defaultFrameRate: Flow<Int>

    /** One of [DeviceSettingsValues.ASPECT_RATIOS]; [DeviceSettingsValues.DEFAULT_ASPECT_RATIO] when nothing valid is stored. */
    val defaultAspectRatio: Flow<String>

    /** Default stylus pressure for brushes not tuned on their own; [DeviceSettingsValues.DEFAULT_STYLUS_PRESSURE] when nothing is stored. */
    val stylusPressure: Flow<Boolean>

    /** True once the user has drawn a first stroke in any project or lesson on this device. Only goes from false to true. */
    val firstStrokeDrawn: Flow<Boolean>

    /** Throws IllegalArgumentException for a value outside FRAME_RATES, IOException when the write fails. */
    suspend fun setDefaultFrameRate(frameRate: Int)

    /** Throws IllegalArgumentException for a value outside ASPECT_RATIOS, IOException when the write fails. */
    suspend fun setDefaultAspectRatio(aspectRatio: String)

    /** Throws IOException when the write fails. */
    suspend fun setStylusPressure(enabled: Boolean)

    /** Stores the mark once; true when it is stored (now or before), false when the write failed. Never throws IOException. */
    suspend fun markFirstStrokeDrawn(): Boolean
}

/** Allowed values and defaults of [DeviceSettingsRepository], in the order shown by the Settings pickers. */
object DeviceSettingsValues {
    val FRAME_RATES: List<Int> = listOf(6, 8, 12, 15, 24, 30)
    const val ASPECT_RATIO_16_9 = "16:9"
    const val ASPECT_RATIO_9_16 = "9:16"
    const val ASPECT_RATIO_1_1 = "1:1"
    val ASPECT_RATIOS: List<String> = listOf(ASPECT_RATIO_16_9, ASPECT_RATIO_9_16, ASPECT_RATIO_1_1)
    const val DEFAULT_FRAME_RATE = 12
    const val DEFAULT_ASPECT_RATIO = ASPECT_RATIO_9_16
    const val DEFAULT_STYLUS_PRESSURE = true
}
