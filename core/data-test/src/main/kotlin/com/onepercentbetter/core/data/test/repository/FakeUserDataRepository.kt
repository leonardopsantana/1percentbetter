

package com.onepercentbetter.core.data.test.repository

import com.onepercentbetter.core.data.repository.UserDataRepository
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.model.data.DarkThemeConfig
import com.onepercentbetter.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fake implementation of the [UserDataRepository] that returns hardcoded user data.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeUserDataRepository @Inject constructor(
    private val opbPreferencesDataSource: OPBPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        opbPreferencesDataSource.userData

    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) =
        opbPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) =
        opbPreferencesDataSource.setTopicIdFollowed(followedTopicId, followed)

    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        opbPreferencesDataSource.setNewsResourceBookmarked(newsResourceId, bookmarked)
    }
    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) =
        opbPreferencesDataSource.setNewsResourceViewed(newsResourceId, viewed)

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        opbPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }
}
