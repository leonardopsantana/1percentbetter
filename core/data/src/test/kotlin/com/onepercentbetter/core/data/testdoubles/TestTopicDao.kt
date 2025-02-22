

package com.onepercentbetter.core.data.testdoubles

import com.onepercentbetter.core.database.dao.TopicDao
import com.onepercentbetter.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Test double for [TopicDao]
 */
class TestTopicDao : TopicDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<TopicEntity>())

    override fun getTopicEntity(topicId: String): Flow<TopicEntity> =
        throw NotImplementedError("Unused in tests")

    override fun getTopicEntities(): Flow<List<TopicEntity>> = entitiesStateFlow

    override fun getTopicEntities(ids: Set<String>): Flow<List<TopicEntity>> =
        getTopicEntities().map { topics -> topics.filter { it.id in ids } }

    override suspend fun getOneOffTopicEntities(): List<TopicEntity> = emptyList()

    override suspend fun insertOrIgnoreTopics(topicEntities: List<TopicEntity>): List<Long> {
        // Keep old values over new values
        entitiesStateFlow.update { oldValues ->
            (oldValues + topicEntities).distinctBy(TopicEntity::id)
        }
        return topicEntities.map { it.id.toLong() }
    }

    override suspend fun upsertTopics(entities: List<TopicEntity>) {
        // Overwrite old values with new values
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(TopicEntity::id) }
    }

    override suspend fun deleteTopics(ids: List<String>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities -> entities.filterNot { it.id in idSet } }
    }
}
