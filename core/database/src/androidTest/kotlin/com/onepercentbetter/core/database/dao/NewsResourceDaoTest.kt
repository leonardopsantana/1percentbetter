

package com.onepercentbetter.core.database.dao

import com.onepercentbetter.core.database.model.CategoryEntity
import com.onepercentbetter.core.database.model.TaskEntity
import com.onepercentbetter.core.database.model.asModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

internal class NewsResourceDaoTest : DatabaseTest() {

    @Test
    fun getRoutines_allEntries_areOrderedByPublishDateDesc() = runTest {
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )

        val savedNewsResourceEntities = newsResourceDao.getRoutines()
            .first()

        assertEquals(
            listOf(3L, 2L, 1L, 0L),
            savedNewsResourceEntities.map {
                it.asModel().publishDate.toEpochMilliseconds()
            },
        )
    }

    @Test
    fun getRoutines_filteredById_areOrderedByDescendingPublishDate() = runTest {
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )

        val savedNewsResourceEntities = newsResourceDao.getRoutines(
            useFilterNewsIds = true,
            filterNewsIds = setOf("3", "0"),
        )
            .first()

        assertEquals(
            listOf("3", "0"),
            savedNewsResourceEntities.map {
                it.entity.id
            },
        )
    }

    @Test
    fun getRoutines_filteredByTopicId_areOrderedByDescendingPublishDate() = runTest {
        val topicEntities = listOf(
            testTopicEntity(
                id = "1",
                name = "1",
            ),
            testTopicEntity(
                id = "2",
                name = "2",
            ),
        )
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        val newsResourceTopicCrossRefEntities = topicEntities.mapIndexed { index, topicEntity ->
            NewsResourceTopicCrossRef(
                newsResourceId = index.toString(),
                topicId = topicEntity.categoryId,
            )
        }

        categoryDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources = newsResourceDao.getRoutines(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map(CategoryEntity::categoryId)
                .toSet(),
        ).first()

        assertEquals(
            listOf("1", "0"),
            filteredNewsResources.map { it.entity.id },
        )
    }

    @Test
    fun getRoutines_filteredByIdAndTopicId_areOrderedByDescendingPublishDate() = runTest {
        val topicEntities = listOf(
            testTopicEntity(
                id = "1",
                name = "1",
            ),
            testTopicEntity(
                id = "2",
                name = "2",
            ),
        )
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        val newsResourceTopicCrossRefEntities = topicEntities.mapIndexed { index, topicEntity ->
            NewsResourceTopicCrossRef(
                newsResourceId = index.toString(),
                topicId = topicEntity.categoryId,
            )
        }

        categoryDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources = newsResourceDao.getRoutines(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map(CategoryEntity::categoryId)
                .toSet(),
            useFilterNewsIds = true,
            filterNewsIds = setOf("1"),
        ).first()

        assertEquals(
            listOf("1"),
            filteredNewsResources.map { it.entity.id },
        )
    }

    @Test
    fun deleteNewsResources_byId() =
        runTest {
            val newsResourceEntities = listOf(
                testNewsResource(
                    id = "0",
                    millisSinceEpoch = 0,
                ),
                testNewsResource(
                    id = "1",
                    millisSinceEpoch = 3,
                ),
                testNewsResource(
                    id = "2",
                    millisSinceEpoch = 1,
                ),
                testNewsResource(
                    id = "3",
                    millisSinceEpoch = 2,
                ),
            )
            newsResourceDao.upsertNewsResources(newsResourceEntities)

            val (toDelete, toKeep) = newsResourceEntities.partition { it.id.toInt() % 2 == 0 }

            newsResourceDao.deleteNewsResources(
                toDelete.map(TaskEntity::id),
            )

            assertEquals(
                toKeep.map(TaskEntity::id)
                    .toSet(),
                newsResourceDao.getRoutines().first()
                    .map { it.entity.id }
                    .toSet(),
            )
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

private fun testNewsResource(
    id: String = "0",
    millisSinceEpoch: Long = 0,
) = TaskEntity(
    id = id,
    taskTitle = "",
    content = "",
    url = "",
    headerImageUrl = "",
    publishDate = Instant.fromEpochMilliseconds(millisSinceEpoch),
    type = "Article 📚",
)
