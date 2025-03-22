package com.onepercentbetter.feature.goals.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.onepercentbetter.feature.goals.GoalsScreen
import kotlinx.serialization.Serializable

@Serializable object GoalsRoute

fun NavController.navigateToGoals(navOptions: NavOptions) =
    navigate(route = GoalsRoute, navOptions)

fun NavGraphBuilder.goalsScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
) {
    composable<GoalsRoute> {
        GoalsScreen(
            showBackButton = showBackButton,
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
        )
    }
}
