

package com.onepercentbetter.core.database.dao

import com.onepercentbetter.core.database.model.CategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

internal class CategoryDaoTest : DatabaseTest() {

    @Test
    fun getTopics() = runTest {
        insertTopics()

        val savedTopics = categoryDao.getTopicEntities().first()

        assertEquals(
            listOf("1", "2", "3"),
            savedTopics.map { it.id },
        )
    }

    @Test
    fun getTopic() = runTest {
        insertTopics()

        val savedTopicEntity = categoryDao.getTopicEntity("2").first()

        assertEquals("performance", savedTopicEntity.name)
    }

    @Test
    fun getTopics_oneOff() = runTest {
        insertTopics()

        val savedTopics = categoryDao.getCategories()

        assertEquals(
            listOf("1", "2", "3"),
            savedTopics.map { it.categoryId },
        )
    }

    @Test
    fun getTopics_byId() = runTest {
        insertTopics()

        val savedTopics = categoryDao.getTopicEntities(setOf("1", "2"))
            .first()

        assertEquals(listOf("compose", "performance"), savedTopics.map { it.name })
    }

    @Test
    fun insertTopic_newEntryIsIgnoredIfAlreadyExists() = runTest {
        insertTopics()
        categoryDao.insertOrIgnoreTopics(
            listOf(testTopicEntity("1", "compose")),
        )

        val savedTopics = categoryDao.getCategories()

        assertEquals(3, savedTopics.size)
    }

    @Test
    fun upsertTopic_existingEntryIsUpdated() = runTest {
        insertTopics()
        categoryDao.upsertTopics(
            listOf(testTopicEntity("1", "newName")),
        )

        val savedTopics = categoryDao.getCategories()

        assertEquals(3, savedTopics.size)
        assertEquals("newName", savedTopics.first().name)
    }

    @Test
    fun deleteTopics_byId_existingEntriesAreDeleted() = runTest {
        insertTopics()
        categoryDao.deleteTopics(listOf("1", "2"))

        val savedTopics = categoryDao.getCategories()

        assertEquals(1, savedTopics.size)
        assertEquals("3", savedTopics.first().categoryId)
    }

    private suspend fun insertTopics() {
        val topicEntities = listOf(
            testTopicEntity("1", "compose"),
            testTopicEntity("2", "performance"),
            testTopicEntity("3", "headline"),
        )
        categoryDao.insertOrIgnoreTopics(topicEntities)
    }
}

private fun testTopicEntity(
    id: String = "0",
    name: String,
) = CategoryEntity(
    categoryId = id,
    name = name,
    shortDescription = "",
    longDescription = "",
    url = "",
    image = "",
)
