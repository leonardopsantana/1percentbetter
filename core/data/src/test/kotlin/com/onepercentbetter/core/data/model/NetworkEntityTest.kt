

package com.onepercentbetter.core.data.model

import com.onepercentbetter.core.model.data.NewsResource
import com.onepercentbetter.core.model.data.Topic
import com.onepercentbetter.core.network.model.NetworkNewsResource
import com.onepercentbetter.core.network.model.NetworkTopic
import com.onepercentbetter.core.network.model.asExternalModel
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class NetworkEntityTest {

    @Test
    fun networkTopicMapsToDatabaseModel() {
        val networkModel = NetworkTopic(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "image URL",
        )
        val entity = networkModel.asEntity()

        assertEquals("0", entity.id)
        assertEquals("Test", entity.name)
        assertEquals("short description", entity.shortDescription)
        assertEquals("long description", entity.longDescription)
        assertEquals("URL", entity.url)
        assertEquals("image URL", entity.imageUrl)
    }

    @Test
    fun networkNewsResourceMapsToDatabaseModel() {
        val networkModel =
            NetworkNewsResource(
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
        assertEquals("title", entity.title)
        assertEquals("content", entity.content)
        assertEquals("url", entity.url)
        assertEquals("headerImageUrl", entity.headerImageUrl)
        assertEquals(Instant.fromEpochMilliseconds(1), entity.publishDate)
        assertEquals("Article 📚", entity.type)
    }

    @Test
    fun networkTopicMapsToExternalModel() {
        val networkTopic = NetworkTopic(
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

        assertEquals(expected, networkTopic.asExternalModel())
    }

    @Test
    fun networkNewsResourceMapsToExternalModel() {
        val networkNewsResource = NetworkNewsResource(
            id = "0",
            title = "title",
            content = "content",
            url = "url",
            headerImageUrl = "headerImageUrl",
            publishDate = Instant.fromEpochMilliseconds(1),
            type = "Article 📚",
            topics = listOf("1", "2"),
        )

        val networkTopics = listOf(
            NetworkTopic(
                id = "1",
                name = "Test 1",
                shortDescription = "short description 1",
                longDescription = "long description 1",
                url = "url 1",
                imageUrl = "imageUrl 1",
            ),
            NetworkTopic(
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
            topics = networkTopics.map(NetworkTopic::asExternalModel),
        )
        assertEquals(expected, networkNewsResource.asExternalModel(networkTopics))
    }
}
