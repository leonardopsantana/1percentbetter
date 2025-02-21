

package com.onepercentbetter.core.database.dao

import com.onepercentbetter.core.database.model.TopicEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

internal class TopicDaoTest : DatabaseTest() {

    @Test
    fun getTopics() = runTest {
        insertTopics()

        val savedTopics = topicDao.getTopicEntities().first()

        assertEquals(
            listOf("1", "2", "3"),
            savedTopics.map { it.id },
        )
    }

    @Test
    fun getTopic() = runTest {
        insertTopics()

        val savedTopicEntity = topicDao.getTopicEntity("2").first()

        assertEquals("performance", savedTopicEntity.name)
    }

    @Test
    fun getTopics_oneOff() = runTest {
        insertTopics()

        val savedTopics = topicDao.getOneOffTopicEntities()

        assertEquals(
            listOf("1", "2", "3"),
            savedTopics.map { it.id },
        )
    }

    @Test
    fun getTopics_byId() = runTest {
        insertTopics()

        val savedTopics = topicDao.getTopicEntities(setOf("1", "2"))
            .first()

        assertEquals(listOf("compose", "performance"), savedTopics.map { it.name })
    }

    @Test
    fun insertTopic_newEntryIsIgnoredIfAlreadyExists() = runTest {
        insertTopics()
        topicDao.insertOrIgnoreTopics(
            listOf(testTopicEntity("1", "compose")),
        )

        val savedTopics = topicDao.getOneOffTopicEntities()

        assertEquals(3, savedTopics.size)
    }

    @Test
    fun upsertTopic_existingEntryIsUpdated() = runTest {
        insertTopics()
        topicDao.upsertTopics(
            listOf(testTopicEntity("1", "newName")),
        )

        val savedTopics = topicDao.getOneOffTopicEntities()

        assertEquals(3, savedTopics.size)
        assertEquals("newName", savedTopics.first().name)
    }

    @Test
    fun deleteTopics_byId_existingEntriesAreDeleted() = runTest {
        insertTopics()
        topicDao.deleteTopics(listOf("1", "2"))

        val savedTopics = topicDao.getOneOffTopicEntities()

        assertEquals(1, savedTopics.size)
        assertEquals("3", savedTopics.first().id)
    }

    private suspend fun insertTopics() {
        val topicEntities = listOf(
            testTopicEntity("1", "compose"),
            testTopicEntity("2", "performance"),
            testTopicEntity("3", "headline"),
        )
        topicDao.insertOrIgnoreTopics(topicEntities)
    }
}

private fun testTopicEntity(
    id: String = "0",
    name: String,
) = TopicEntity(
    id = id,
    name = name,
    shortDescription = "",
    longDescription = "",
    url = "",
    imageUrl = "",
)
