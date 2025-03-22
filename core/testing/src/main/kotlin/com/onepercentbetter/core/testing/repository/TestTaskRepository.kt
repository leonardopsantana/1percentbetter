

package com.onepercentbetter.core.testing.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.repository.task.TaskRepository
import com.onepercentbetter.core.model.data.TaskWithCategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class TestTaskRepository : TaskRepository {

    override suspend fun getTasksForDate(selectedDate: Instant): Flow<List<TaskWithCategoryModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
