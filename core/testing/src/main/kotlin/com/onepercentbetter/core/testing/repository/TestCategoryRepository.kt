

package com.onepercentbetter.core.testing.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.repository.category.CategoryRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestCategoryRepository : CategoryRepository {
    /**
     * The backing hot flow for the list of topics ids for testing.
     */
    private val topicsFlow: MutableSharedFlow<List<Topic>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getCategories(): Flow<List<Topic>> = topicsFlow

    override fun getTopic(id: String): Flow<Topic> =
        topicsFlow.map { topics -> topics.find { it.id == id }!! }

    /**
     * A test-only API to allow controlling the list of topics from tests.
     */
    fun sendTopics(topics: List<Topic>) {
        topicsFlow.tryEmit(topics)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
