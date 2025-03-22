

package com.onepercentbetter.core.testing.notifications

import com.onepercentbetter.core.model.data.TaskModel
import com.onepercentbetter.core.notifications.Notifier

/**
 * Aggregates news resources that have been notified for addition
 */
class TestNotifier : Notifier {

    private val mutableAddedNewResources = mutableListOf<List<TaskModel>>()

    val addedNewsResources: List<List<TaskModel>> = mutableAddedNewResources

    override fun postRoutineNotifications(newsResources: List<TaskModel>) {
        mutableAddedNewResources.add(newsResources)
    }
}
