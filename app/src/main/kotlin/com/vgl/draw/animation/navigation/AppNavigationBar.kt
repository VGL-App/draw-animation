package com.vgl.draw.animation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Skeleton of step 3 (tests first): replaced by the real implementation in step 4.

/** The top-level tabs of the app shell. */
enum class TopLevelTab { Projects, Lessons }

/** Tag of the bottom navigation bar, for tests and scenario files. */
const val APP_NAVIGATION_BAR_TAG = "app_navigation_bar"

@Composable
fun AppNavigationBar(selected: TopLevelTab, onSelect: (TopLevelTab) -> Unit, modifier: Modifier = Modifier) = Unit

internal fun showsNavigationBar(route: String?, lessonsEnabled: Boolean): Boolean = false
