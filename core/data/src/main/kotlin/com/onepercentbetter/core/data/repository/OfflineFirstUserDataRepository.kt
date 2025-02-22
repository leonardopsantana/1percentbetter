

package com.onepercentbetter.core.data.repository

import androidx.annotation.VisibleForTesting
import com.onepercentbetter.core.analytics.AnalyticsHelper
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.model.data.DarkThemeConfig
import com.onepercentbetter.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val opbPreferencesDataSource: OPBPreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        opbPreferencesDataSource.userData

    @VisibleForTesting
    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) =
        opbPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {
        opbPreferencesDataSource.setTopicIdFollowed(followedTopicId, followed)
        analyticsHelper.logTopicFollowToggled(followedTopicId, followed)
    }

    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        opbPreferencesDataSource.setNewsResourceBookmarked(newsResourceId, bookmarked)
        analyticsHelper.logNewsResourceBookmarkToggled(
            newsResourceId = newsResourceId,
            isBookmarked = bookmarked,
        )
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) =
        opbPreferencesDataSource.setNewsResourceViewed(newsResourceId, viewed)

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        opbPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
    }
}
