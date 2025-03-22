package com.onepercentbetter.core.model.data

import kotlinx.datetime.Instant

data class TaskModel(
    val id: String,
    val title: String,
    val isDone: Boolean,
    val date: Instant,
    val categoryId: String
)
