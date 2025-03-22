

package com.onepercentbetter.core.data

import com.onepercentbetter.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import com.onepercentbetter.core.model.data.UserData
import com.onepercentbetter.core.model.data.TaskModel
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskModelTest {

    /**
     * Given: Some user data and news resources
     * When: They are combined using `UserNewsResource.from`
     * Then: The correct UserNewsResources are constructed
     */
    @Test
    fun userNewsResourcesAreConstructedFromNewsResourcesAndUserData() {
        val newsResource1 = NewsResource(
            id = "N1",
            title = "Test news title",
            content = "Test news content",
            url = "Test URL",
            headerImageUrl = "Test image URL",
            publishDate = Clock.System.now(),
            type = "Article 📚",
            topics = listOf(
                Topic(
                    id = "T1",
                    name = "Topic 1",
                    shortDescription = "Topic 1 short description",
                    longDescription = "Topic 1 long description",
                    url = "Topic 1 URL",
                    imageUrl = "Topic 1 image URL",
                ),
                Topic(
                    id = "T2",
                    name = "Topic 2",
                    shortDescription = "Topic 2 short description",
                    longDescription = "Topic 2 long description",
                    url = "Topic 2 URL",
                    imageUrl = "Topic 2 image URL",
                ),
            ),
        )

        val userData = UserData(
            bookmarkedNewsResources = setOf("N1"),
            viewedNewsResources = setOf("N1"),
            followedTopics = setOf("T1"),
            darkThemeConfig = FOLLOW_SYSTEM,
        )

        val taskModel = TaskModel(newsResource1, userData)

        // Check that the simple field mappings have been done correctly.
        assertEquals(newsResource1.id, taskModel.id)
        assertEquals(newsResource1.title, taskModel.title)
        assertEquals(newsResource1.content, taskModel.content)
        assertEquals(newsResource1.url, taskModel.url)
        assertEquals(newsResource1.headerImageUrl, taskModel.headerImageUrl)
        assertEquals(newsResource1.publishDate, taskModel.publishDate)

        // Check that each Topic has been converted to a FollowedTopic correctly.
        assertEquals(newsResource1.topics.size, taskModel.followableTopics.size)
        for (topic in newsResource1.topics) {
            // Construct the expected FollowableTopic.
            val followableTopic = FollowableTopic(
                topic = topic,
                isFollowed = topic.id in userData.followedTopics,
            )
            assertTrue(taskModel.followableTopics.contains(followableTopic))
        }

        // Check that the saved flag is set correctly.
        assertEquals(
            newsResource1.id in userData.bookmarkedNewsResources,
            taskModel.isSaved,
        )
    }
}
