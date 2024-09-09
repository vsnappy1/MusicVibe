package com.randos.musicvibe.presentation.screen.track

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.core.data.MusicScanner
import com.randos.core.data.model.MusicFile
import com.randos.core.navigation.NavigationDestination
import com.randos.core.presentation.theme.seed
import com.randos.core.utils.PermissionManager.getMediaReadPermissionString
import com.randos.core.utils.PermissionManager.isMediaReadPermissionGranted
import com.randos.core.utils.Utils
import com.randos.core.utils.defaultPadding
import com.randos.logger.Logger
import com.randos.music_player.presentation.component.MusicPlaybackButtons
import com.randos.music_player.presentation.screen.music_player.MusicPlayer
import com.randos.music_player.presentation.screen.music_player.MusicPlayerState
import com.randos.music_player.presentation.screen.music_player.MusicPlayerViewModel
import com.randos.musicvibe.BuildConfig
import com.randos.musicvibe.presentation.component.AlphabetSlider
import com.randos.musicvibe.presentation.component.MusicItem
import kotlinx.coroutines.delay

object TrackScreenNavigationDestination : NavigationDestination {
    override val name: String = "Track"
    override val route: String = "/track"
}

data class TrackScreenUiState(
    val musicFiles: List<MusicFile> = emptyList(),
    val selectedIndex: Int? = 0,
)

/**
 * Screen to represent all tracks present on device.
 *
 * [TrackScreen] and [MusicPlayer] uses the same data source, that is [MusicScanner].
 */

@Composable
fun TrackScreen(
    onBottomPlayerClick: () -> Unit,
    viewModel: TrackViewModel = hiltViewModel<TrackViewModel>(),
) {

    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    val uiState by viewModel.uiState.observeAsState(initial = TrackScreenUiState())
    val defaultMusicThumbnail by remember { mutableStateOf(Utils.getDefaultThumbnail(context)) }

    val musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
    val musicPlayerState by musicPlayerViewModel.uiState.observeAsState(MusicPlayerState())

    var isMediaReadPermissionGranted by remember { mutableStateOf(false) }
    val mediaReadRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            isMediaReadPermissionGranted = granted
            if (granted) {
                Logger.i(context, "Media Read permission granted")
                musicPlayerViewModel.rescan()
                viewModel.rescan()
            } else {
                Logger.i(context, "Media Read permission denied")
            }
        }
    isMediaReadPermissionGranted = context.isMediaReadPermissionGranted()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .defaultPadding(start = 0.dp, end = 0.dp),
        contentAlignment = Alignment.Center
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, bottom = 30.dp),
        ) {
            uiState.musicFiles.let { items ->
                items(
                    count = items.size,
                    key = { items[it].id }) { index ->
                    MusicItem(
                        musicFile = items[index],
                        defaultMusicThumbnail = defaultMusicThumbnail,
                        onClick = {
                            musicPlayerViewModel.play(index)
                        }
                    )
                }
            }
        }

        AlphabetSlider(modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 8.dp),
            onSelectionChange = { viewModel.updateSelectedIndex(it) },
            /**
             * Setting selectedIndex to null when user finished scrolling, so that when user scrolls
             * and then navigates to a different screen and returns backs to this screen he/she/they
             * should see the same scroll state as he/she/they left before navigating.
             */
            onSelectionChangeFinished = { viewModel.updateSelectedIndex(null) })

        BottomMusicPlayer(
            modifier = Modifier.align(Alignment.BottomCenter),
            musicPlayerState = musicPlayerState,
            onBottomPlayerClick = { onBottomPlayerClick() },
            enabled = uiState.musicFiles.isNotEmpty(),
            onNextClick = { musicPlayerViewModel.onNextClick() },
            onPreviousClick = { musicPlayerViewModel.onPreviousClick() },
            onPlayPauseClick = { musicPlayerViewModel.onPlayPauseClick() })

        Box(modifier = Modifier.padding(24.dp)){
            if (!isMediaReadPermissionGranted) {
                Text(text = "Please grant media permission, to see your music files")
            } else if (uiState.musicFiles.isEmpty()) {
                Text(text = "No music files found :(")
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        if (!isMediaReadPermissionGranted && !BuildConfig.IS_BENCHMARKING) {
            mediaReadRequestLauncher.launch(getMediaReadPermissionString())
        }
    }
    LaunchedEffect(key1 = uiState.selectedIndex) {
        uiState.selectedIndex?.let { lazyListState.scrollToItem(it) }
    }
}

@Composable
private fun BottomMusicPlayer(
    modifier: Modifier = Modifier,
    musicPlayerState: MusicPlayerState,
    onBottomPlayerClick: () -> Unit,
    enabled: Boolean,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .fillMaxWidth()
            .height(70.dp)
            .background(
                gradientBackgroundColor(backgroundColor = musicPlayerState.backgroundColor),
                CircleShape
            )
            .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
            .clip(CircleShape)
            .clickable(enabled) { onBottomPlayerClick() },
        contentAlignment = Alignment.Center
    ) {
        musicPlayerState.apply {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(), contentDescription = "Preview Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.width(100.dp)) {
                    Text(
                        text = musicPlayerState.currentTrack.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = musicPlayerState.currentTrack.artist,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                MusicPlaybackButtons(
                    state = controllerState,
                    onNextClick = onNextClick,
                    onPreviousClick = onPreviousClick,
                    onPlayPauseClick = onPlayPauseClick
                )
            }
        }
    }
}

@Composable
private fun gradientBackgroundColor(backgroundColor: Int?): Brush {
    val color by animateColorAsState(
        targetValue = if (backgroundColor != null) Color(backgroundColor) else seed,
        animationSpec = tween(durationMillis = 500), label = "Music player background animation"
    )

    return Brush.horizontalGradient(
        colorStops = arrayOf(
            0.0f to color,
            0.6f to MaterialTheme.colorScheme.background,
        )
    )
}

@Preview
@Composable
fun PreviewTrackScreen() {
    TrackScreen(onBottomPlayerClick = {})
}