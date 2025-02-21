

package com.onepercentbetter.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.onepercentbetterforyou.forYouScrollFeedDownUp
import com.onepercentbetterforyou.forYouSelectTopics
import com.onepercentbetterforyou.forYouWaitForContent
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the "For You" screen
 */
class ForYouBaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Scroll the feed critical user journey
            forYouWaitForContent()
            forYouSelectTopics(true)
            forYouScrollFeedDownUp()
        }
}
