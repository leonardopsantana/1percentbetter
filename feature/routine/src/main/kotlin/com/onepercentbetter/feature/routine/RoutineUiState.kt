package com.onepercentbetter.feature.routine

import com.onepercentbetter.core.model.data.TaskModel
import com.onepercentbetter.core.model.data.TaskWithCategoryModel
import java.time.LocalDate

/**
 * A sealed hierarchy describing the state of the feed of routine.
 */
sealed interface RoutineUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : RoutineUiState

    /**
     * The feed is loaded with the given list of routine.
     */
    data class Success(
        /**
         * The list of routine of the user.
         */
        val feed: List<TaskWithCategoryModel>,
        val daysOfWeek: List<LocalDate>
    ) : RoutineUiState
}