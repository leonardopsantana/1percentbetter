

package com.onepercentbetter.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.onepercentbetterinterests.goToInterestsScreen
import com.onepercentbetterinterests.interestsScrollTopicsDownUp
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the "Interests" screen
 */
class InterestsBaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Navigate to interests screen
            goToInterestsScreen()
            interestsScrollTopicsDownUp()
        }
}
