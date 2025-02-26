

package com.onepercentbetter.feature.bookmarks

import com.onepercentbetter.core.data.repository.CompositeUserNewsResourceRepository
import com.onepercentbetter.core.testing.data.newsResourcesTestData
import com.onepercentbetter.core.testing.repository.TestNewsRepository
import com.onepercentbetter.core.testing.repository.TestUserDataRepository
import com.onepercentbetter.core.testing.util.MainDispatcherRule
import com.onepercentbetter.core.ui.NewsFeedUiState.Loading
import com.onepercentbetter.core.ui.NewsFeedUiState.Success
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class BookmarksViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val newsRepository = TestNewsRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )
    private lateinit var viewModel: BookmarksViewModel

    @Before
    fun setup() {
        viewModel = BookmarksViewModel(
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userNewsResourceRepository,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.feedUiState.value)
    }

    @Test
    fun oneBookmark_showsInFeed() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }

        newsRepository.sendNewsResources(newsResourcesTestData)
        userDataRepository.setNewsResourceBookmarked(newsResourcesTestData[0].id, true)
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(item.feed.size, 1)
    }

    @Test
    fun oneBookmark_whenRemoving_removesFromFeed() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }
        // Set the news resources to be used by this test
        newsRepository.sendNewsResources(newsResourcesTestData)
        // Start with the resource saved
        userDataRepository.setNewsResourceBookmarked(newsResourcesTestData[0].id, true)
        // Use viewModel to remove saved resource
        viewModel.removeFromSavedResources(newsResourcesTestData[0].id)
        // Verify list of saved resources is now empty
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(item.feed.size, 0)
        assertTrue(viewModel.shouldDisplayUndoBookmark)
    }

    @Test
    fun feedUiState_resourceIsViewed_setResourcesViewed() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }

        // Given
        newsRepository.sendNewsResources(newsResourcesTestData)
        userDataRepository.setNewsResourceBookmarked(newsResourcesTestData[0].id, true)
        val itemBeforeViewed = viewModel.feedUiState.value
        assertIs<Success>(itemBeforeViewed)
        assertFalse(itemBeforeViewed.feed.first().hasBeenViewed)

        // When
        viewModel.setNewsResourceViewed(newsResourcesTestData[0].id, true)

        // Then
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertTrue(item.feed.first().hasBeenViewed)
    }

    @Test
    fun feedUiState_undoneBookmarkRemoval_bookmarkIsRestored() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }

        // Given
        newsRepository.sendNewsResources(newsResourcesTestData)
        userDataRepository.setNewsResourceBookmarked(newsResourcesTestData[0].id, true)
        viewModel.removeFromSavedResources(newsResourcesTestData[0].id)
        assertTrue(viewModel.shouldDisplayUndoBookmark)
        val itemBeforeUndo = viewModel.feedUiState.value
        assertIs<Success>(itemBeforeUndo)
        assertEquals(0, itemBeforeUndo.feed.size)

        // When
        viewModel.undoBookmarkRemoval()

        // Then
        assertFalse(viewModel.shouldDisplayUndoBookmark)
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(1, item.feed.size)
    }
}
