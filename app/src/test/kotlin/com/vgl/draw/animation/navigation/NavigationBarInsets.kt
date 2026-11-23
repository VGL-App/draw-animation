package com.vgl.draw.animation.navigation

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/** A gesture navigation bar at the bottom: 24 dp at xxhdpi. */
internal const val TEST_NAVIGATION_BAR_PX = 72

/**
 * Robolectric gives the window no system bar insets, so a test of "insets not added twice" could never fail. This
 * sends a navigation bar inset of [bottomPx] at the bottom to the window of [rule], drawn edge to edge as
 * `MainActivity` does. Call it after `setContent` (like `PhoneInsets.kt` of testStaging, internal to that source set).
 */
internal fun dispatchNavigationBarInsets(rule: ComposeContentTestRule, bottomPx: Int) {
    val activity = (rule as AndroidComposeTestRule<*, *>).activity
    val insets = WindowInsetsCompat.Builder()
        .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, bottomPx))
        .build()
    rule.runOnUiThread {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        ViewCompat.dispatchApplyWindowInsets(activity.window.decorView, insets)
    }
    rule.waitForIdle()
}
