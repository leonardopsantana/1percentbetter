

package com.onepercentbetter.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.onepercentbetter.R
import com.onepercentbetter.core.designsystem.icon.OPBIcons
import com.onepercentbetter.feature.bookmarks.navigation.BookmarksRoute
import com.onepercentbetter.feature.interests.navigation.InterestsRoute
import com.onepercentbetter.feature.routine.navigation.RoutineBaseRoute
import com.onepercentbetter.feature.routine.navigation.RoutineRoute
import kotlin.reflect.KClass
import com.onepercentbetter.feature.bookmarks.R as bookmarksR
import com.onepercentbetter.feature.interests.R as interestS
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
        selectedIcon = OPBIcons.Book,
        unselectedIcon = OPBIcons.BookBorder,
        iconTextId = rouTine.string.feature_routine_title,
        titleTextId = R.string.app_name,
        route = RoutineRoute::class,
        baseRoute = RoutineBaseRoute::class,
    ),
    BOOKMARKS(
        selectedIcon = OPBIcons.Bookmarks,
        unselectedIcon = OPBIcons.BookmarksBorder,
        iconTextId = bookmarksR.string.feature_bookmarks_title,
        titleTextId = bookmarksR.string.feature_bookmarks_title,
        route = BookmarksRoute::class,
    ),
    INTERESTS(
        selectedIcon = OPBIcons.Grid3x3,
        unselectedIcon = OPBIcons.Grid3x3,
        iconTextId = interestS.string.feature_interests_title,
        titleTextId = interestS.string.feature_interests_title,
        route = InterestsRoute::class,
    ),
}
