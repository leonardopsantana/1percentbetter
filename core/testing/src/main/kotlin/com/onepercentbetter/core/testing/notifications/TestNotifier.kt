

package com.onepercentbetter.core.testing.notifications

import com.onepercentbetter.core.model.data.NewsResource
import com.onepercentbetter.core.notifications.Notifier

/**
 * Aggregates news resources that have been notified for addition
 */
class TestNotifier : Notifier {

    private val mutableAddedNewResources = mutableListOf<List<NewsResource>>()

    val addedNewsResources: List<List<NewsResource>> = mutableAddedNewResources

    override fun postNewsNotifications(newsResources: List<NewsResource>) {
        mutableAddedNewResources.add(newsResources)
    }
}
