

package com.onepercentbetter.feature.routine

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.onepercentbetter.core.rules.GrantPostNotificationsPermissionRule
import com.onepercentbetter.core.testing.data.followableTopicTestData
import com.onepercentbetter.core.testing.data.userNewsResourcesTestData
import com.onepercentbetter.core.ui.NewsFeedUiState.Loading
import com.onepercentbetter.core.ui.NewsFeedUiState.Success
import com.onepercentbetter.feature.routine.OnboardingUiState.Loading
import com.onepercentbetter.feature.routine.OnboardingUiState.NotShown
import com.onepercentbetter.feature.routine.OnboardingUiState.Shown
import org.junit.Rule
import org.junit.Test

class RoutineScreenTest {

    @get:Rule(order = 0)
    val postNotificationsPermission = GrantPostNotificationsPermissionRule()

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val doneButtonMatcher by lazy {
        hasText(
            composeTestRule.activity.resources.getString(R.string.feature_routine_done),
        )
    }

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            Box {
                RoutineScreen(
                    isSyncing = false,
                    onboardingUiState = Loading,
                    feedState = Loading,
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_routine_loading),
            )
            .assertExists()
    }

    @Test
    fun circularProgressIndicator_whenScreenIsSyncing_exists() {
        composeTestRule.setContent {
            Box {
                RoutineScreen(
                    isSyncing = true,
                    onboardingUiState = NotShown,
                    feedState = Success(emptyList()),
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_routine_loading),
            )
            .assertExists()
    }

    @Test
    fun topicSelector_whenNoTopicsSelected_showsTopicChipsAndDisabledDoneButton() {
        val testData = followableTopicTestData.map { it.copy(isFollowed = false) }

        composeTestRule.setContent {
            Box {
                RoutineScreen(
                    isSyncing = false,
                    onboardingUiState = Shown(
                        topics = testData,
                    ),
                    feedState = Success(
                        feed = emptyList(),
                    ),
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        testData.forEach { testTopic ->
            composeTestRule
                .onNodeWithText(testTopic.topic.name)
                .assertExists()
                .assertHasClickAction()
        }

        // Scroll until the Done button is visible
        composeTestRule
            .onAllNodes(hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(doneButtonMatcher)

        composeTestRule
            .onNode(doneButtonMatcher)
            .assertExists()
            .assertIsNotEnabled()
            .assertHasClickAction()
    }

    @Test
    fun topicSelector_whenSomeTopicsSelected_showsTopicChipsAndEnabledDoneButton() {
        composeTestRule.setContent {
            Box {
                RoutineScreen(
                    isSyncing = false,
                    onboardingUiState =
                    Shown(
                        // Follow one topic
                        topics = followableTopicTestData.mapIndexed { index, testTopic ->
                            testTopic.copy(isFollowed = index == 1)
                        },
                    ),
                    feedState = Success(
                        feed = emptyList(),
                    ),
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        followableTopicTestData.forEach { testTopic ->
            composeTestRule
                .onNodeWithText(testTopic.topic.name)
                .assertExists()
                .assertHasClickAction()
        }

        // Scroll until the Done button is visible
        composeTestRule
            .onAllNodes(hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(doneButtonMatcher)

        composeTestRule
            .onNode(doneButtonMatcher)
            .assertExists()
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun feed_whenInterestsSelectedAndLoading_showsLoadingIndicator() {
        composeTestRule.setContent {
            Box {
                RoutineScreen(
                    isSyncing = false,
                    onboardingUiState =
                    Shown(topics = followableTopicTestData),
                    feedState = Loading,
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_routine_loading),
            )
            .assertExists()
    }

    @Test
    fun feed_whenNoInterestsSelectionAndLoading_showsLoadingIndicator() {
        composeTestRule.setContent {
            Box {
                RoutineScreen(
                    isSyncing = false,
                    onboardingUiState = NotShown,
                    feedState = Loading,
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_routine_loading),
            )
            .assertExists()
    }

    @Test
    fun feed_whenNoInterestsSelectionAndLoaded_showsFeed() {
        composeTestRule.setContent {
            RoutineScreen(
                isSyncing = false,
                onboardingUiState = NotShown,
                feedState = Success(
                    feed = userNewsResourcesTestData,
                ),
                deepLinkedUserNewsResource = null,
                onTopicCheckedChanged = { _, _ -> },
                onTopicClick = {},
                saveFollowedTopics = {},
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onDeepLinkOpened = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                userNewsResourcesTestData[0].title,
                substring = true,
            )
            .assertExists()
            .assertHasClickAction()

        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(
                hasText(
                    userNewsResourcesTestData[1].title,
                    substring = true,
                ),
            )

        composeTestRule
            .onNodeWithText(
                userNewsResourcesTestData[1].title,
                substring = true,
            )
            .assertExists()
            .assertHasClickAction()
    }
}
