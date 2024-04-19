package com.randos.musicvibe.presentation.screen.track

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.musicvibe.data.AudioFile

data class TrackScreenUiState(
    val audioFiles: List<AudioFile> = emptyList()
)

@Composable
fun TrackScreen(
    viewModel: TrackViewModel = hiltViewModel<TrackViewModel>()
) {
    val uiState by viewModel.uiState.observeAsState(initial = TrackScreenUiState())
    LazyColumn {
        items(uiState.audioFiles){audio ->
            Text(text = audio.title)
        }
    }
}


@Preview
@Composable
fun PreviewTrackScreen() {
    TrackScreen()
}