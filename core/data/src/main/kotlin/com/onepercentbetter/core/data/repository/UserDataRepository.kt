

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.model.data.DarkThemeConfig
import com.onepercentbetter.core.model.data.ThemeBrand
import com.onepercentbetter.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets the user's currently followed topics
     */
    suspend fun setFollowedTopicIds(followedTopicIds: Set<String>)

    /**
     * Sets the user's newly followed/unfollowed topic
     */
    suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean)

    /**
     * Updates the bookmarked status for a news resource
     */
    suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean)

    /**
     * Updates the viewed status for a news resource
     */
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean)

    /**
     * Sets the desired theme brand.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     * Sets whether the user has completed the onboarding process.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}
