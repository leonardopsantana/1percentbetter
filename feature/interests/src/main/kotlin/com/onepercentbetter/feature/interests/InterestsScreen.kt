

package com.onepercentbetter.feature.interests

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onepercentbetter.core.designsystem.component.OPBBackground
import com.onepercentbetter.core.designsystem.component.OPBLoadingWheel
import com.onepercentbetter.core.designsystem.theme.OPBTheme
import com.onepercentbetter.core.model.data.FollowableTopic
import com.onepercentbetter.core.ui.DevicePreviews
import com.onepercentbetter.core.ui.FollowableTopicPreviewParameterProvider
import com.onepercentbetter.core.ui.TrackScreenViewEvent
import com.onepercentbetter.feature.interests.InterestsUiState.Empty
import com.onepercentbetter.feature.interests.InterestsUiState.Interests
import com.onepercentbetter.feature.interests.InterestsUiState.Loading

@Composable
fun InterestsRoute(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    highlightSelectedTopic: Boolean = false,
    viewModel: InterestsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InterestsScreen(
        uiState = uiState,
        followTopic = viewModel::followTopic,
        onTopicClick = {
            viewModel.onTopicClick(it)
            onTopicClick(it)
        },
        highlightSelectedTopic = highlightSelectedTopic,
        modifier = modifier,
    )
}

@Composable
internal fun InterestsScreen(
    uiState: InterestsUiState,
    followTopic: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    highlightSelectedTopic: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            Loading ->
                OPBLoadingWheel(
                    modifier = modifier,
                    contentDesc = stringResource(id = R.string.feature_interests_loading),
                )

            is Interests ->
                TopicsTabContent(
                    topics = uiState.topics,
                    onTopicClick = onTopicClick,
                    onFollowButtonClick = followTopic,
                    selectedTopicId = uiState.selectedTopicId,
                    highlightSelectedTopic = highlightSelectedTopic,
                    modifier = modifier,
                )

            is Empty -> InterestsEmptyScreen()
        }
    }
    TrackScreenViewEvent(screenName = "Interests")
}

@Composable
private fun InterestsEmptyScreen() {
    Text(text = stringResource(id = R.string.feature_interests_empty_header))
}

@DevicePreviews
@Composable
fun InterestsScreenPopulated(
    @PreviewParameter(FollowableTopicPreviewParameterProvider::class)
    followableTopics: List<FollowableTopic>,
) {
    OPBTheme {
        OPBBackground {
            InterestsScreen(
                uiState = Interests(
                    selectedTopicId = null,
                    topics = followableTopics,
                ),
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenLoading() {
    OPBTheme {
        OPBBackground {
            InterestsScreen(
                uiState = Loading,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenEmpty() {
    OPBTheme {
        OPBBackground {
            InterestsScreen(
                uiState = Empty,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}
