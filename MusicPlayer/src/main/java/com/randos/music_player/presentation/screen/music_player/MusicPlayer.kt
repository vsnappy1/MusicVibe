package com.randos.music_player.presentation.screen.music_player


import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.randos.core.data.model.MusicFile
import com.randos.core.navigation.NavigationDestinationWithParams
import com.randos.core.presentation.component.BouncyComposable
import com.randos.core.utils.defaultPadding
import com.randos.music_player.presentation.component.MusicPlaybackController
import com.randos.music_player.presentation.component.MusicPlaybackControllerState

object MusicPlayerNavigationDestination : NavigationDestinationWithParams {
    override val name: String = "Music Player"
    override val route: String = "/music_player"
    override val param: String = "index"
}

@Immutable
data class MusicPlayerState(
    val index: Int = 0,
    val bitmap: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888),
    val backgroundColor: Int? = null,
    val currentTrack: MusicFile = MusicFile.default(),
    val controllerState: MusicPlaybackControllerState = MusicPlaybackControllerState()
)

@OptIn(UnstableApi::class)
@Composable
fun MusicPlayer(
    onBack: () -> Unit
) {
    val viewModel: MusicPlayerViewModel = hiltViewModel()
    val state by viewModel.uiState.observeAsState(MusicPlayerState())

    val backgroundColor = state.backgroundColor?.let { Color(it) } ?: MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .background(gradientBackgroundColor(backgroundColor))
            .fillMaxSize()
            .defaultPadding(top = 48.dp, bottom = 64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton(onBack = onBack)
            OptionMenu()
        }

        PreviewImageTitleArtist(
            modifier = Modifier.align(Alignment.Center),
            isPlaying = state.controllerState.isPlaying,
            previewImage = state.bitmap,
            title = state.currentTrack.title,
            artist = state.currentTrack.artist,
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
}

@Composable
private fun OptionMenu() {
    BouncyComposable(onClick = { /*TODO*/ }) {
        ThreeDotsOptionMenu()
    }
}

@Composable
private fun BackButton(
    onBack: () -> Unit
) {

    BouncyComposable(onClick = {
        onBack()
    }) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Arrow back",
            modifier = Modifier
                .size(32.dp)
        )
    }
}

/**
 * Uses muted color from media thumbnail create vertical gradient background color.
 */
@Composable
private fun gradientBackgroundColor(backgroundColor: Color): Brush {
    val color by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 500), label = "Music player background animation"
    )

    return Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to MaterialTheme.colorScheme.background,
            0.5f to color,
            1f to MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun PreviewImageTitleArtist(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    previewImage: Bitmap?,
    title: String,
    artist: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val previewImageSize by animateDpAsState(
            targetValue = if (isPlaying) 300.dp else 150.dp,
            animationSpec = tween(),
            label = "music player thumbnail preview animation"
        )

        previewImage?.let {
            Image(
                bitmap = it.asImageBitmap(), contentDescription = "Preview Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(previewImageSize)
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

@Preview
@Composable
fun ThreeDotsOptionMenu() {
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .size(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val size = 7.dp
        Icon(
            imageVector = Icons.Rounded.Circle,
            modifier = Modifier.size(size),
            contentDescription = "Circle"
        )
        Icon(
            imageVector = Icons.Rounded.Circle,
            modifier = Modifier.size(size),
            contentDescription = "Circle"
        )
        Icon(
            imageVector = Icons.Rounded.Circle,
            modifier = Modifier.size(size),
            contentDescription = "Circle"
        )
    }
}
