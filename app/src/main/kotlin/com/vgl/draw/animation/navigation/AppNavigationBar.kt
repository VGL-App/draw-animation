package com.vgl.draw.animation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vgl.draw.animation.R
import com.vgl.draw.animation.feature.projects.navigation.ProjectsDestination

/** The top-level tabs of the app shell (design projects, BottomNav). */
enum class TopLevelTab { Projects, Lessons }

/** Tag of the bottom navigation bar, for tests and scenario files. */
const val APP_NAVIGATION_BAR_TAG = "app_navigation_bar"

/**
 * The bottom navigation bar shared by the top-level screens: Projects and Lessons. It adds the system navigation bar
 * inset itself, so the content above it must not add that inset again.
 */
@Composable
fun AppNavigationBar(selected: TopLevelTab, onSelect: (TopLevelTab) -> Unit, modifier: Modifier = Modifier) {
    val colors = NavigationBarItemDefaults.colors(
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onSurface,
        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    NavigationBar(
        modifier = modifier.testTag(APP_NAVIGATION_BAR_TAG),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        TopLevelTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = { Icon(painter = painterResource(tab.icon), contentDescription = null) },
                label = { Text(text = stringResource(tab.label), style = MaterialTheme.typography.labelMedium) },
                colors = colors,
            )
        }
    }
}

private val TopLevelTab.icon: Int
    get() = when (this) {
        TopLevelTab.Projects -> R.drawable.ic_nav_projects
        TopLevelTab.Lessons -> R.drawable.ic_nav_lessons
    }

private val TopLevelTab.label: Int
    get() = when (this) {
        TopLevelTab.Projects -> R.string.app_nav_projects
        TopLevelTab.Lessons -> R.string.app_nav_lessons
    }

/**
 * Whether the bottom bar shows on [route]: only on a top-level screen, and only with the Lessons flag on. The bar has
 * two tabs; without Lessons one would be left, so the whole bar hides (spec projects, KB-1).
 */
internal fun showsNavigationBar(route: String?, lessonsEnabled: Boolean): Boolean =
    lessonsEnabled && route != null && route == ProjectsDestination::class.qualifiedName
