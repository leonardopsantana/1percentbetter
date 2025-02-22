package com.onepercentbetter.feature.routine

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells.Adaptive
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
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
import com.onepercentbetter.core.ui.TrackScreenViewEvent
import com.onepercentbetter.core.ui.TrackScrollJank
import com.onepercentbetter.core.ui.UserNewsResourcePreviewParameterProvider
import com.onepercentbetter.core.ui.conditional
import com.onepercentbetter.feature.routine.RoutineUiState.Loading
import com.onepercentbetter.feature.routine.RoutineUiState.Success
import java.time.LocalDate

@Composable
internal fun RoutineScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineViewModel = hiltViewModel(),
) {
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    RoutineScreen(
        isSyncing = isSyncing,
        feedState = feedState,
        onTopicClick = onTopicClick,
        onNewsResourcesCheckedChanged = viewModel::updateNewsResourceSaved,
        onNewsResourceViewed = { viewModel.setNewsResourceViewed(it, true) },
        modifier = modifier,
    )
}

@Composable
internal fun RoutineScreen(
    isSyncing: Boolean,
    feedState: RoutineUiState,
    onTopicClick: (String) -> Unit,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFeedLoading = feedState is RoutineUiState.Loading

    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isFeedLoading }

    val itemsAvailable = feedItemsSize(feedState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    TrackScrollJank(scrollableState = state, stateName = "routine:feed")
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {

        when (feedState) {
            Loading -> {
                RoutineLoading(isSyncing, isFeedLoading)
            }

            is Success -> {
                Column(
                    modifier = modifier
                        .fillMaxSize(),
                ) {
                    DaysRoutine(feedState.daysOfWeek)
                    RoutineLoaded(
                        state,
                        feedState,
                        onNewsResourcesCheckedChanged,
                        onNewsResourceViewed,
                        onTopicClick,
                    )
                }
            }
        }

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

        TrackScreenViewEvent(screenName = "Routine")
        NotificationPermissionEffect()
    }
}

@Composable
private fun RoutineLoaded(
    state: LazyStaggeredGridState,
    feedState: RoutineUiState,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = Adaptive(300.dp),
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
}

@Composable
private fun RoutineLoading(
    isSyncing: Boolean,
    isFeedLoading: Boolean,
) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DaysRoutine(daysOfWeek: List<LocalDate>) {
    HorizontalUncontainedCarousel(
        state = rememberCarouselState(initialItem = daysOfWeek.size - 1) { daysOfWeek.size },
        itemWidth = 90.dp,
    ) { index ->
        Card(
            onClick = {
                index.toString()
            },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            // Use custom label for accessibility services to communicate button's action to user.
            // Pass null for action to only override the label and not the actual action.
            modifier = Modifier
                .semantics {
                    onClick(label = index.toString(), action = null)
                },
        ) {
            val todayColor = MaterialTheme.colorScheme.primaryContainer
            Box(
                modifier = Modifier
                    .conditional(index == daysOfWeek.size - 1) {
                        background(todayColor)
                    }
                    .padding(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(daysOfWeek[index].dayOfWeek.toString()[0].toString(), style = MaterialTheme.typography.titleSmall)
                    Text(daysOfWeek[index].dayOfMonth.toString(), style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
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

private fun feedItemsSize(
    feedState: RoutineUiState,
): Int {
    val feedSize = when (feedState) {
        Loading -> 0
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
                daysOfWeek = emptyList()
            ),
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
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
                daysOfWeek = emptyList()
            ),
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
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
                daysOfWeek = emptyList()
            ),
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}

@DevicePreviews
@Composable
fun RoutineScreenLoading() {
    OPBTheme {
        RoutineScreen(
            isSyncing = false,
            feedState = RoutineUiState.Loading,
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
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
                daysOfWeek = emptyList()
            ),
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}
