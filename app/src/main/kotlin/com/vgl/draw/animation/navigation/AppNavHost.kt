package com.vgl.draw.animation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vgl.draw.animation.R
import com.vgl.draw.animation.feature.loading.navigation.LoadingDestination
import com.vgl.draw.animation.feature.loading.navigation.loadingScreen
import com.vgl.draw.animation.feature.projects.navigation.ProjectsDestination
import com.vgl.draw.animation.feature.projects.navigation.projectsScreen
import com.vgl.draw.animation.feature.sample.navigation.SampleDestination
import com.vgl.draw.animation.feature.sample.navigation.sampleScreen
import com.vgl.draw.animation.network.reportedScreenOf
import com.vgl.draw.animation.network.reportsScreen
import kotlinx.coroutines.launch

/**
 * Wires every feature's destinations together. Features never navigate to each other directly.
 * [onScreenChanged] receives the current screen for the network dialog: `null` on the loading screen (spec network,
 * KB-1), also after the system recreates the activity and restores the back stack. Nothing is reported while the
 * back stack has no entry yet, so a recreation never looks like a passage through loading ([reportsScreen]).
 *
 * Loading leads to the project list; with the projects flag off, the list hands over to the sample screen once
 * (spec projects, KB-13). The bottom bar shows on the list when [lessonsEnabled]; it takes the system navigation bar
 * inset itself, so the content consumes the bar's padding before its own safe drawing padding.
 * [screens] is replaced by tests with fake destinations; the default is the real feature screens.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    lessonsEnabled: Boolean = false,
    onScreenChanged: (String?) -> Unit = {},
    screens: AppScreens = AppScreens(),
) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val currentOnScreenChanged by rememberUpdatedState(onScreenChanged)
    LaunchedEffect(route) {
        if (reportsScreen(route)) currentOnScreenChanged(reportedScreenOf(route))
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val comingSoon = stringResource(R.string.app_coming_soon)
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showsNavigationBar(route, lessonsEnabled)) {
                AppNavigationBar(
                    selected = TopLevelTab.Projects,
                    onSelect = { tab ->
                        // No Lessons destination yet (T-050).
                        if (tab == TopLevelTab.Lessons) {
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(comingSoon)
                            }
                        }
                    },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoadingDestination,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).safeDrawingPadding(),
        ) {
            screens.loading(this) {
                // Loading leaves the back stack: Back on the start screen closes the app (spec loading, KB-1).
                navController.navigate(ProjectsDestination) {
                    popUpTo<LoadingDestination> { inclusive = true }
                }
            }
            screens.projects(this) {
                // Called once from the list's LaunchedEffect, on the main thread. The sample screen does not watch
                // the flag: the user stays there until the next start (plan T-009, question 7).
                navController.navigate(SampleDestination) {
                    popUpTo<ProjectsDestination> { inclusive = true }
                }
            }
            screens.sample(this)
        }
    }
}

/** The destinations [AppNavHost] wires. Tests pass fakes, because the real routes need Hilt ViewModels. */
data class AppScreens(
    val loading: NavGraphBuilder.(onFinished: () -> Unit) -> Unit = { loadingScreen(it) },
    val projects: NavGraphBuilder.(onFeatureDisabled: () -> Unit) -> Unit = {
        // Destinations not built yet (Create T-011, editor T-018, lessons T-050, settings T-016, restore T-013):
        // null callbacks show "Coming soon".
        projectsScreen(
            onNewProject = null,
            onOpenProject = null,
            onOpenLessons = null,
            onOpenSettings = null,
            onImport = null,
            onFeatureDisabled = it,
        )
    },
    val sample: NavGraphBuilder.() -> Unit = { sampleScreen() },
)
