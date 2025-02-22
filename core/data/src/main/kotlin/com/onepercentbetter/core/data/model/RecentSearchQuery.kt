

package com.onepercentbetter.core.data.model

import com.onepercentbetter.core.database.model.RecentSearchQueryEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class RecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now(),
)

fun RecentSearchQueryEntity.asExternalModel() = RecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)
