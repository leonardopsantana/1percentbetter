

package com.onepercentbetter.core.domain

import com.onepercentbetter.core.data.repository.TopicsRepository
import com.onepercentbetter.core.data.repository.UserDataRepository
import com.onepercentbetter.core.domain.TopicSortField.NAME
import com.onepercentbetter.core.domain.TopicSortField.NONE
import com.onepercentbetter.core.model.data.FollowableTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which obtains a list of topics with their followed state.
 */
class GetFollowableTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
    private val userDataRepository: UserDataRepository,
) {
    /**
     * Returns a list of topics with their associated followed state.
     *
     * @param sortBy - the field used to sort the topics. Default NONE = no sorting.
     */
    operator fun invoke(sortBy: TopicSortField = NONE): Flow<List<FollowableTopic>> = combine(
        userDataRepository.userData,
        topicsRepository.getTopics(),
    ) { userData, topics ->
        val followedTopics = topics
            .map { topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics,
                )
            }
        when (sortBy) {
            NAME -> followedTopics.sortedBy { it.topic.name }
            else -> followedTopics
        }
    }
}

enum class TopicSortField {
    NONE,
    NAME,
}
