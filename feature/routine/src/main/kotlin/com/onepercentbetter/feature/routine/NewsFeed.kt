/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package com.onepercentbetter.feature.routine

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.onepercentbetter.core.analytics.LocalAnalyticsHelper
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetter.core.model.data.UserNewsResource
import com.onepercentbetter.core.ui.NewsResourceCardExpanded
import com.onepercentbetter.core.ui.UserNewsResourcePreviewParameterProvider
import com.onepercentbetter.core.ui.logNewsResourceOpened
import com.onepercentbetter.feature.routine.RoutineUiState.Loading
import com.onepercentbetter.feature.routine.RoutineUiState.Success

/**
 * An extension on [LazyListScope] defining a feed with news resources.
 * Depending on the [feedState], this might emit no items.
 */
fun LazyStaggeredGridScope.newsFeed(
    feedState: RoutineUiState,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        Loading -> Unit
        is Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "newsFeedItem" },
            ) { userNewsResource ->
                val analyticsHelper = LocalAnalyticsHelper.current

                NewsResourceCardExpanded(
                    userNewsResource = userNewsResource,
                    isBookmarked = userNewsResource.isSaved,
                    onClick = {
                        onExpandedCardClick()
                        analyticsHelper.logNewsResourceOpened(
                            newsResourceId = userNewsResource.id,
                        )
                        onNewsResourceViewed(userNewsResource.id)
                    },
                    hasBeenViewed = userNewsResource.hasBeenViewed,
                    onToggleBookmark = {
                        onNewsResourcesCheckedChanged(
                            userNewsResource.id,
                            !userNewsResource.isSaved,
                        )
                    },
                    onTopicClick = onTopicClick,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun NewsFeedLoadingPreview() {
    OPBTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            newsFeed(
                feedState = Loading,
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

@Preview
@Preview(device = Devices.TABLET)
@Composable
private fun NewsFeedContentPreview(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    OPBTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            newsFeed(
                feedState = Success(userNewsResources, emptyList()),
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}
