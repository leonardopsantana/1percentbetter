package com.onepercentbetter.core.data.repository.task

import com.onepercentbetter.core.data.Syncable
import com.onepercentbetter.core.model.data.TaskWithCategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TaskRepository : Syncable {
    /**
     * Returns available news resources that match the specified [query].
     */
    suspend fun getTasksForDate(
        selectedDate: Instant,
    ): Flow<List<TaskWithCategoryModel>>
}
