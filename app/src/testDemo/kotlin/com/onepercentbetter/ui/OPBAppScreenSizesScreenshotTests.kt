

package com.onepercentbetter.ui

import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.github.takahirom.roborazzi.captureRoboImage
import com.onepercentbetter.core.data.repository.category.CategoryRepository
import com.onepercentbetter.core.data.repository.user.UserDataRepository
import com.onepercentbetter.core.data.util.NetworkMonitor
import com.onepercentbetter.core.data.util.TimeZoneMonitor
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetter.core.testing.util.DefaultRoborazziOptions
import com.onepercentbetter.uitesthiltmanifest.HiltComponentActivity
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
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import java.util.TimeZone
import javax.inject.Inject

/**
 * Tests that the navigation UI is rendered correctly on different screen sizes.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// Configure Robolectric to use a very large screen size that can fit all of the test sizes.
// This allows enough room to render the content under test without clipping or scaling.
@Config(application = HiltTestApplication::class, qualifiers = "w1000dp-h1000dp-480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
@HiltAndroidTest
class OPBAppScreenSizesScreenshotTests {

    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository

    @Before
    fun setup() {
        hiltRule.inject()

        // Configure user data
        runBlocking {
            userDataRepository.setFollowedTopicIds(
                setOf(categoryRepository.getCategories().first().first().id),
            )
        }
    }

    @Before
    fun setTimeZone() {
        // Make time zone deterministic in tests
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    private fun testOPBAppScreenshotWithSize(width: Dp, height: Dp, screenshotName: String) {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    override = DeviceConfigurationOverride.ForcedSize(DpSize(width, height)),
                ) {
                    OPBTheme {
                        val fakeAppState = rememberOPBAppState(
                            networkMonitor = networkMonitor,
                            userNewsResourceRepository = userNewsResourceRepository,
                            timeZoneMonitor = timeZoneMonitor,
                        )
                        OPBApp(
                            fakeAppState,
                            windowAdaptiveInfo = WindowAdaptiveInfo(
                                windowSizeClass = WindowSizeClass.compute(
                                    width.value,
                                    height.value,
                                ),
                                windowPosture = Posture(),
                            ),
                        )
                    }
                }
            }
        }

        composeTestRule.onRoot()
            .captureRoboImage(
                "src/testDemo/screenshots/$screenshotName.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    @Test
    fun compactWidth_compactHeight_showsNavigationBar() {
        testOPBAppScreenshotWithSize(
            400.dp,
            400.dp,
            "compactWidth_compactHeight_showsNavigationBar",
        )
    }

    @Test
    fun mediumWidth_compactHeight_showsNavigationBar() {
        testOPBAppScreenshotWithSize(
            610.dp,
            400.dp,
            "mediumWidth_compactHeight_showsNavigationBar",
        )
    }

    @Test
    fun expandedWidth_compactHeight_showsNavigationBar() {
        testOPBAppScreenshotWithSize(
            900.dp,
            400.dp,
            "expandedWidth_compactHeight_showsNavigationBar",
        )
    }

    @Test
    fun compactWidth_mediumHeight_showsNavigationBar() {
        testOPBAppScreenshotWithSize(
            400.dp,
            500.dp,
            "compactWidth_mediumHeight_showsNavigationBar",
        )
    }

    @Test
    fun mediumWidth_mediumHeight_showsNavigationRail() {
        testOPBAppScreenshotWithSize(
            610.dp,
            500.dp,
            "mediumWidth_mediumHeight_showsNavigationRail",
        )
    }

    @Test
    fun expandedWidth_mediumHeight_showsNavigationRail() {
        testOPBAppScreenshotWithSize(
            900.dp,
            500.dp,
            "expandedWidth_mediumHeight_showsNavigationRail",
        )
    }

    @Test
    fun compactWidth_expandedHeight_showsNavigationBar() {
        testOPBAppScreenshotWithSize(
            400.dp,
            1000.dp,
            "compactWidth_expandedHeight_showsNavigationBar",
        )
    }

    @Test
    fun mediumWidth_expandedHeight_showsNavigationRail() {
        testOPBAppScreenshotWithSize(
            610.dp,
            1000.dp,
            "mediumWidth_expandedHeight_showsNavigationRail",
        )
    }

    @Test
    fun expandedWidth_expandedHeight_showsNavigationRail() {
        testOPBAppScreenshotWithSize(
            900.dp,
            1000.dp,
            "expandedWidth_expandedHeight_showsNavigationRail",
        )
    }
}
