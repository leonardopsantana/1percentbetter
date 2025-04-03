

package com.onepercentbetter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.onepercentbetter.feature.goals.navigation.goalsScreen
import com.onepercentbetter.feature.routine.navigation.RoutineBaseRoute
import com.onepercentbetter.feature.routine.navigation.routineScreen
import com.onepercentbetter.ui.OPBAppState

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun OPBNavHost(
    appState: OPBAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = RoutineBaseRoute,
        modifier = modifier,
    ) {
        routineScreen(
            onTopicClick = { },
        ) {
        }
        goalsScreen(
            showBackButton = true,
            onBackClick = navController::popBackStack,
            onTopicClick = {},
//                onTopicClick = navController::navigateToGoals,
        )
    }
}
