

package com.onepercentbetter.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.onepercentbetter.core.designsystem.component.OPBLoadingWheel
import com.onepercentbetter.core.designsystem.component.OPBOverlayLoadingWheel
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetter.core.testing.util.DefaultRoborazziOptions
import com.onepercentbetter.core.testing.util.captureMultiTheme
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class LoadingWheelScreenshotTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingWheel_multipleThemes() {
        composeTestRule.captureMultiTheme("LoadingWheel") {
            Surface {
                OPBLoadingWheel(contentDesc = "test")
            }
        }
    }

    @Test
    fun overlayLoadingWheel_multipleThemes() {
        composeTestRule.captureMultiTheme("LoadingWheel", "OverlayLoadingWheel") {
            Surface {
                OPBOverlayLoadingWheel(contentDesc = "test")
            }
        }
    }

    @Test
    fun loadingWheelAnimation() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            OPBTheme {
                OPBLoadingWheel(contentDesc = "")
            }
        }
        // Try multiple frames of the animation; some arbitrary, some synchronized with duration.
        listOf(20L, 115L, 724L, 1000L).forEach { deltaTime ->
            composeTestRule.mainClock.advanceTimeBy(deltaTime)
            composeTestRule.onRoot()
                .captureRoboImage(
                    "src/test/screenshots/LoadingWheel/LoadingWheel_animation_$deltaTime.png",
                    roborazziOptions = DefaultRoborazziOptions,
                )
        }
    }
}
