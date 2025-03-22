

package com.onepercentbetter.core.data.model

import com.onepercentbetter.core.database.model.TaskEntity
import com.onepercentbetter.core.network.model.TaskResponse

fun TaskResponse.asEntity() = TaskEntity(
    id = id,
    taskTitle = title,
    categoryId = TODO(),
    isDone = TODO(),
    date = TODO(),
)
