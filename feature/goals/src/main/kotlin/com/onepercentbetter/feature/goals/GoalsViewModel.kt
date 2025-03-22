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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.onepercentbetter.core.data.repository.category.CategoryRepository
import com.onepercentbetter.core.data.repository.user.UserDataRepository
import com.onepercentbetter.core.model.data.TaskModel
import com.onepercentbetter.feature.goals.GoalsUiState.Loading
import com.onepercentbetter.feature.goals.navigation.GoalsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDataRepository: UserDataRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val goalId = savedStateHandle.toRoute<GoalsRoute>()

    private val _goalsState = MutableStateFlow<GoalsUiState>(Loading)
    val goalsState: StateFlow<GoalsUiState> = _goalsState
}

sealed interface GoalsUiState {
    data class Success(val news: List<TaskModel>) : GoalsUiState
    data object Error : GoalsUiState
    data object Loading : GoalsUiState
}
