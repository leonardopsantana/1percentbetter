

package com.onepercentbetter.core.network.model

import com.onepercentbetter.core.model.data.NewsResource
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Network representation of [NewsResource] when fetched from /newsresources
 */
@Serializable
data class NetworkNewsResource(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String,
    val publishDate: Instant,
    val type: String,
    val topics: List<String> = listOf(),
)
