

package com.onepercentbetter.core.data

import com.onepercentbetter.core.data.repository.task.NewsResourceQuery
import com.onepercentbetter.core.model.data.mapToUserNewsResources
import com.onepercentbetter.core.testing.repository.TestTaskRepository
import com.onepercentbetter.core.testing.repository.TestUserDataRepository
import com.onepercentbetter.core.testing.repository.emptyUserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class CompositeTaskModelRepositoryTest {

    private val newsRepository = TestTaskRepository()
    private val userDataRepository = TestUserDataRepository()

    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    @Test
    fun whenNoFilters_allNewsResourcesAreReturned() = runTest {
        // Obtain the user news resources flow.
        val userNewsResources = userNewsResourceRepository.observeAll()

        // Send some news resources and user data into the data repositories.
        newsRepository.sendNewsResources(sampleNewsResources)

        // Construct the test user data with bookmarks and followed topics.
        val userData = emptyUserData.copy(
            bookmarkedNewsResources = setOf(sampleNewsResources[0].id, sampleNewsResources[2].id),
            followedTopics = setOf(sampleTopic1.id),
        )

        userDataRepository.setUserData(userData)

        // Check that the correct news resources are returned with their bookmarked state.
        assertEquals(
            sampleNewsResources.mapToUserNewsResources(userData),
            userNewsResources.first(),
        )
    }

    @Test
    fun whenFilteredByTopicId_matchingNewsResourcesAreReturned() = runTest {
        // Obtain a stream of user news resources for the given topic id.
        val userNewsResources =
            userNewsResourceRepository.observeAll(
                NewsResourceQuery(
                    filterTopicIds = setOf(
                        sampleTopic1.id,
                    ),
                ),
            )

        // Send test data into the repositories.
        newsRepository.sendNewsResources(sampleNewsResources)
        userDataRepository.setUserData(emptyUserData)

        // Check that only news resources with the given topic id are returned.
        assertEquals(
            sampleNewsResources
                .filter { sampleTopic1 in it.topics }
                .mapToUserNewsResources(emptyUserData),
            userNewsResources.first(),
        )
    }

    @Test
    fun whenFilteredByFollowedTopics_matchingNewsResourcesAreReturned() = runTest {
        // Obtain a stream of user news resources for the given topic id.
        val userNewsResources =
            userNewsResourceRepository.observeAllForFollowedTopics()

        // Send test data into the repositories.
        val userData = emptyUserData.copy(
            followedTopics = setOf(sampleTopic1.id),
        )
        newsRepository.sendNewsResources(sampleNewsResources)
        userDataRepository.setUserData(userData)

        // Check that only news resources with the given topic id are returned.
        assertEquals(
            sampleNewsResources
                .filter { sampleTopic1 in it.topics }
                .mapToUserNewsResources(userData),
            userNewsResources.first(),
        )
    }

    @Test
    fun whenFilteredByBookmarkedResources_matchingNewsResourcesAreReturned() = runTest {
        // Obtain the bookmarked user news resources flow.
        val userNewsResources = userNewsResourceRepository.observeAllBookmarked()

        // Send some news resources and user data into the data repositories.
        newsRepository.sendNewsResources(sampleNewsResources)

        // Construct the test user data with bookmarks and followed topics.
        val userData = emptyUserData.copy(
            bookmarkedNewsResources = setOf(sampleNewsResources[0].id, sampleNewsResources[2].id),
            followedTopics = setOf(sampleTopic1.id),
        )

        userDataRepository.setUserData(userData)

        // Check that the correct news resources are returned with their bookmarked state.
        assertEquals(
            listOf(sampleNewsResources[0], sampleNewsResources[2]).mapToUserNewsResources(userData),
            userNewsResources.first(),
        )
    }
}

private val sampleTopic1 = Topic(
    id = "Topic1",
    name = "Headlines",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

private val sampleTopic2 = Topic(
    id = "Topic2",
    name = "UI",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

private val sampleNewsResources = listOf(
    NewsResource(
        id = "1",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the One percent better series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(sampleTopic1),
    ),
    NewsResource(
        id = "2",
        title = "Transformations and customisations in the Paging Library",
        content = "A demonstration of different operations that can be performed with Paging. " +
            "Transformations like inserting separators, when to create a new pager, and " +
            "customisation options for consuming PagingData.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(sampleTopic1, sampleTopic2),
    ),
    NewsResource(
        id = "3",
        title = "Community tip on Paging",
        content = "Tips for using the Paging library from the developer community",
        url = "https://youtu.be/r5JgIyS3t3s",
        headerImageUrl = "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(sampleTopic2),
    ),
)
