package com.onepercentbetter.feature.routine.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.onepercentbetter.feature.routine.RoutineScreen
import kotlinx.serialization.Serializable

@Serializable
data object RoutineRoute // route to routine screen

@Serializable
data object RoutineBaseRoute // route to base navigation graph

fun NavController.navigateRoutine(navOptions: NavOptions) =
    navigate(route = RoutineRoute, navOptions)

/**
 *  The Routine section of the app. It can also display information about topics.
 *  This should be supplied from a separate module.
 *
 *  @param onTopicClick - Called when a topic is clicked, contains the ID of the topic
 *  @param topicDestination - Destination for topic content
 */
fun NavGraphBuilder.routineSection(
    onTopicClick: (String) -> Unit,
    topicDestination: NavGraphBuilder.() -> Unit,
) {
    navigation<RoutineBaseRoute>(startDestination = RoutineRoute) {
        composable<RoutineRoute> {
            RoutineScreen(onTopicClick)
        }
        topicDestination()
    }
}
