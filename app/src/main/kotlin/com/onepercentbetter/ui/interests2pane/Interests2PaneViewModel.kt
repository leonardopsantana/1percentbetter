

package com.onepercentbetter.ui.interests2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.onepercentbetter.feature.interests.navigation.InterestsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

const val TOPIC_ID_KEY = "selectedTopicId"

@HiltViewModel
class Interests2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val route = savedStateHandle.toRoute<InterestsRoute>()
    val selectedTopicId: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = TOPIC_ID_KEY,
        initialValue = route.initialTopicId,
    )

    fun onTopicClick(topicId: String?) {
        savedStateHandle[TOPIC_ID_KEY] = topicId
    }
}
