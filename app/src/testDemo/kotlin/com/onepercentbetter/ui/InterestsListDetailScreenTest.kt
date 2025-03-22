

package com.onepercentbetter.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.onepercentbetter.core.data.repository.category.CategoryRepository
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetterui.interests2pane.InterestsListDetailScreen
import com.onepercentbetteruitesthiltmanifest.HiltComponentActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty
import kotlin.test.assertTrue
import com.onepercentbetter.feature.topic.R as FeatureTopicR

private const val EXPANDED_WIDTH = "w1200dp-h840dp"
private const val COMPACT_WIDTH = "w412dp-h915dp"

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class InterestsListDetailScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

    @Inject
    lateinit var categoryRepository: CategoryRepository

    /** Convenience function for getting all topics during tests, */
    private fun getTopics(): List<Topic> = runBlocking {
        categoryRepository.getCategories().first().sortedBy { it.name }
    }

    // The strings used for matching in these tests.
    private val placeholderText by composeTestRule.stringResource(FeatureTopicR.string.feature_topic_select_an_interest)
    private val listPaneTag = "interests:topics"

    private val Topic.testTag
        get() = "topic:${this.id}"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_initialState_showsTwoPanesWithPlaceholder() {
        composeTestRule.apply {
            setContent {
                OPBTheme {
                    InterestsListDetailScreen()
                }
            }

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsDisplayed()
        }
    }

    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_initialState_showsListPane() {
        composeTestRule.apply {
            setContent {
                OPBTheme {
                    InterestsListDetailScreen()
                }
            }

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
        }
    }

    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_topicSelected_updatesDetailPane() {
        composeTestRule.apply {
            setContent {
                OPBTheme {
                    InterestsListDetailScreen()
                }
            }

            val firstTopic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstTopic.testTag).assertIsDisplayed()
        }
    }

    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_topicSelected_showsTopicDetailPane() {
        composeTestRule.apply {
            setContent {
                OPBTheme {
                    InterestsListDetailScreen()
                }
            }

            val firstTopic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            onNodeWithTag(listPaneTag).assertIsNotDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstTopic.testTag).assertIsDisplayed()
        }
    }

    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_backPressFromTopicDetail_leavesInterests() {
        var unhandledBackPress = false
        composeTestRule.apply {
            setContent {
                OPBTheme {
                    // Back press should not be handled by the two pane layout, and thus
                    // "fall through" to this BackHandler.
                    BackHandler {
                        unhandledBackPress = true
                    }
                    InterestsListDetailScreen()
                }
            }

            val firstTopic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            waitForIdle()
            Espresso.pressBack()

            assertTrue(unhandledBackPress)
        }
    }

    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_backPressFromTopicDetail_showsListPane() {
        composeTestRule.apply {
            setContent {
                OPBTheme {
                    InterestsListDetailScreen()
                }
            }

            val firstTopic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            waitForIdle()
            Espresso.pressBack()

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstTopic.testTag).assertIsNotDisplayed()
        }
    }
}

private fun AndroidComposeTestRule<*, *>.stringResource(
    @StringRes resId: Int,
): ReadOnlyProperty<Any, String> =
    ReadOnlyProperty { _, _ -> activity.getString(resId) }
