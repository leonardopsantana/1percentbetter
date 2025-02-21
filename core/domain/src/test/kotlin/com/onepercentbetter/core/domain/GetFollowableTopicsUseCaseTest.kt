

package com.onepercentbetter.core.domain

import com.onepercentbetter.core.domain.GetFollowableTopicsUseCase
import com.onepercentbetter.core.domain.TopicSortField.NAME
import com.onepercentbetter.core.model.data.FollowableTopic
import com.onepercentbetter.core.model.data.Topic
import com.onepercentbetter.core.testing.repository.TestTopicsRepository
import com.onepercentbetter.core.testing.repository.TestUserDataRepository
import com.onepercentbetter.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetFollowableTopicsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val topicsRepository = TestTopicsRepository()
    private val userDataRepository = TestUserDataRepository()

    val useCase = GetFollowableTopicsUseCase(
        topicsRepository,
        userDataRepository,
    )

    @Test
    fun whenNoParams_followableTopicsAreReturnedWithNoSorting() = runTest {
        // Obtain a stream of followable topics.
        val followableTopics = useCase()

        // Send some test topics and their followed state.
        topicsRepository.sendTopics(testTopics)
        userDataRepository.setFollowedTopicIds(setOf(testTopics[0].id, testTopics[2].id))

        // Check that the order hasn't changed and that the correct topics are marked as followed.
        assertEquals(
            listOf(
                FollowableTopic(testTopics[0], true),
                FollowableTopic(testTopics[1], false),
                FollowableTopic(testTopics[2], true),
            ),
            followableTopics.first(),
        )
    }

    @Test
    fun whenSortOrderIsByName_topicsSortedByNameAreReturned() = runTest {
        // Obtain a stream of followable topics, sorted by name.
        val followableTopics = useCase(
            sortBy = NAME,
        )

        // Send some test topics and their followed state.
        topicsRepository.sendTopics(testTopics)
        userDataRepository.setFollowedTopicIds(setOf())

        // Check that the followable topics are sorted by the topic name.
        assertEquals(
            followableTopics.first(),
            testTopics
                .sortedBy { it.name }
                .map {
                    FollowableTopic(it, false)
                },
        )
    }
}

private val testTopics = listOf(
    Topic("1", "Headlines", "", "", "", ""),
    Topic("2", "Android Studio", "", "", "", ""),
    Topic("3", "Compose", "", "", "", ""),
)
