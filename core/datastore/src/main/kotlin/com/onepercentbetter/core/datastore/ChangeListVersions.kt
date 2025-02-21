

package com.onepercentbetter.core.datastore

/**
 * Class summarizing the local version of each model for sync
 */
data class ChangeListVersions(
    val topicVersion: Int = -1,
    val newsResourceVersion: Int = -1,
)
