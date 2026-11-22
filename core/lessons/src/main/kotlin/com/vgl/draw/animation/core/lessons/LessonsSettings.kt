package com.vgl.draw.animation.core.lessons

import com.vgl.draw.animation.core.config.RemoteKey

/** Remote Config keys of the lessons module (ADR-0046), listed by :app in AppRemoteKeys. */
object LessonsSettings {
    /** R2 folder of the lesson content: `lessons/<version>/` (spec lessons, question 5; plan T-048). */
    val CONTENT_VERSION: RemoteKey<String> = RemoteKey.string(
        name = "lessons_content_version",
        default = "v1",
        description = "R2 folder of the current lesson content, for example v1 for lessons/v1/. " +
            "Every content change goes to a new folder.",
    )

    fun all(): List<RemoteKey<*>> = listOf(CONTENT_VERSION)
}

/** A content version folder: `v1` to `v999999`. */
private val CONTENT_VERSION_PATTERN = Regex("v[1-9][0-9]{0,5}")

internal fun isContentVersion(value: String): Boolean = CONTENT_VERSION_PATTERN.matches(value)

/**
 * [raw] when it is a valid version folder, else the default: a typo on Firebase ("V2", "v2/", "") must not break the
 * lessons screen. The caller logs a warning.
 */
internal fun contentVersionOrDefault(raw: String): String =
    if (isContentVersion(raw)) raw else LessonsSettings.CONTENT_VERSION.default
