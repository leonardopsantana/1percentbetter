

package com.onepercentbetter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.onepercentbetter.feature.goals.navigation.goalsScreen
import com.onepercentbetter.feature.routine.navigation.RoutineBaseRoute
import com.onepercentbetter.feature.routine.navigation.routineSection
import com.onepercentbetter.ui.OPBAppState
import com.onepercentbetter.ui.interests2pane.interestsListDetailScreen

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
        routineSection(
            onTopicClick = { },
        ) {
        }
        goalsScreen(
            showBackButton = true,
            onBackClick = navController::popBackStack,
            onTopicClick = {},
//                onTopicClick = navController::navigateToGoals,
        )
        interestsListDetailScreen()
    }
}
