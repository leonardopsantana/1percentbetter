

package com.onepercentbetter.feature.topic

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.onepercentbetter.core.testing.repository.TestTaskRepository
import com.onepercentbetter.core.testing.repository.TestCategoryRepository
import com.onepercentbetter.core.testing.repository.TestUserDataRepository
import com.onepercentbetter.core.testing.util.MainDispatcherRule
import com.onepercentbetter.feature.goals.GoalsUiState
import com.onepercentbetter.feature.goals.GoalsViewModel
import com.onepercentbetter.feature.goals.navigation.GoalsRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 *
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  *  See b/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class GoalsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val topicsRepository = TestCategoryRepository()
    private val newsRepository = TestTaskRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )
    private lateinit var viewModel: GoalsViewModel

    @Before
    fun setup() {
        viewModel = GoalsViewModel(
            savedStateHandle = SavedStateHandle(
                route = GoalsRoute(id = testInputTopics[0].topic.id),
            ),
            userDataRepository = userDataRepository,
            categoryRepository = topicsRepository,
            userNewsResourceRepository = userNewsResourceRepository,
        )
    }

    @Test
    fun topicId_matchesTopicIdFromSavedStateHandle() =
        assertEquals(testInputTopics[0].topic.id, viewModel.goalId)

    @Test
    fun uiStateTopic_whenSuccess_matchesTopicFromRepository() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.goalsUiState.collect() }

        topicsRepository.sendTopics(testInputTopics.map(FollowableTopic::topic))
        userDataRepository.setFollowedTopicIds(setOf(testInputTopics[1].topic.id))
        val item = viewModel.goalsUiState.value
        assertIs<TopicUiState.Success>(item)

        val topicFromRepository = topicsRepository.getTopic(
            testInputTopics[0].topic.id,
        ).first()

        assertEquals(topicFromRepository, item.followableTopic.topic)
    }

    @Test
    fun uiStateNews_whenInitialized_thenShowLoading() = runTest {
        assertEquals(GoalsUiState.Loading, viewModel.newsUiState.value)
    }

    @Test
    fun uiStateTopic_whenInitialized_thenShowLoading() = runTest {
        assertEquals(TopicUiState.Loading, viewModel.goalsUiState.value)
    }

    @Test
    fun uiStateTopic_whenFollowedIdsSuccessAndTopicLoading_thenShowLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.goalsUiState.collect() }

        userDataRepository.setFollowedTopicIds(setOf(testInputTopics[1].topic.id))
        assertEquals(TopicUiState.Loading, viewModel.goalsUiState.value)
    }

    @Test
    fun uiStateTopic_whenFollowedIdsSuccessAndTopicSuccess_thenTopicSuccessAndNewsLoading() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.goalsUiState.collect() }

            topicsRepository.sendTopics(testInputTopics.map { it.topic })
            userDataRepository.setFollowedTopicIds(setOf(testInputTopics[1].topic.id))
            val topicUiState = viewModel.goalsUiState.value
            val newsUiState = viewModel.newsUiState.value

            assertIs<TopicUiState.Success>(topicUiState)
            assertIs<GoalsUiState.Loading>(newsUiState)
        }

    @Test
    fun uiStateTopic_whenFollowedIdsSuccessAndTopicSuccessAndNewsIsSuccess_thenAllSuccess() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) {
                combine(
                    viewModel.goalsUiState,
                    viewModel.newsUiState,
                    ::Pair,
                ).collect()
            }
            topicsRepository.sendTopics(testInputTopics.map { it.topic })
            userDataRepository.setFollowedTopicIds(setOf(testInputTopics[1].topic.id))
            newsRepository.sendNewsResources(sampleNewsResources)
            val topicUiState = viewModel.goalsUiState.value
            val newsUiState = viewModel.newsUiState.value

            assertIs<TopicUiState.Success>(topicUiState)
            assertIs<GoalsUiState.Success>(newsUiState)
        }

    @Test
    fun uiStateTopic_whenFollowingTopic_thenShowUpdatedTopic() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.goalsUiState.collect() }

        topicsRepository.sendTopics(testInputTopics.map { it.topic })
        // Set which topic IDs are followed, not including 0.
        userDataRepository.setFollowedTopicIds(setOf(testInputTopics[1].topic.id))

        viewModel.followTopicToggle(true)

        assertEquals(
            TopicUiState.Success(followableTopic = testOutputTopics[0]),
            viewModel.goalsUiState.value,
        )
    }
}

private const val TOPIC_1_NAME = "Android Studio"
private const val TOPIC_2_NAME = "Build"
private const val TOPIC_3_NAME = "Compose"
private const val TOPIC_SHORT_DESC = "At vero eos et accusamus."
private const val TOPIC_LONG_DESC = "At vero eos et accusamus et iusto odio dignissimos ducimus."
private const val TOPIC_URL = "URL"
private const val TOPIC_IMAGE_URL = "Image URL"

private val testInputTopics = listOf(
    FollowableTopic(
        Topic(
            id = "0",
            name = TOPIC_1_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableTopic(
        Topic(
            id = "1",
            name = TOPIC_2_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = false,
    ),
    FollowableTopic(
        Topic(
            id = "2",
            name = TOPIC_3_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = false,
    ),
)

private val testOutputTopics = listOf(
    FollowableTopic(
        Topic(
            id = "0",
            name = TOPIC_1_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableTopic(
        Topic(
            id = "1",
            name = TOPIC_2_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableTopic(
        Topic(
            id = "2",
            name = TOPIC_3_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = false,
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
)
