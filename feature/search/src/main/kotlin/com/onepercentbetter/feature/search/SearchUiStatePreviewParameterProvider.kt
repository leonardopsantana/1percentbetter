

@file:Suppress("ktlint:standard:max-line-length")

package com.onepercentbetter.feature.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.onepercentbetter.core.model.data.FollowableTopic
import com.onepercentbetter.core.ui.PreviewParameterData.newsResources
import com.onepercentbetter.core.ui.PreviewParameterData.topics
import com.onepercentbetter.feature.search.SearchResultUiState.Success

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [SearchResultUiState] for Composable previews.
 */
class SearchUiStatePreviewParameterProvider : PreviewParameterProvider<SearchResultUiState> {
    override val values: Sequence<SearchResultUiState> = sequenceOf(
        Success(
            topics = topics.mapIndexed { i, topic ->
                FollowableTopic(topic = topic, isFollowed = i % 2 == 0)
            },
            newsResources = newsResources,
        ),
    )
}
