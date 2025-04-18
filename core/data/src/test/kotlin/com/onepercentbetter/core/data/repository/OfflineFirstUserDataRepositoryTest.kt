package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.analytics.NoOpAnalyticsHelper
import com.onepercentbetter.core.data.repository.user.OfflineFirstUserDataRepository
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.datastore.test.InMemoryDataStore
import com.onepercentbetter.core.model.data.DarkThemeConfig
import com.onepercentbetter.core.model.data.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class OfflineFirstUserDataRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstUserDataRepository

    private lateinit var opbPreferencesDataSource: OPBPreferencesDataSource

    private val analyticsHelper = NoOpAnalyticsHelper()

    @Before
    fun setup() {
        opbPreferencesDataSource =
            OPBPreferencesDataSource(InMemoryDataStore(_root_ide_package_.com.onepercentbetter.core.datastore.UserPreferences.getDefaultInstance()))

        subject = OfflineFirstUserDataRepository(
            opbPreferencesDataSource = opbPreferencesDataSource,
            analyticsHelper,
        )
    }

    @Test
    fun offlineFirstUserDataRepository_default_user_data_is_correct() =
        testScope.runTest {
            assertEquals(
                UserData(
                    bookmarkedNewsResources = emptySet(),
                    viewedNewsResources = emptySet(),
                    followedTopics = emptySet(),
                    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                ),
                subject.userData.first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_toggle_followed_topics_logic_delegates_to_opb_preferences() =
        testScope.runTest {
            subject.setTopicIdFollowed(followedTopicId = "0", followed = true)

            assertEquals(
                setOf("0"),
                subject.userData
                    .map { it.followedTopics }
                    .first(),
            )

            subject.setTopicIdFollowed(followedTopicId = "1", followed = true)

            assertEquals(
                setOf("0", "1"),
                subject.userData
                    .map { it.followedTopics }
                    .first(),
            )

            assertEquals(
                opbPreferencesDataSource.userData
                    .map { it.followedTopics }
                    .first(),
                subject.userData
                    .map { it.followedTopics }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_followed_topics_logic_delegates_to_opb_preferences() =
        testScope.runTest {
            subject.setFollowedTopicIds(followedTopicIds = setOf("1", "2"))

            assertEquals(
                setOf("1", "2"),
                subject.userData
                    .map { it.followedTopics }
                    .first(),
            )

            assertEquals(
                opbPreferencesDataSource.userData
                    .map { it.followedTopics }
                    .first(),
                subject.userData
                    .map { it.followedTopics }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_bookmark_news_resource_logic_delegates_to_opb_preferences() =
        testScope.runTest {
            subject.setNewsResourceBookmarked(newsResourceId = "0", bookmarked = true)

            assertEquals(
                setOf("0"),
                subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
            )

            subject.setNewsResourceBookmarked(newsResourceId = "1", bookmarked = true)

            assertEquals(
                setOf("0", "1"),
                subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
            )

            assertEquals(
                opbPreferencesDataSource.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
                subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_update_viewed_news_resources_delegates_to_opb_preferences() =
        runTest {
            subject.setNewsResourceViewed(newsResourceId = "0", viewed = true)

            assertEquals(
                setOf("0"),
                subject.userData
                    .map { it.viewedNewsResources }
                    .first(),
            )

            subject.setNewsResourceViewed(newsResourceId = "1", viewed = true)

            assertEquals(
                setOf("0", "1"),
                subject.userData
                    .map { it.viewedNewsResources }
                    .first(),
            )

            assertEquals(
                opbPreferencesDataSource.userData
                    .map { it.viewedNewsResources }
                    .first(),
                subject.userData
                    .map { it.viewedNewsResources }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_dark_theme_config_delegates_to_opb_preferences() =
        testScope.runTest {
            subject.setDarkThemeConfig(DarkThemeConfig.DARK)

            assertEquals(
                DarkThemeConfig.DARK,
                subject.userData
                    .map { it.darkThemeConfig }
                    .first(),
            )
            assertEquals(
                DarkThemeConfig.DARK,
                opbPreferencesDataSource
                    .userData
                    .map { it.darkThemeConfig }
                    .first(),
            )
        }
}
