

package com.onepercentbetter.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.onepercentbetter.PACKAGE_NAME
import com.onepercentbetter.routine.routineScrollFeedDownUp
import com.onepercentbetter.routine.routineSelectTopics
import com.onepercentbetter.routine.routineWaitForContent
import com.onepercentbetter.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the "Routine" screen
 */
class RoutineBaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Scroll the feed critical user journey
            routineWaitForContent()
            routineSelectTopics(true)
            routineScrollFeedDownUp()
        }
}
