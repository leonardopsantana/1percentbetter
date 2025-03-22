

package com.onepercentbetter.core.notifications

import com.onepercentbetter.core.model.data.TaskModel

/**
 * Interface for creating notifications in the app
 */
interface Notifier {
    fun postRoutineNotifications(newsResources: List<TaskModel>)
}
