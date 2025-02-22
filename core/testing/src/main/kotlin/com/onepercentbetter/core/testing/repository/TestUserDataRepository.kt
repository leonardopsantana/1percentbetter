

package com.onepercentbetter.core.testing.repository

import com.onepercentbetter.core.data.repository.UserDataRepository
import com.onepercentbetter.core.model.data.DarkThemeConfig
import com.onepercentbetter.core.model.data.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

val emptyUserData = UserData(
    bookmarkedNewsResources = emptySet(),
    viewedNewsResources = emptySet(),
    followedTopics = emptySet(),
    themeBrand = ThemeBrand.DEFAULT,
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    useDynamicColor = false,
    shouldHideOnboarding = false,
)

class TestUserDataRepository : UserDataRepository {
    /**
     * The backing hot flow for the list of followed topic ids for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: com.onepercentbetter.core.testing.repository.emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()

    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {
        _userData.tryEmit(currentUserData.copy(followedTopics = followedTopicIds))
    }

    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {
        currentUserData.let { current ->
            val followedTopics = if (followed) {
                current.followedTopics + followedTopicId
            } else {
                current.followedTopics - followedTopicId
            }

            _userData.tryEmit(current.copy(followedTopics = followedTopics))
        }
    }

    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        currentUserData.let { current ->
            val bookmarkedNews = if (bookmarked) {
                current.bookmarkedNewsResources + newsResourceId
            } else {
                current.bookmarkedNewsResources - newsResourceId
            }

            _userData.tryEmit(current.copy(bookmarkedNewsResources = bookmarkedNews))
        }
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    viewedNewsResources =
                    if (viewed) {
                        current.viewedNewsResources + newsResourceId
                    } else {
                        current.viewedNewsResources - newsResourceId
                    },
                ),
            )
        }
    }

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(themeBrand = themeBrand))
        }
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(darkThemeConfig = darkThemeConfig))
        }
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(useDynamicColor = useDynamicColor))
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(shouldHideOnboarding = shouldHideOnboarding))
        }
    }

    /**
     * A test-only API to allow setting of user data directly.
     */
    fun setUserData(userData: UserData) {
        _userData.tryEmit(userData)
    }
}
