

package com.onepercentbetter.sync.workers

import com.onepercentbetter.core.analytics.AnalyticsEvent
import com.onepercentbetter.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logSyncStarted() =
    logEvent(
        AnalyticsEvent(type = "network_sync_started"),
    )

internal fun AnalyticsHelper.logSyncFinished(syncedSuccessfully: Boolean) {
    val eventType = if (syncedSuccessfully) "network_sync_successful" else "network_sync_failed"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}
