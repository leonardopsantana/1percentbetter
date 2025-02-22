

package com.onepercentbetter.feature.routine

import androidx.lifecycle.SavedStateHandle
import com.onepercentbetter.core.analytics.AnalyticsEvent
import com.onepercentbetter.core.analytics.AnalyticsEvent.Param
import com.onepercentbetter.core.data.repository.CompositeUserNewsResourceRepository
import com.onepercentbetter.core.model.data.NewsResource
import com.onepercentbetter.core.model.data.Topic
import com.onepercentbetter.core.model.data.UserNewsResource
import com.onepercentbetter.core.notifications.DEEP_LINK_NEWS_RESOURCE_ID_KEY
import com.onepercentbetter.core.testing.repository.TestNewsRepository
import com.onepercentbetter.core.testing.repository.TestTopicsRepository
import com.onepercentbetter.core.testing.repository.TestUserDataRepository
import com.onepercentbetter.core.testing.repository.emptyUserData
import com.onepercentbetter.core.testing.util.MainDispatcherRule
import com.onepercentbetter.core.testing.util.TestAnalyticsHelper
import com.onepercentbetter.core.testing.util.TestSyncManager
import com.onepercentbetter.core.ui.NewsFeedUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class RoutineViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()
    private val analyticsHelper = TestAnalyticsHelper()
    private val userDataRepository = TestUserDataRepository()
    private val topicsRepository = TestTopicsRepository()
    private val newsRepository = TestNewsRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: RoutineViewModel

    @Before
    fun setup() {
        viewModel = RoutineViewModel(
            syncManager = syncManager,
            savedStateHandle = savedStateHandle,
            analyticsHelper = analyticsHelper,
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userNewsResourceRepository
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(NewsFeedUiState.Loading, viewModel.feedState.value)
    }

    @Test
    fun stateIsLoadingWhenFollowedTopicsAreLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        topicsRepository.sendTopics(sampleTopics)

        assertEquals(NewsFeedUiState.Loading, viewModel.feedState.value)
    }

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoInterests() = runTest {
        syncManager.setSyncing(true)

        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            true,
            viewModel.isSyncing.value,
        )
    }

    @Test
    fun topicSelectionUpdatesAfterSelectingTopic() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        topicsRepository.sendTopics(sampleTopics)
        userDataRepository.setFollowedTopicIds(emptySet())
        newsRepository.sendNewsResources(sampleNewsResources)

        assertEquals(
            NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.feedState.value,
        )

        val followedTopicId = sampleTopics[1].id
        viewModel.updateTopicSelection(followedTopicId, isChecked = true)

        val userData = emptyUserData.copy(followedTopics = setOf(followedTopicId))

        assertEquals(
            NewsFeedUiState.Success(
                feed = listOf(
                    UserNewsResource(sampleNewsResources[1], userData),
                    UserNewsResource(sampleNewsResources[2], userData),
                ),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun topicSelectionUpdatesAfterUnselectingTopic() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        topicsRepository.sendTopics(sampleTopics)
        userDataRepository.setFollowedTopicIds(emptySet())
        newsRepository.sendNewsResources(sampleNewsResources)
        viewModel.updateTopicSelection("1", isChecked = true)
        viewModel.updateTopicSelection("1", isChecked = false)

        advanceUntilIdle()
        assertEquals(
            NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun newsResourceSelectionUpdatesAfterLoadingFollowedTopics() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        val followedTopicIds = setOf("1")
        val userData = emptyUserData.copy(
            followedTopics = followedTopicIds
        )

        topicsRepository.sendTopics(sampleTopics)
        userDataRepository.setUserData(userData)
        newsRepository.sendNewsResources(sampleNewsResources)

        val bookmarkedNewsResourceId = "2"
        viewModel.updateNewsResourceSaved(
            newsResourceId = bookmarkedNewsResourceId,
            isChecked = true,
        )

        val userDataExpected = userData.copy(
            bookmarkedNewsResources = setOf(bookmarkedNewsResourceId),
        )

        assertEquals(
            NewsFeedUiState.Success(
                feed = listOf(
                    UserNewsResource(newsResource = sampleNewsResources[1], userDataExpected),
                    UserNewsResource(newsResource = sampleNewsResources[2], userDataExpected),
                ),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun deepLinkedNewsResourceIsFetchedAndResetAfterViewing() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.deepLinkedNewsResource.collect() }

        newsRepository.sendNewsResources(sampleNewsResources)
        userDataRepository.setUserData(emptyUserData)
        savedStateHandle[DEEP_LINK_NEWS_RESOURCE_ID_KEY] = sampleNewsResources.first().id

        assertEquals(
            expected = UserNewsResource(
                newsResource = sampleNewsResources.first(),
                userData = emptyUserData,
            ),
            actual = viewModel.deepLinkedNewsResource.value,
        )

        viewModel.onDeepLinkOpened(
            newsResourceId = sampleNewsResources.first().id,
        )

        assertNull(
            viewModel.deepLinkedNewsResource.value,
        )

        assertTrue(
            analyticsHelper.hasLogged(
                AnalyticsEvent(
                    type = "news_deep_link_opened",
                    extras = listOf(
                        Param(
                            key = DEEP_LINK_NEWS_RESOURCE_ID_KEY,
                            value = sampleNewsResources.first().id,
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun whenUpdateNewsResourceSavedIsCalled_bookmarkStateIsUpdated() = runTest {
        val newsResourceId = "123"
        viewModel.updateNewsResourceSaved(newsResourceId, true)

        assertEquals(
            expected = setOf(newsResourceId),
            actual = userDataRepository.userData.first().bookmarkedNewsResources,
        )

        viewModel.updateNewsResourceSaved(newsResourceId, false)

        assertEquals(
            expected = emptySet(),
            actual = userDataRepository.userData.first().bookmarkedNewsResources,
        )
    }
}

private val sampleTopics = listOf(
    Topic(
        id = "0",
        name = "Headlines",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "1",
        name = "UI",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "2",
        name = "Tools",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
)

private val sampleNewsResources = listOf(
    NewsResource(
        id = "1",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the One percent better series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "0",
                name = "Headlines",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    NewsResource(
        id = "2",
        title = "Transformations and customisations in the Paging Library",
        content = "A demonstration of different operations that can be performed with Paging. " +
            "Transformations like inserting separators, when to create a new pager, and " +
            "customisation options for consuming PagingData.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    NewsResource(
        id = "3",
        title = "Community tip on Paging",
        content = "Tips for using the Paging library from the developer community",
        url = "https://youtu.be/r5JgIyS3t3s",
        headerImageUrl = "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
)
