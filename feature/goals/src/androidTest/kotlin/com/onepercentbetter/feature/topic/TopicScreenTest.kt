

package com.onepercentbetter.feature.topic

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.onepercentbetter.core.testing.data.followableTopicTestData
import com.onepercentbetter.core.testing.data.userNewsResourcesTestData
import com.onepercentbetter.feature.goals.GoalsScreen
import com.onepercentbetter.feature.goals.GoalsUiState
import com.onepercentbetter.feature.goals.GoalsUiState.Loading
import com.onepercentbetter.feature.goals.GoalsUiState.Success
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI test for checking the correct behaviour of the Topic screen;
 * Verifies that, when a specific UiState is set, the corresponding
 * composables and details are shown
 */
class TopicScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var topicLoading: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            topicLoading = getString(R.string.feature_topic_loading)
        }
    }

    @Test
    fun opbLoadingWheel_whenScreenIsLoading_showLoading() {
        composeTestRule.setContent {
            GoalsScreen(
                topicUiState = TopicUiState.Loading,
                goalsUiState = Loading,
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(topicLoading)
            .assertExists()
    }

    @Test
    fun topicTitle_whenTopicIsSuccess_isShown() {
        val testTopic = followableTopicTestData.first()
        composeTestRule.setContent {
            GoalsScreen(
                topicUiState = TopicUiState.Success(testTopic),
                goalsUiState = Loading,
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        // Name is shown
        composeTestRule
            .onNodeWithText(testTopic.topic.name)
            .assertExists()

        // Description is shown
        composeTestRule
            .onNodeWithText(testTopic.topic.longDescription)
            .assertExists()
    }

    @Test
    fun news_whenTopicIsLoading_isNotShown() {
        composeTestRule.setContent {
            GoalsScreen(
                topicUiState = TopicUiState.Loading,
                goalsUiState = Success(userNewsResourcesTestData),
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        // Loading indicator shown
        composeTestRule
            .onNodeWithContentDescription(topicLoading)
            .assertExists()
    }

    @Test
    fun news_whenSuccessAndTopicIsSuccess_isShown() {
        val testTopic = followableTopicTestData.first()
        composeTestRule.setContent {
            GoalsScreen(
                topicUiState = TopicUiState.Success(testTopic),
                goalsUiState = Success(
                    userNewsResourcesTestData,
                ),
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        // Scroll to first news title if available
        composeTestRule
            .onAllNodes(hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(hasText(userNewsResourcesTestData.first().title))
    }
}
