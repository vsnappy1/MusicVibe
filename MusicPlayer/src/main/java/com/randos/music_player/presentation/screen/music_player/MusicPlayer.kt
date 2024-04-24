package com.randos.music_player.presentation.screen.music_player


import android.app.Activity
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.media3.common.MediaItem
import androidx.media3.common.util.RepeatModeUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.randos.core.navigation.NavigationDestinationWithParams
import com.randos.core.data.model.MusicFile
import com.randos.core.utils.Utils
import com.randos.music_player.di.MusicVibeExoPlayer
import com.randos.music_player.presentation.component.MusicPlaybackController
import com.randos.music_player.presentation.component.MusicPlaybackControllerState
import com.randos.music_player.presentation.component.RepeatMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object MusicPlayerNavigationDestination : NavigationDestinationWithParams {
    override val name: String = "Music Player"
    override val route: String = "/music_player"
    override val argument: String = "filePath"
}

@OptIn(UnstableApi::class)
@Composable
fun MusicPlayer(
    path: String,
    activity: Activity = LocalContext.current as Activity,
) {
    var musicFile by remember { mutableStateOf(MusicFile.default()) }
    val viewModel: MusicPlayerViewModel = hiltViewModel()
    val state by viewModel.uiState.observeAsState(
        MusicPlaybackControllerState(
            trackLength = musicFile.duration
        )
    )
    val exoPlayer: ExoPlayer = remember { MusicVibeExoPlayer.getInstance(activity) }
    val mediaSource = remember(path) { MediaItem.fromUri(path) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(mediaSource) {
        musicFile = Utils.getMusicFile(context, path) ?: MusicFile.default()
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
        viewModel.setTrackLength(musicFile.duration)
        delay(100) // Some delay before we start playing music.
        viewModel.onFirstLaunch()

        /**
         * Update the seek bar position as playback continues.
         */
        coroutineScope.launch {
            while (true) {
                delay(250)
                viewModel.onSeekPositionChange(exoPlayer.currentPosition)
            }
        }
    }

    /**
     * Trigger play and pause function on exoPlayer based on state.
     */
    LaunchedEffect(key1 = state.isPlaying) {
        state.apply {
            if (isPlaying) {
                exoPlayer.play()
            } else {
                exoPlayer.pause()
            }
        }
    }

    /**
     * Enable/Disable shuffle mode based on state.
     */
    LaunchedEffect(key1 = state.isShuffleOn) {
        exoPlayer.shuffleModeEnabled = state.isShuffleOn
    }

    /**
     * Update repeat mode of exoPlayer based on state.
     */
    LaunchedEffect(key1 = state.repeatMode) {
        exoPlayer.repeatMode = when (state.repeatMode) {
            RepeatMode.NONE -> RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE
            RepeatMode.ALL -> RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL
            RepeatMode.ONE -> RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        PreviewImageTitleArtist(
            modifier = Modifier.align(Alignment.Center),
            previewImage = musicFile.previewImage,
            title = musicFile.title,
            artist = musicFile.artist
        )

        MusicPlaybackController(
            modifier = Modifier.align(Alignment.BottomCenter),
            state = state,
            onPlayPauseClick = { viewModel.onPlayPauseClick() },
            onShuffleClick = { viewModel.onShuffleClick() },
            onRepeatModeClick = { viewModel.onRepeatModeClick() },
            onNextClick = { viewModel.onNextClick() },
            onPreviousClick = { viewModel.onPreviousClick() },
            onValueChangeFinished = {
                viewModel.onSeekPositionChangeFinished(it)
                exoPlayer.seekTo(it)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
//            exoPlayer.release()
            exoPlayer.stop()
        }
    }
}

@Composable
private fun PreviewImageTitleArtist(
    modifier: Modifier = Modifier,
    previewImage: Bitmap,
    title: String,
    artist: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = previewImage.asImageBitmap(), contentDescription = "Preview Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
        )
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



