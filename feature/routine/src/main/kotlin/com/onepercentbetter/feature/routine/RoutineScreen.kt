package com.onepercentbetter.feature.routine

import android.Manifest.permission
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells.Adaptive
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import com.onepercentbetter.core.designsystem.component.DynamicAsyncImage
import com.onepercentbetter.core.designsystem.component.OPBIconToggleButton
import com.onepercentbetter.core.designsystem.component.OPBOverlayLoadingWheel
import com.onepercentbetter.core.designsystem.component.scrollbar.DraggableScrollbar
import com.onepercentbetter.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.onepercentbetter.core.designsystem.component.scrollbar.scrollbarState
import com.onepercentbetter.core.designsystem.icon.OPBIcons
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetter.core.model.data.TaskWithCategoryModel
import com.onepercentbetter.core.ui.TrackScreenViewEvent
import com.onepercentbetter.core.ui.TrackScrollJank
import com.onepercentbetter.core.ui.conditional
import com.onepercentbetter.feature.routine.RoutineUiState.Loading
import com.onepercentbetter.feature.routine.RoutineUiState.Success
import kotlinx.coroutines.launch
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
        onNewsResourcesCheckedChanged = { _, _ -> },
        onNewsResourceViewed = { },
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
    val isFeedLoading = feedState is Loading

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
            orientation = Vertical,
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
    feedState: Success,
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
        items(
            items = feedState.feed,
            key = { it.task.id },
            contentType = { "routineItem" },
        ) { feedItem ->
            RoutineItem(
                name = feedItem.task.id,
                topicId = "",
                imageUrl = "",
                isSelected = false,
                onClick = { _, _ ->
                },
            )
        }
    }
}

@Composable
private fun RoutineItem(
    name: String,
    topicId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit,
) {
//    Surface(
//        modifier = Modifier
//            .width(312.dp)
//            .heightIn(min = 56.dp),
//        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
//        color = MaterialTheme.colorScheme.surface,
//        selected = isSelected,
//        onClick = {
//            onClick(topicId, !isSelected)
//        },
//    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 12.dp, end = 8.dp),
    ) {
        TopicIcon(
            imageUrl = imageUrl,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
        )
        OPBIconToggleButton(
            checked = isSelected,
            onCheckedChange = { checked -> onClick(topicId, checked) },
            icon = {
                Icon(
                    imageVector = OPBIcons.Close,
                    contentDescription = name,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = OPBIcons.CheckFilled,
                    contentDescription = name,
                )
            },
        )
    }
//    }
}

@Composable
fun TopicIcon(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    DynamicAsyncImage(
        placeholder = painterResource(R.drawable.feature_foryou_ic_icon_placeholder),
        imageUrl = imageUrl,
        // decorative
        contentDescription = null,
        modifier = modifier
            .padding(10.dp)
            .size(32.dp),
    )
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

@Composable
private fun DaysRoutine(daysOfWeek: List<LocalDate>) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    Row(
        modifier = Modifier.horizontalScroll(scrollState),
    ) {
        daysOfWeek.forEachIndexed { index, day ->
            CardDay(index, daysOfWeek, day)
        }
    }
}

@Composable
private fun CardDay(
    index: Int,
    daysOfWeek: List<LocalDate>,
    day: LocalDate,
) {
    Card(
        onClick = {
            index.toString()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // Use custom label for accessibility services to communicate button's action to user.
        // Pass null for action to only override the label and not the actual action.
        modifier = Modifier
            .padding(4.dp)
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
                Text(
                    day.dayOfWeek.toString()[0].toString(),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    day.dayOfMonth.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                )
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
        permission.POST_NOTIFICATIONS,
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

@Composable
fun RoutineScreenPopulatedFeed(
    taskModels: List<TaskWithCategoryModel>,
) {
    OPBTheme {
        RoutineScreen(
            isSyncing = false,
            feedState = Success(
                feed = taskModels,
                daysOfWeek = emptyList(),
            ),
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}
