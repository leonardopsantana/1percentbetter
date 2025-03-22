

package com.onepercentbetter.core.data.model

import com.onepercentbetter.core.network.model.TaskResponse
import com.onepercentbetter.core.network.model.CategoryResponse
import com.onepercentbetter.core.network.model.asModel
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class NetworkEntityTest {

    @Test
    fun networkTopicMapsToDatabaseModel() {
        val networkModel = CategoryResponse(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "image URL",
        )
        val entity = networkModel.asEntity()

        assertEquals("0", entity.categoryId)
        assertEquals("Test", entity.name)
        assertEquals("short description", entity.shortDescription)
        assertEquals("long description", entity.longDescription)
        assertEquals("URL", entity.url)
        assertEquals("image URL", entity.image)
    }

    @Test
    fun networkNewsResourceMapsToDatabaseModel() {
        val networkModel =
            TaskResponse(
                id = "0",
                title = "title",
                content = "content",
                url = "url",
                headerImageUrl = "headerImageUrl",
                publishDate = Instant.fromEpochMilliseconds(1),
                type = "Article 📚",
            )
        val entity = networkModel.asEntity()

        assertEquals("0", entity.id)
        assertEquals("title", entity.taskTitle)
        assertEquals("content", entity.content)
        assertEquals("url", entity.url)
        assertEquals("headerImageUrl", entity.headerImageUrl)
        assertEquals(Instant.fromEpochMilliseconds(1), entity.publishDate)
        assertEquals("Article 📚", entity.type)
    }

    @Test
    fun networkTopicMapsToExternalModel() {
        val categoryResponse = CategoryResponse(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "imageUrl",
        )

        val expected = Topic(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "imageUrl",
        )

        assertEquals(expected, categoryResponse.asModel())
    }

    @Test
    fun networkNewsResourceMapsToExternalModel() {
        val taskResponse = TaskResponse(
            id = "0",
            title = "title",
            content = "content",
            url = "url",
            headerImageUrl = "headerImageUrl",
            publishDate = Instant.fromEpochMilliseconds(1),
            type = "Article 📚",
            topics = listOf("1", "2"),
        )

        val categoryRespons = listOf(
            CategoryResponse(
                id = "1",
                name = "Test 1",
                shortDescription = "short description 1",
                longDescription = "long description 1",
                url = "url 1",
                imageUrl = "imageUrl 1",
            ),
            CategoryResponse(
                id = "2",
                name = "Test 2",
                shortDescription = "short description 2",
                longDescription = "long description 2",
                url = "url 2",
                imageUrl = "imageUrl 2",
            ),
        )

        val expected = NewsResource(
            id = "0",
            title = "title",
            content = "content",
            url = "url",
            headerImageUrl = "headerImageUrl",
            publishDate = Instant.fromEpochMilliseconds(1),
            type = "Article 📚",
            topics = categoryRespons.map(CategoryResponse::asModel),
        )
        assertEquals(expected, taskResponse.asExternalModel(categoryRespons))
    }
}
