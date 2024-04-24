package com.randos.musicvibe.presentation.screen.track

import android.graphics.Bitmap
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.musicvibe.data.AudioFile
import com.randos.core.navigation.NavigationDestination
import com.randos.musicvibe.presentation.component.AlphabetSlider
import com.randos.musicvibe.presentation.component.MusicItem

object TrackScreenNavigationDestination : NavigationDestination {
    override val name: String = "Track"
    override val route: String = "/track"
}

data class TrackScreenUiState(
    val audioFiles: List<AudioFile> = emptyList(),
    val defaultMusicThumbnail: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888),
    val selectedIndex: Int? = 0,
    val selectedAudioFile: AudioFile? = null
)

@Composable
fun TrackScreen(
    viewModel: TrackViewModel = hiltViewModel<TrackViewModel>(),
    onItemClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.observeAsState(initial = TrackScreenUiState())
    val lazyListState = rememberLazyListState()

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
                        viewModel.updateSelectedAudioFile(audioFile)
                        onItemClick(audioFile.path)
                    }
                )
            }
        }
        AlphabetSlider(modifier = Modifier.align(Alignment.CenterEnd),
            onSelectionChange = { viewModel.updateSelectedIndex(it) },
            onSelectionChangeFinished = {viewModel.updateSelectedIndexFinished()})
    }

    LaunchedEffect(key1 = uiState.selectedIndex) {
        uiState.selectedIndex?.let { lazyListState.scrollToItem(it) }
    }
}

@Preview
@Composable
fun PreviewTrackScreen() {
    TrackScreen(onItemClick = {})
}