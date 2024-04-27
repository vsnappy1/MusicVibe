package com.randos.music_player.presentation.screen.music_player


import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.randos.core.data.model.MusicFile
import com.randos.core.navigation.NavigationDestinationWithParams
import com.randos.core.utils.Utils
import com.randos.music_player.presentation.component.MusicPlaybackController
import com.randos.music_player.presentation.component.MusicPlaybackControllerState

object MusicPlayerNavigationDestination : NavigationDestinationWithParams {
    override val name: String = "Music Player"
    override val route: String = "/music_player"
    override val param: String = "index"
}

internal data class MusicPlayerState(
    val index: Int = 0,
    val currentTrack: MusicFile = MusicFile.default(),
    val controllerState: MusicPlaybackControllerState = MusicPlaybackControllerState()
)

@OptIn(UnstableApi::class)
@Composable
fun MusicPlayer() {
    val viewModel: MusicPlayerViewModel = hiltViewModel()
    val state by viewModel.uiState.observeAsState(MusicPlayerState())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {


        PreviewImageTitleArtist(
            modifier = Modifier.align(Alignment.Center),
            title = state.currentTrack.title,
            artist = state.currentTrack.artist,
            path = state.currentTrack.path
        )

        MusicPlaybackController(
            modifier = Modifier.align(Alignment.BottomCenter),
            state = state.controllerState,
            onPlayPauseClick = { viewModel.onPlayPauseClick() },
            onShuffleClick = { viewModel.onShuffleClick() },
            onRepeatModeClick = { viewModel.onRepeatModeClick() },
            onNextClick = { viewModel.onNextClick() },
            onPreviousClick = { viewModel.onPreviousClick() },
            onValueChangeFinished = {
                viewModel.onSeekPositionChangeFinished(it)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
        }
    }
}

@Composable
private fun PreviewImageTitleArtist(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    path: String
) {

    val context = LocalContext.current
    val defaultThumbnail by remember { mutableStateOf(Utils.getDefaultThumbnail(context)) }
    var previewImage by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LaunchedEffect(path) {
            previewImage = Utils.getAlbumImage(path) ?: defaultThumbnail
        }

        previewImage?.let {
            Image(
                bitmap = it.asImageBitmap(), contentDescription = "Preview Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}



