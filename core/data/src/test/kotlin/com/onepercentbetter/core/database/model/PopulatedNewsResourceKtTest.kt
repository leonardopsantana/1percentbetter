

package com.onepercentbetter.core.database.model

import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class PopulatedNewsResourceKtTest {
    @Test
    fun populated_news_resource_can_be_mapped_to_news_resource() {
        val populatedNewsResource = PopulatedNewsResource(
            entity = com.onepercentbetter.core.database.model.TaskEntity(
                id = "1",
                taskTitle = "news",
                content = "Hilt",
                url = "url",
                headerImageUrl = "headerImageUrl",
                type = "Video 📺",
                publishDate = Instant.fromEpochMilliseconds(1),
            ),
            topics = listOf(
                CategoryEntity(
                    categoryId = "3",
                    name = "name",
                    shortDescription = "short description",
                    longDescription = "long description",
                    url = "URL",
                    image = "image URL",
                ),
            ),
        )
        val newsResource = populatedNewsResource.asModel()

        assertEquals(
            NewsResource(
                id = "1",
                title = "news",
                content = "Hilt",
                url = "url",
                headerImageUrl = "headerImageUrl",
                type = "Video 📺",
                publishDate = Instant.fromEpochMilliseconds(1),
                topics = listOf(
                    Topic(
                        id = "3",
                        name = "name",
                        shortDescription = "short description",
                        longDescription = "long description",
                        url = "URL",
                        imageUrl = "image URL",
                    ),
                ),
            ),
            newsResource,
        )
    }
}
