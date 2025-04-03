

package com.onepercentbetter.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.onepercentbetter.core.designsystem.icon.OPBIcons
import com.onepercentbetter.feature.goals.navigation.GoalsRoute
import com.onepercentbetter.feature.routine.navigation.RoutineBaseRoute
import com.onepercentbetter.feature.routine.navigation.RoutineRoute
import kotlin.reflect.KClass
import com.onepercentbetter.feature.goals.R as goalsR
import com.onepercentbetter.feature.routine.R as rouTine

/**
 * Type for the top level destinations in the application. Contains metadata about the destination
 * that is used in the top app bar and common navigation UI.
 *
 * @param selectedIcon The icon to be displayed in the navigation UI when this destination is
 * selected.
 * @param unselectedIcon The icon to be displayed in the navigation UI when this destination is
 * not selected.
 * @param iconTextId Text that to be displayed in the navigation UI.
 * @param titleTextId Text that is displayed on the top app bar.
 * @param route The route to use when navigating to this destination.
 * @param baseRoute The highest ancestor of this destination. Defaults to [route], meaning that
 * there is a single destination in that section of the app (no nested destinations).
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    ROUTINE(
        selectedIcon = OPBIcons.AccessTime,
        unselectedIcon = OPBIcons.AccessTimeBorder,
        iconTextId = rouTine.string.feature_routine_title,
        titleTextId = com.onepercentbetter.feature.routine.R.string.feature_routine_title,
        route = RoutineRoute::class,
        baseRoute = RoutineBaseRoute::class,
    ),
    GOALS(
        selectedIcon = OPBIcons.AdsClick,
        unselectedIcon = OPBIcons.AdsClickBorder,
        iconTextId = goalsR.string.feature_goals_title,
        titleTextId = goalsR.string.feature_goals_title,
        route = GoalsRoute::class,
    )
}
