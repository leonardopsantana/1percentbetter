

package com.onepercentbetter.interests

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.onepercentbetter.core.model.data.FollowableTopic
import com.onepercentbetter.core.model.data.Topic
import com.onepercentbetter.core.testing.repository.TestTopicsRepository
import com.onepercentbetter.core.testing.repository.TestUserDataRepository
import com.onepercentbetter.core.testing.util.MainDispatcherRule
import com.onepercentbetter.feature.interests.InterestsUiState
import com.onepercentbetter.feature.interests.InterestsViewModel
import com.onepercentbetter.feature.interests.navigation.InterestsRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 *
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  See https://issuetracker.google.com/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class InterestsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val topicsRepository = TestTopicsRepository()
    private lateinit var viewModel: InterestsViewModel

    @Before
    fun setup() {
        viewModel = InterestsViewModel(
            savedStateHandle = SavedStateHandle(
                route = InterestsRoute(initialTopicId = testInputTopics[0].topic.id),
            ),
            userDataRepository = userDataRepository
        )
    }

    @Test
    fun uiState_whenInitialized_thenShowLoading() = runTest {
        assertEquals(InterestsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_whenFollowedTopicsAreLoading_thenShowLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        userDataRepository.setFollowedTopicIds(emptySet())
        assertEquals(InterestsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_whenFollowingNewTopic_thenShowUpdatedTopics() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val toggleTopicId = testOutputTopics[1].topic.id
        topicsRepository.sendTopics(testInputTopics.map { it.topic })
        userDataRepository.setFollowedTopicIds(setOf(testInputTopics[0].topic.id))

        assertEquals(
            false,
            (viewModel.uiState.value as InterestsUiState.Interests)
                .topics.first { it.topic.id == toggleTopicId }.isFollowed,
        )

        viewModel.followTopic(
            followedTopicId = toggleTopicId,
            true,
        )

        assertEquals(
            InterestsUiState.Interests(
                topics = testOutputTopics,
                selectedTopicId = testInputTopics[0].topic.id,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun uiState_whenUnfollowingTopics_thenShowUpdatedTopics() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val toggleTopicId = testOutputTopics[1].topic.id

        topicsRepository.sendTopics(testOutputTopics.map { it.topic })
        userDataRepository.setFollowedTopicIds(
            setOf(testOutputTopics[0].topic.id, testOutputTopics[1].topic.id),
        )

        assertEquals(
            true,
            (viewModel.uiState.value as InterestsUiState.Interests)
                .topics.first { it.topic.id == toggleTopicId }.isFollowed,
        )

        viewModel.followTopic(
            followedTopicId = toggleTopicId,
            false,
        )

        assertEquals(
            InterestsUiState.Interests(
                topics = testInputTopics,
                selectedTopicId = testInputTopics[0].topic.id,
            ),
            viewModel.uiState.value,
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
