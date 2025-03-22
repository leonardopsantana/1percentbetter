

package com.onepercentbetter.core.data.testdoubles

import com.onepercentbetter.core.database.dao.CategoryDao
import com.onepercentbetter.core.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Test double for [CategoryDao]
 */
class TestCategoryDao : CategoryDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<CategoryEntity>())

    override fun getTopicEntity(topicId: String): Flow<CategoryEntity> =
        throw NotImplementedError("Unused in tests")

    override fun getTopicEntities(): Flow<List<CategoryEntity>> = entitiesStateFlow

    override fun getTopicEntities(ids: Set<String>): Flow<List<CategoryEntity>> =
        getTopicEntities().map { topics -> topics.filter { it.categoryId in ids } }

    override suspend fun getCategories(): List<CategoryEntity> = emptyList()

    override suspend fun insertOrIgnoreTopics(topicEntities: List<CategoryEntity>): List<Long> {
        // Keep old values over new values
        entitiesStateFlow.update { oldValues ->
            (oldValues + topicEntities).distinctBy(CategoryEntity::categoryId)
        }
        return topicEntities.map { it.categoryId.toLong() }
    }

    override suspend fun upsertTopics(entities: List<CategoryEntity>) {
        // Overwrite old values with new values
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(CategoryEntity::categoryId) }
    }

    override suspend fun deleteTopics(ids: List<String>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities -> entities.filterNot { it.categoryId in idSet } }
    }
}
