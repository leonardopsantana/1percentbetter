package com.onepercentbetter.feature.routine

import com.onepercentbetter.core.data.repository.task.TaskRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onepercentbetter.core.data.repository.user.UserDataRepository
import com.onepercentbetter.core.data.util.SyncManager
import com.onepercentbetter.feature.routine.RoutineUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    syncManager: SyncManager,
    private val userDataRepository: UserDataRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val daysOfWeek: List<LocalDate>
        get() {
            return (0..6).map { LocalDate.now().minusDays(it.toLong()) }.reversed()
        }

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    private val _feedState = MutableStateFlow<RoutineUiState>(Loading)
    val feedState: StateFlow<RoutineUiState> = _feedState

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        viewModelScope.launch {
            taskRepository.getTasksForDate(System.now()).map {
                _feedState.value = RoutineUiState.Success(it, daysOfWeek)
            }
        }
    }
}
