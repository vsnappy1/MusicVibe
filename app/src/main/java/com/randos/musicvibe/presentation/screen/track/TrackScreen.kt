package com.randos.musicvibe.presentation.screen.track

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.musicvibe.data.AudioFile
import com.randos.musicvibe.utils.IoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class TrackScreenUiState(
    val audioFiles: List<AudioFile> = emptyList(),
    val defaultMusicThumbnail: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
)

@Composable
fun TrackScreen(
    viewModel: TrackViewModel = hiltViewModel<TrackViewModel>()
) {
    val uiState by viewModel.uiState.observeAsState(initial = TrackScreenUiState())
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.audioFiles) { audioFile ->
            MusicItem(audioFile = audioFile, uiState.defaultMusicThumbnail)
        }
    }
}

@Composable
fun MusicItem(audioFile: AudioFile, defaultMusicThumbnail: Bitmap) {
    var bitmap by remember { mutableStateOf(defaultMusicThumbnail) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            bitmap = IoUtils.getAlbumImage(audioFile.path) ?: defaultMusicThumbnail
        }
    }

    Row {
        Image(
            bitmap = bitmap.asImageBitmap(), contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = audioFile.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audioFile.artist,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun PreviewTrackScreen() {
    TrackScreen()
}