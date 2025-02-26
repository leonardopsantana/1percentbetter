

package com.onepercentbetter.core.model.data

/**
 * Class summarizing user interest data
 */
data class UserData(
    val bookmarkedNewsResources: Set<String>,
    val viewedNewsResources: Set<String>,
    val followedTopics: Set<String>,
    val darkThemeConfig: DarkThemeConfig
)
