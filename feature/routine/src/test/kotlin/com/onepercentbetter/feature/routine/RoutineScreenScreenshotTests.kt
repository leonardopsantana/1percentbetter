//
//
//package com.onepercentbetter.feature.routine
//
//import androidx.activity.ComponentActivity
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils
//import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesElements
//import com.google.android.apps.common.testing.accessibility.framework.checks.TextContrastCheck
//import com.google.android.apps.common.testing.accessibility.framework.matcher.ElementMatchers.withText
//import com.onepercentbetter.core.designsystem.component.OPBBackground
//import com.onepercentbetter.core.designsystem.theme.OPBTheme
//import com.onepercentbetter.core.testing.util.DefaultTestDevices
//import com.onepercentbetter.core.testing.util.captureForDevice
//import com.onepercentbetter.core.testing.util.captureMultiDevice
//import dagger.hilt.android.testing.HiltTestApplication
//import org.hamcrest.Matchers
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//import org.robolectric.annotation.GraphicsMode
//import org.robolectric.annotation.LooperMode
//import java.util.TimeZone
//
///**
// * Screenshot tests for the [RoutineScreen].
// */
//@RunWith(RobolectricTestRunner::class)
//@GraphicsMode(GraphicsMode.Mode.NATIVE)
//@Config(application = HiltTestApplication::class)
//@LooperMode(LooperMode.Mode.PAUSED)
//class RoutineScreenScreenshotTests {
//
//    /**
//     * Use a test activity to set the content on.
//     */
//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
//
//    private val userNewsResources = UserNewsResourcePreviewParameterProvider().values.first()
//
//    @Before
//    fun setTimeZone() {
//        // Make time zone deterministic in tests
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
//    }
//
//    @Test
//    fun routineScreenPopulatedFeed() {
//        composeTestRule.captureMultiDevice("RoutineScreenPopulatedFeed") {
//            OPBTheme {
//                RoutineScreen(
//                    isSyncing = false,
//                    feedState = Success(
//                        feed = userNewsResources,
//                    ),
//                    onNewsResourcesCheckedChanged = { _, _ -> },
//                    onNewsResourceViewed = {},
//                    onTopicClick = {},
//                    deepLinkedUserNewsResource = null,
//                    onDeepLinkOpened = {},
//                )
//            }
//        }
//    }
//
//    @Test
//    fun routineScreenLoading() {
//        composeTestRule.captureMultiDevice("RoutineScreenLoading") {
//            OPBTheme {
//                RoutineScreen(
//                    isSyncing = false,
//                    feedState = RoutineUiState.Loading,
//                    onNewsResourcesCheckedChanged = { _, _ -> },
//                    onNewsResourceViewed = {},
//                    onTopicClick = {},
//                    deepLinkedUserNewsResource = null,
//                    onDeepLinkOpened = {},
//                )
//            }
//        }
//    }
//
//    @Test
//    fun routineScreenTopicSelection() {
//        composeTestRule.captureMultiDevice(
//            "RoutineScreenTopicSelection",
//            accessibilitySuppressions = Matchers.allOf(
//                AccessibilityCheckResultUtils.matchesCheck(TextContrastCheck::class.java),
//                Matchers.anyOf(
//                    // Disabled Button
//                    matchesElements(withText("Done")),
//
//                    // TODO investigate, seems a false positive
//                    matchesElements(withText("What are you interested in?")),
//                    matchesElements(withText("UI")),
//                ),
//            ),
//        ) {
//            RoutineScreenTopicSelection()
//        }
//    }
//
//    @Test
//    fun routineScreenTopicSelection_dark() {
//        composeTestRule.captureForDevice(
//            deviceName = "phone_dark",
//            deviceSpec = DefaultTestDevices.PHONE.spec,
//            screenshotName = "RoutineScreenTopicSelection",
//            darkMode = true,
//        ) {
//            RoutineScreenTopicSelection()
//        }
//    }
//
//    @Test
//    fun routineScreenPopulatedAndLoading() {
//        composeTestRule.captureMultiDevice("RoutineScreenPopulatedAndLoading") {
//            RoutineScreenPopulatedAndLoading()
//        }
//    }
//
//    @Test
//    fun routineScreenPopulatedAndLoading_dark() {
//        composeTestRule.captureForDevice(
//            deviceName = "phone_dark",
//            deviceSpec = DefaultTestDevices.PHONE.spec,
//            screenshotName = "RoutineScreenPopulatedAndLoading",
//            darkMode = true,
//        ) {
//            RoutineScreenPopulatedAndLoading()
//        }
//    }
//
//    @Composable
//    private fun RoutineScreenTopicSelection() {
//        OPBTheme {
//            OPBBackground {
//                RoutineScreen(
//                    isSyncing = false,
//                    feedState = Success(
//                        feed = userNewsResources,
//                    ),
//                    onNewsResourcesCheckedChanged = { _, _ -> },
//                    onNewsResourceViewed = {},
//                    onTopicClick = {},
//                    deepLinkedUserNewsResource = null,
//                    onDeepLinkOpened = {},
//                )
//            }
//        }
//    }
//
//    @Composable
//    private fun RoutineScreenPopulatedAndLoading() {
//        OPBTheme {
//            OPBBackground {
//                OPBTheme {
//                    RoutineScreen(
//                        isSyncing = true,
//                        feedState = Success(
//                            feed = userNewsResources,
//                        ),
//                        onNewsResourcesCheckedChanged = { _, _ -> },
//                        onNewsResourceViewed = {},
//                        onTopicClick = {},
//                        deepLinkedUserNewsResource = null,
//                        onDeepLinkOpened = {},
//                    )
//                }
//            }
//        }
//    }
//}
