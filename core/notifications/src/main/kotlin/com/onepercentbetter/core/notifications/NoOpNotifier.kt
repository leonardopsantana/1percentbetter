

package com.onepercentbetter.core.notifications

import com.onepercentbetter.core.model.data.NewsResource
import javax.inject.Inject

/**
 * Implementation of [Notifier] which does nothing. Useful for tests and previews.
 */
internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postNewsNotifications(newsResources: List<NewsResource>) = Unit
}
