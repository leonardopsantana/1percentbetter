package com.onepercentbetter.core.network.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: String,
    val title: String,
    val isDone: Boolean,
    val date: Instant,
    val categoryId: String
)
