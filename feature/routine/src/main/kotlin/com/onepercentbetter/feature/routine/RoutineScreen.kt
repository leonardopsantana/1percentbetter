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
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
        var selectedIndex by remember { mutableIntStateOf(-1) }

        daysOfWeek.forEachIndexed { index, day ->
            CardDay(
                index = index,
                day = day,
                isSelected = index == selectedIndex || (selectedIndex == -1 && index == daysOfWeek.size - 1),
                onCardClick = { newIndex ->
                    selectedIndex =
                        if (selectedIndex == newIndex) selectedIndex else newIndex
                },
            )
        }
    }
}

@Composable
private fun CardDay(
    index: Int,
    day: LocalDate,
    isSelected: Boolean,
    onCardClick: (Int) -> Unit,
) {
    Card(
        onClick = { onCardClick(index) },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .padding(4.dp)
            .clickable { onCardClick(index) },
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(48.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    day.dayOfWeek.toString().take(3).lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                )
                HorizontalDivider(
                    thickness = 10.dp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    day.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
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
