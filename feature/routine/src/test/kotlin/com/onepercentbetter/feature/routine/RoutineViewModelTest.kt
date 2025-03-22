package com.onepercentbetter.feature.routine

import com.onepercentbetter.core.testing.repository.TestTaskRepository
import com.onepercentbetter.core.testing.repository.TestUserDataRepository
import com.onepercentbetter.core.testing.util.MainDispatcherRule
import com.onepercentbetter.core.testing.util.TestAnalyticsHelper
import com.onepercentbetter.core.testing.util.TestSyncManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class RoutineViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()
    private val analyticsHelper = TestAnalyticsHelper()
    private val userDataRepository = TestUserDataRepository()
    private val taskRepository = TestTaskRepository()
    private lateinit var viewModel: RoutineViewModel

    @Before
    fun setup() {
        viewModel = RoutineViewModel(
            syncManager = syncManager,
            analyticsHelper = analyticsHelper,
            taskRepository = taskRepository,
            userDataRepository = userDataRepository,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(RoutineUiState.Loading, viewModel.feedState.value)
    }

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoInterests() = runTest {
        syncManager.setSyncing(true)

        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            true,
            viewModel.isSyncing.value,
        )
    }

//    @Test
//    fun topicSelectionUpdatesAfterSelectingTopic() = runTest {
//        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }
//
//        topicsRepository.sendTopics(sampleTopics)
//        userDataRepository.setFollowedTopicIds(emptySet())
//        taskRepository.sendNewsResources(sampleNewsResources)
//
//        assertEquals(
//            RoutineUiState.Success(
//                feed = emptyList(),
//            ),
//            viewModel.feedState.value,
//        )
//
//        val followedTopicId = sampleTopics[1].id
//        viewModel.updateTopicSelection(followedTopicId, isChecked = true)
//
//        val userData = emptyUserData.copy(followedTopics = setOf(followedTopicId))
//
//        assertEquals(
//            RoutineUiState.Success(
//                feed = listOf(
//                    TaskModel(sampleNewsResources[1], userData),
//                    TaskModel(sampleNewsResources[2], userData),
//                ),
//            ),
//            viewModel.feedState.value,
//        )
//    }
}
