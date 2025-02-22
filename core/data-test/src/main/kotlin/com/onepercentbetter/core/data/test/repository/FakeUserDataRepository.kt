

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
    private val OPBPreferencesDataSource: OPBPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        OPBPreferencesDataSource.userData

    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) =
        OPBPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) =
        OPBPreferencesDataSource.setTopicIdFollowed(followedTopicId, followed)

    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        OPBPreferencesDataSource.setNewsResourceBookmarked(newsResourceId, bookmarked)
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) =
        OPBPreferencesDataSource.setNewsResourceViewed(newsResourceId, viewed)

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        OPBPreferencesDataSource.setThemeBrand(themeBrand)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        OPBPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        OPBPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        OPBPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }
}
