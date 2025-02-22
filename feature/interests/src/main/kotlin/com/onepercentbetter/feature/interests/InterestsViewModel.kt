

package com.onepercentbetter.feature.interests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.onepercentbetter.core.data.repository.UserDataRepository
import com.onepercentbetter.core.model.data.FollowableTopic
import com.onepercentbetter.feature.interests.navigation.InterestsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val userDataRepository: UserDataRepository
) : ViewModel() {

    // Key used to save and retrieve the currently selected topic id from saved state.
    private val selectedTopicIdKey = "selectedTopicIdKey"

    private val interestsRoute: InterestsRoute = savedStateHandle.toRoute()
    private val selectedTopicId = savedStateHandle.getStateFlow(
        key = selectedTopicIdKey,
        initialValue = interestsRoute.initialTopicId,
    )

//    val uiState: StateFlow<InterestsUiState> = combine(
//        selectedTopicId,
//        getFollowableTopics(sortBy = TopicSortField.NAME),
//        InterestsUiState::Interests,
//    ).stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = Loading,
//    )

    fun followTopic(followedTopicId: String, followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(followedTopicId, followed)
        }
    }

    fun onTopicClick(topicId: String?) {
        savedStateHandle[selectedTopicIdKey] = topicId
    }
}

sealed interface InterestsUiState {
    data object Loading : InterestsUiState

    data class Interests(
        val selectedTopicId: String?,
        val topics: List<FollowableTopic>,
    ) : InterestsUiState

    data object Empty : InterestsUiState
}
