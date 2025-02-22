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

import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import com.onepercentbetter.core.designsystem.component.OPBOverlayLoadingWheel
import com.onepercentbetter.core.designsystem.component.scrollbar.DraggableScrollbar
import com.onepercentbetter.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.onepercentbetter.core.designsystem.component.scrollbar.scrollbarState
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetter.core.model.data.UserNewsResource
import com.onepercentbetter.core.ui.DevicePreviews
import com.onepercentbetter.core.ui.NewsFeedUiState
import com.onepercentbetter.core.ui.NewsFeedUiState.Success
import com.onepercentbetter.core.ui.TrackScreenViewEvent
import com.onepercentbetter.core.ui.TrackScrollJank
import com.onepercentbetter.core.ui.UserNewsResourcePreviewParameterProvider
import com.onepercentbetter.core.ui.launchCustomChromeTab
import com.onepercentbetter.core.ui.newsFeed

@Composable
internal fun RoutineScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineViewModel = hiltViewModel(),
) {
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val deepLinkedUserNewsResource by viewModel.deepLinkedNewsResource.collectAsStateWithLifecycle()

    RoutineScreen(
        isSyncing = isSyncing,
        feedState = feedState,
        deepLinkedUserNewsResource = deepLinkedUserNewsResource,
        onDeepLinkOpened = viewModel::onDeepLinkOpened,
        onTopicClick = onTopicClick,
        onNewsResourcesCheckedChanged = viewModel::updateNewsResourceSaved,
        onNewsResourceViewed = { viewModel.setNewsResourceViewed(it, true) },
        modifier = modifier,
    )
}

@Composable
internal fun RoutineScreen(
    isSyncing: Boolean,
    feedState: NewsFeedUiState,
    deepLinkedUserNewsResource: UserNewsResource?,
    onTopicClick: (String) -> Unit,
    onDeepLinkOpened: (String) -> Unit,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFeedLoading = feedState is NewsFeedUiState.Loading

    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isFeedLoading }

    val itemsAvailable = feedItemsSize(feedState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    TrackScrollJank(scrollableState = state, stateName = "routine:feed")

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        DaysRoutine(state)

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag("routine:feed"),
            state = state,
        ) {
            newsFeed(
                feedState = feedState,
                onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
            )

            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Add space for the content to clear the "offline" snackbar.
                    // TODO: Check that the Scaffold handles this correctly in OPBApp
                    // if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
        AnimatedVisibility(
            visible = isSyncing || isFeedLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_routine_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                OPBOverlayLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterHorizontally),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
    TrackScreenViewEvent(screenName = "Routine")
    NotificationPermissionEffect()
    DeepLinkEffect(
        deepLinkedUserNewsResource,
        onDeepLinkOpened,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DaysRoutine(state: LazyStaggeredGridState) {
    val carouselUncontainedState = rememberCarouselState(
        initialItem = 30,
    ) {
        30
    }
    HorizontalUncontainedCarousel(
        state = carouselUncontainedState,
        itemWidth = 80.dp,
        itemSpacing = 1.dp,
    ) { index ->
        Card(
            onClick = { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            // Use custom label for accessibility services to communicate button's action to user.
            // Pass null for action to only override the label and not the actual action.

//            modifier = modifier
//                .semantics {
//                    onClick(label = clickActionLabel, action = null)
//                }
//                .testTag("newsResourceCard:${userNewsResource.id}"),
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("S", style = MaterialTheme.typography.titleSmall)
                    val title = if (index == 29) "Hoje" else index.toString()
                    Text(title, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }

//        val pokemonEntity = state.pokemonMutableList[index]
//        LoadPokemonImage(
//            pokemonEntity = pokemonEntity,
//            modifier = Modifier
//                .maskClip(
//                    MaterialTheme.shapes.extraLarge,
//                ),
//        )
        //Paging
//        LaunchedEffect(key1 = true) {
//            if (!state.nextPage.isNullOrEmpty() && state.pokemonMutableList.size - 1 == index) {
//                pokemonListViewModel.requestToFetchPokemon(
//                    state.nextPage,
//                )
//            }
//        }
//        if (state.isLoading) StartDefaultLoader()
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews
    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return
    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
private fun DeepLinkEffect(
    userNewsResource: UserNewsResource?,
    onDeepLinkOpened: (String) -> Unit,
) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(userNewsResource) {
        if (userNewsResource == null) return@LaunchedEffect
        if (!userNewsResource.hasBeenViewed) onDeepLinkOpened(userNewsResource.id)

        launchCustomChromeTab(
            context = context,
            uri = Uri.parse(userNewsResource.url),
            toolbarColor = backgroundColor,
        )
    }
}

private fun feedItemsSize(
    feedState: NewsFeedUiState,
): Int {
    val feedSize = when (feedState) {
        NewsFeedUiState.Loading -> 0
        is Success -> feedState.feed.size
    }
    return feedSize
}

@DevicePreviews
@Composable
fun RoutineScreenPopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    OPBTheme {
        RoutineScreen(
            isSyncing = false,
            feedState = Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun RoutineScreenOfflinePopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    OPBTheme {
        RoutineScreen(
            isSyncing = false,
            feedState = Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun RoutineScreenTopicSelection(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    OPBTheme {
        RoutineScreen(
            isSyncing = false,
            feedState = Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun RoutineScreenLoading() {
    OPBTheme {
        RoutineScreen(
            isSyncing = false,
            feedState = NewsFeedUiState.Loading,
            deepLinkedUserNewsResource = null,
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun RoutineScreenPopulatedAndLoading(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    OPBTheme {
        RoutineScreen(
            isSyncing = true,
            feedState = Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}
