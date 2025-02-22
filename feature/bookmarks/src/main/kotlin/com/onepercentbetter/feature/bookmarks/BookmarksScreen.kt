

package com.onepercentbetter.feature.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun BookmarksRoute(
    onTopicClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel(),
) {
//    val feedState by viewModel.feedUiState.collectAsStateWithLifecycle()
//    BookmarksScreen(
//        feedState = feedState,
//        onShowSnackbar = onShowSnackbar,
//        removeFromBookmarks = viewModel::removeFromSavedResources,
//        onNewsResourceViewed = { viewModel.setNewsResourceViewed(it, true) },
//        onTopicClick = onTopicClick,
//        modifier = modifier,
//        shouldDisplayUndoBookmark = viewModel.shouldDisplayUndoBookmark,
//        undoBookmarkRemoval = viewModel::undoBookmarkRemoval,
//        clearUndoState = viewModel::clearUndoState,
//    )
}

///**
// * Displays the user's bookmarked articles. Includes support for loading and empty states.
// */
//@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//@Composable
//internal fun BookmarksScreen(
//    feedState: NewsFeedUiState,
//    onShowSnackbar: suspend (String, String?) -> Boolean,
//    removeFromBookmarks: (String) -> Unit,
//    onNewsResourceViewed: (String) -> Unit,
//    onTopicClick: (String) -> Unit,
//    modifier: Modifier = Modifier,
//    shouldDisplayUndoBookmark: Boolean = false,
//    undoBookmarkRemoval: () -> Unit = {},
//    clearUndoState: () -> Unit = {},
//) {
//    val bookmarkRemovedMessage = stringResource(id = R.string.feature_bookmarks_removed)
//    val undoText = stringResource(id = R.string.feature_bookmarks_undo)
//
//    LaunchedEffect(shouldDisplayUndoBookmark) {
//        if (shouldDisplayUndoBookmark) {
//            val snackBarResult = onShowSnackbar(bookmarkRemovedMessage, undoText)
//            if (snackBarResult) {
//                undoBookmarkRemoval()
//            } else {
//                clearUndoState()
//            }
//        }
//    }
//
//    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
//        clearUndoState()
//    }
//
//    when (feedState) {
//        Loading -> LoadingState(modifier)
//        is Success -> if (feedState.feed.isNotEmpty()) {
//            BookmarksGrid(
//                feedState,
//                removeFromBookmarks,
//                onNewsResourceViewed,
//                onTopicClick,
//                modifier,
//            )
//        } else {
//            EmptyState(modifier)
//        }
//    }
//
//    TrackScreenViewEvent(screenName = "Saved")
//}
//
//@Composable
//private fun LoadingState(modifier: Modifier = Modifier) {
//    OPBLoadingWheel(
//        modifier = modifier
//            .fillMaxWidth()
//            .wrapContentSize()
//            .testTag("routine:loading"),
//        contentDesc = stringResource(id = R.string.feature_bookmarks_loading),
//    )
//}
//
//@Composable
//private fun BookmarksGrid(
//    feedState: NewsFeedUiState,
//    removeFromBookmarks: (String) -> Unit,
//    onNewsResourceViewed: (String) -> Unit,
//    onTopicClick: (String) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val scrollableState = rememberLazyStaggeredGridState()
//    TrackScrollJank(scrollableState = scrollableState, stateName = "bookmarks:grid")
//    Box(
//        modifier = modifier
//            .fillMaxSize(),
//    ) {
//        LazyVerticalStaggeredGrid(
//            columns = StaggeredGridCells.Adaptive(300.dp),
//            contentPadding = PaddingValues(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalItemSpacing = 24.dp,
//            state = scrollableState,
//            modifier = Modifier
//                .fillMaxSize()
//                .testTag("bookmarks:feed"),
//        ) {
//            newsFeed(
//                feedState = feedState,
//                onNewsResourcesCheckedChanged = { id, _ -> removeFromBookmarks(id) },
//                onNewsResourceViewed = onNewsResourceViewed,
//                onTopicClick = onTopicClick,
//            )
//            item(span = StaggeredGridItemSpan.FullLine) {
//                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
//            }
//        }
//        val itemsAvailable = when (feedState) {
//            Loading -> 1
//            is Success -> feedState.feed.size
//        }
//        val scrollbarState = scrollableState.scrollbarState(
//            itemsAvailable = itemsAvailable,
//        )
//        scrollableState.DraggableScrollbar(
//            modifier = Modifier
//                .fillMaxHeight()
//                .windowInsetsPadding(WindowInsets.systemBars)
//                .padding(horizontal = 2.dp)
//                .align(Alignment.CenterEnd),
//            state = scrollbarState,
//            orientation = Orientation.Vertical,
//            onThumbMoved = scrollableState.rememberDraggableScroller(
//                itemsAvailable = itemsAvailable,
//            ),
//        )
//    }
//}
//
//@Composable
//private fun EmptyState(modifier: Modifier = Modifier) {
//    Column(
//        modifier = modifier
//            .padding(16.dp)
//            .fillMaxSize()
//            .testTag("bookmarks:empty"),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        val iconTint = LocalTintTheme.current.iconTint
//        Image(
//            modifier = Modifier.fillMaxWidth(),
//            painter = painterResource(id = R.drawable.feature_bookmarks_img_empty_bookmarks),
//            colorFilter = if (iconTint != Color.Unspecified) ColorFilter.tint(iconTint) else null,
//            contentDescription = null,
//        )
//
//        Spacer(modifier = Modifier.height(48.dp))
//
//        Text(
//            text = stringResource(id = R.string.feature_bookmarks_empty_error),
//            modifier = Modifier.fillMaxWidth(),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold,
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = stringResource(id = R.string.feature_bookmarks_empty_description),
//            modifier = Modifier.fillMaxWidth(),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.bodyMedium,
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun LoadingStatePreview() {
//    OPBTheme {
//        LoadingState()
//    }
//}
//
//@Preview
//@Composable
//private fun BookmarksGridPreview(
//    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
//    userNewsResources: List<UserNewsResource>,
//) {
//    OPBTheme {
//        BookmarksGrid(
//            feedState = Success(userNewsResources),
//            removeFromBookmarks = {},
//            onNewsResourceViewed = {},
//            onTopicClick = {},
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun EmptyStatePreview() {
//    OPBTheme {
//        EmptyState()
//    }
//}
