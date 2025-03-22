package com.onepercentbetter.core.network

import com.onepercentbetter.core.network.model.CategoryResponse
import com.onepercentbetter.core.network.model.TaskResponse
import kotlinx.datetime.Instant

/**
 * Interface representing network calls to the OPB backend
 */
interface OPBNetworkDataSource {
    suspend fun getCategories(): List<CategoryResponse>

    suspend fun getTasksForDate(date: Instant): List<TaskResponse>
}
