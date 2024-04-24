package com.randos.musicvibe.presentation.screen.track

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.music_player.presentation.screen.music_player.MusicPlayer
import com.randos.musicvibe.data.AudioFile
import com.randos.musicvibe.data.toMusicPlayerFile
import com.randos.musicvibe.presentation.component.AlphabetSlider
import com.randos.musicvibe.presentation.component.MusicItem

data class TrackScreenUiState(
    val audioFiles: List<AudioFile> = emptyList(),
    val defaultMusicThumbnail: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888),
    val selectedIndex: Int = 0,
    val selectedAudioFile: AudioFile? = null
)

@Composable
fun TrackScreen(
    viewModel: TrackViewModel = hiltViewModel<TrackViewModel>()
) {
    val uiState by viewModel.uiState.observeAsState(initial = TrackScreenUiState())
    val lazyListState = rememberLazyListState()
    var selectedFileThumbnail by remember{ mutableStateOf(uiState.defaultMusicThumbnail) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp),
        ) {
            items(uiState.audioFiles) { audioFile ->
                MusicItem(
                    audioFile = audioFile,
                    defaultMusicThumbnail = uiState.defaultMusicThumbnail,
                    onClick = {
                        selectedFileThumbnail = it
                        viewModel.updateSelectedAudioFile(audioFile)
                    }
                )
            }
        }
        AlphabetSlider(modifier = Modifier.align(Alignment.CenterEnd)) {
            viewModel.updateSelectedIndex(it)
        }
    }

    LaunchedEffect(key1 = uiState.selectedIndex) {
        lazyListState.scrollToItem(uiState.selectedIndex)
    }

    uiState.selectedAudioFile?.apply {
        MusicPlayer(this.toMusicPlayerFile(selectedFileThumbnail))
    }

    BackHandler(uiState.selectedAudioFile != null) {
        viewModel.updateSelectedAudioFile(null)
    }

}

@Preview
@Composable
fun PreviewTrackScreen() {
    TrackScreen()
}