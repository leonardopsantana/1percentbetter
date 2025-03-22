package com.onepercentbetter.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onepercentbetter.core.model.data.TaskModel
import kotlinx.datetime.Instant

/**
 * Defines an OPB task.
 */
@Entity(
    tableName = "task",
)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val taskTitle: String,
    val isDone: Boolean,
    val date: Instant,
)

fun TaskEntity.asModel() = TaskModel(
    id = id,
    title = taskTitle,
    isDone = isDone,
    date = date,
    categoryId = categoryId,
)
