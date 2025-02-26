

package com.onepercentbetter.core.database.dao

import com.onepercentbetter.core.database.model.NewsResourceEntity
import com.onepercentbetter.core.database.model.NewsResourceTopicCrossRef
import com.onepercentbetter.core.database.model.TopicEntity
import com.onepercentbetter.core.database.model.asExternalModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

internal class NewsResourceDaoTest : DatabaseTest() {

    @Test
    fun getNewsResources_allEntries_areOrderedByPublishDateDesc() = runTest {
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

        val savedNewsResourceEntities = newsResourceDao.getNewsResources()
            .first()

        assertEquals(
            listOf(3L, 2L, 1L, 0L),
            savedNewsResourceEntities.map {
                it.asExternalModel().publishDate.toEpochMilliseconds()
            },
        )
    }

    @Test
    fun getNewsResources_filteredById_areOrderedByDescendingPublishDate() = runTest {
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

        val savedNewsResourceEntities = newsResourceDao.getNewsResources(
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
    fun getNewsResources_filteredByTopicId_areOrderedByDescendingPublishDate() = runTest {
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
                topicId = topicEntity.id,
            )
        }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources = newsResourceDao.getNewsResources(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map(TopicEntity::id)
                .toSet(),
        ).first()

        assertEquals(
            listOf("1", "0"),
            filteredNewsResources.map { it.entity.id },
        )
    }

    @Test
    fun getNewsResources_filteredByIdAndTopicId_areOrderedByDescendingPublishDate() = runTest {
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
                topicId = topicEntity.id,
            )
        }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources = newsResourceDao.getNewsResources(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map(TopicEntity::id)
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
                toDelete.map(NewsResourceEntity::id),
            )

            assertEquals(
                toKeep.map(NewsResourceEntity::id)
                    .toSet(),
                newsResourceDao.getNewsResources().first()
                    .map { it.entity.id }
                    .toSet(),
            )
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

private fun testNewsResource(
    id: String = "0",
    millisSinceEpoch: Long = 0,
) = NewsResourceEntity(
    id = id,
    title = "",
    content = "",
    url = "",
    headerImageUrl = "",
    publishDate = Instant.fromEpochMilliseconds(millisSinceEpoch),
    type = "Article 📚",
)
