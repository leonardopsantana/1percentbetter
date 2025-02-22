package com.onepercentbetter.feature.routine

import com.onepercentbetter.core.model.data.UserNewsResource
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
        val feed: List<UserNewsResource>,
        val daysOfWeek: List<LocalDate>
    ) : RoutineUiState
}