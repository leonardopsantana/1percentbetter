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

package com.onepercentbetter.feature.goals

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onepercentbetter.core.designsystem.component.DynamicAsyncImage
import com.onepercentbetter.core.designsystem.component.OPBBackground
import com.onepercentbetter.core.designsystem.component.OPBLoadingWheel
import com.onepercentbetter.core.designsystem.component.scrollbar.DraggableScrollbar
import com.onepercentbetter.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.onepercentbetter.core.designsystem.component.scrollbar.scrollbarState
import com.onepercentbetter.core.designsystem.icon.OPBIcons
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetter.core.model.data.TaskModel
import com.onepercentbetter.core.ui.DevicePreviews
import com.onepercentbetter.core.ui.TrackScreenViewEvent
import com.onepercentbetter.core.ui.TrackScrollJank
import com.onepercentbetter.feature.goals.GoalsUiState.Error
import com.onepercentbetter.feature.goals.GoalsUiState.Loading
import com.onepercentbetter.feature.goals.GoalsUiState.Success

@Composable
fun GoalsScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel(),
) {
    val goalsUiState = viewModel.goalsState.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = "Topic: ${viewModel.goalId}")
    GoalsScreen(
        goalsUiState = goalsUiState.value,
        modifier = modifier.testTag("topic:${viewModel.goalId}"),
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
    )
}

@VisibleForTesting
@Composable
internal fun GoalsScreen(
    goalsUiState: GoalsUiState,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "topic:screen")
    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            }
            when (goalsUiState) {
                Loading -> item {
//                    OPBLoadingWheel(
//                        modifier = modifier,
//                        contentDesc = stringResource(id = string.feature_goals_loading),
//                    )
                }

                is Error -> TODO()
                is Success -> {
                    item {
                        TopicToolbar(
                            showBackButton = showBackButton,
                            onBackClick = onBackClick,
                        )
                    }
//                    topicBody(
//                        name = topicUiState.followableTopic.topic.name,
//                        description = topicUiState.followableTopic.topic.longDescription,
//                        news = goalsUiState,
//                        imageUrl = topicUiState.followableTopic.topic.imageUrl,
//                        onBookmarkChanged = onBookmarkChanged,
//                        onNewsResourceViewed = onNewsResourceViewed,
//                        onTopicClick = onTopicClick,
//                    )
                }
            }
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
        val itemsAvailable = topicItemsSize(goalsUiState)
        val scrollbarState = state.scrollbarState(
            itemsAvailable = itemsAvailable,
        )
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
}

private fun topicItemsSize(
    goalsUiState: GoalsUiState,
) = when (goalsUiState) {
    is Error -> 0 // Nothing
    Loading -> 1 // Loading bar
    is Success -> when (goalsUiState) {
        Error -> 0 // Nothing
        Loading -> 1 // Loading bar
        is Success -> 2 + goalsUiState.news.size // Toolbar, header
    }
}

private fun LazyListScope.topicBody(
    name: String,
    description: String,
    news: GoalsUiState,
    imageUrl: String,
    onBookmarkChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    // TODO: Show icon if available
    item {
        TopicHeader(name, description, imageUrl)
    }

    userNewsResourceCards(news, onBookmarkChanged, onNewsResourceViewed, onTopicClick)
}

@Composable
private fun TopicHeader(name: String, description: String, imageUrl: String) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
    ) {
        DynamicAsyncImage(
            imageUrl = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(132.dp)
                .padding(bottom = 12.dp),
        )
        Text(name, style = MaterialTheme.typography.displayMedium)
        if (description.isNotEmpty()) {
            Text(
                description,
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

// TODO: Could/should this be replaced with [LazyGridScope.newsFeed]?
private fun LazyListScope.userNewsResourceCards(
    news: GoalsUiState,
    onBookmarkChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    when (news) {
        is GoalsUiState.Success -> {
//            userNewsResourceCardItems(
//                items = news.news,
//                onToggleBookmark = { onBookmarkChanged(it.id, !it.isSaved) },
//                onNewsResourceViewed = onNewsResourceViewed,
//                onTopicClick = onTopicClick,
//                itemModifier = Modifier.padding(24.dp),
//            )
        }

        is Loading -> item {
            OPBLoadingWheel(contentDesc = "Loading news") // TODO
        }

        else -> item {
            Text("Error") // TODO
        }
    }
}

@Preview
@Composable
private fun TopicBodyPreview() {
    OPBTheme {
        LazyColumn {
            topicBody(
                name = "Jetpack Compose",
                description = "Lorem ipsum maximum",
                news = GoalsUiState.Success(emptyList()),
                imageUrl = "",
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

@Composable
private fun TopicToolbar(
//    uiState: FollowableTopic,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
    ) {
        if (showBackButton) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = OPBIcons.ArrowBack,
                    contentDescription = stringResource(
                        id = com.onepercentbetter.core.ui.R.string.core_ui_back,
                    ),
                )
            }
        } else {
            // Keeps the OPB FilterChip aligned to the end of the Row.
            Spacer(modifier = Modifier.width(1.dp))
        }
//        val selected = uiState.isFollowed
//        OPBFilterChip(
//            selected = selected,
//            onSelectedChange = onFollowClick,
//            modifier = Modifier.padding(end = 24.dp),
//        ) {
//            if (selected) {
//                Text("FOLLOWING")
//            } else {
//                Text("NOT FOLLOWING")
//            }
//        }
    }
}

@Composable
fun TopicScreenPopulated(
    taskModels: List<TaskModel>,
) {
    OPBTheme {
        OPBBackground {
            GoalsScreen(
                goalsUiState = Success(taskModels),
                showBackButton = true,
                onBackClick = {},
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun TopicScreenLoading() {
    OPBTheme {
        OPBBackground {
            GoalsScreen(
                goalsUiState = Loading,
                showBackButton = true,
                onBackClick = {},
                onTopicClick = {},
            )
        }
    }
}
