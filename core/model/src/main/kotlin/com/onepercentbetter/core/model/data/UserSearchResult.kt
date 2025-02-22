

package com.onepercentbetter.core.model.data

/**
 * An entity of [SearchResult] with additional user information such as whether the user is
 * following a topic.
 */
data class UserSearchResult(
    val topics: List<FollowableTopic> = emptyList(),
    val newsResources: List<UserNewsResource> = emptyList(),
)
