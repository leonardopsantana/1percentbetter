

package com.onepercentbetter.core.notifications

import com.onepercentbetter.core.model.data.NewsResource

/**
 * Interface for creating notifications in the app
 */
interface Notifier {
    fun postNewsNotifications(newsResources: List<NewsResource>)
}
