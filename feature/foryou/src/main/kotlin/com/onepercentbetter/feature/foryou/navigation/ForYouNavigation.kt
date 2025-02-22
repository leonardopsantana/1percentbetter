

package com.onepercentbetter.feature.foryou.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.onepercentbetter.core.notifications.DEEP_LINK_URI_PATTERN
import com.onepercentbetter.feature.foryou.ForYouScreen
import kotlinx.serialization.Serializable

@Serializable data object ForYouRoute // route to ForYou screen

@Serializable data object ForYouBaseRoute // route to base navigation graph

fun NavController.navigateToForYou(navOptions: NavOptions) = navigate(route = ForYouRoute, navOptions)

/**
 *  The ForYou section of the app. It can also display information about topics.
 *  This should be supplied from a separate module.
 *
 *  @param onTopicClick - Called when a topic is clicked, contains the ID of the topic
 *  @param topicDestination - Destination for topic content
 */
fun NavGraphBuilder.forYouSection(
    onTopicClick: (String) -> Unit,
    topicDestination: NavGraphBuilder.() -> Unit,
) {
    navigation<ForYouBaseRoute>(startDestination = ForYouRoute) {
        composable<ForYouRoute>(
            deepLinks = listOf(
                navDeepLink {
                    /**
                     * This destination has a deep link that enables a specific news resource to be
                     * opened from a notification (@see SystemTrayNotifier for more). The news resource
                     * ID is sent in the URI rather than being modelled in the route type because it's
                     * transient data (stored in SavedStateHandle) that is cleared after the user has
                     * opened the news resource.
                     */
                    uriPattern = DEEP_LINK_URI_PATTERN
                },
            ),
        ) {
            ForYouScreen(onTopicClick)
        }
        topicDestination()
    }
}
