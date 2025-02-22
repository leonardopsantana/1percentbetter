

package com.onepercentbetter.core.testing.util

import com.onepercentbetter.core.analytics.AnalyticsEvent
import com.onepercentbetter.core.analytics.AnalyticsHelper

class TestAnalyticsHelper : AnalyticsHelper {

    private val events = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    fun hasLogged(event: AnalyticsEvent) = event in events
}
