package com.randos.music_player.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.randos.music_player.utils.toTime
import kotlinx.coroutines.delay

private const val TAG = "MusicPlayerController"

internal enum class RepeatMode {
    NONE, ONE, ALL
}

/**
 * State holder class for [MusicPlaybackController]
 *
 * @param isPlaying Specifies if track is playing or is in pause state.
 * @param isShuffleOn Specifies if shuffle mode is enabled or disabled.
 * @param repeatMode Specifies the repeat mode [RepeatMode]
 * @param seekPosition It is current position of track play back in milliseconds.
 * @param trackLength It is the total length of track in milliseconds
 */
internal data class MusicPlaybackControllerState(
    val isPlaying: Boolean = false,
    val isShuffleOn: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val seekPosition: Long = 0,
    val trackLength: Long = 100
)

@Composable
internal fun MusicPlaybackController(
    modifier: Modifier = Modifier,
    state: MusicPlaybackControllerState,
    onPlayPauseClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatModeClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onValueChangeFinished: (Long) -> Unit
) {

    Column(modifier = modifier) {
        SeekBar(
            state.seekPosition,
            state.trackLength,
            onValueChangeFinished
        )
        MusicPlaybackButtons(
            state,
            onPlayPauseClick,
            onShuffleClick,
            onRepeatModeClick,
            onPreviousClick,
            onNextClick,
        )
    }
}

@Composable
private fun MusicPlaybackButtons(
    state: MusicPlaybackControllerState,
    onPlayPauseClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatModeClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaybackControllerIcon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = "Shuffle",
            tint = if (state.isShuffleOn) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.surfaceVariant,
            onClick = { onShuffleClick() },
        )
        PlaybackControllerIcon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = "Play Previous Track",
            modifier = Modifier.rotate(180f),
            onClick = { onPreviousClick() }
        )
        PlaybackControllerIcon(
            imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = "Play/Pause",
            size = 70.dp,
            onClick = { onPlayPauseClick() }
        )
        PlaybackControllerIcon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = "Play Next Track",
            onClick = { onNextClick() }
        )
        PlaybackControllerIcon(
            imageVector = if (state.repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
            contentDescription = "Repeat Mode",
            tint = if (state.repeatMode == RepeatMode.NONE) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.onBackground,
            onClick = { onRepeatModeClick() }
        )
    }
}

@Composable
private fun PlaybackControllerIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    size: Dp = 40.dp,
    onClick: () -> Unit
) {

    /**
     * Creates animation effect when clicked for more appealing UX.
     */
    var isClicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 0.75f else 1.0f,
        label = "PlaybackController"
    )
    Box(modifier = modifier
        .scale(scale)
        .size(size)
        .clip(CircleShape)
        .clickable {
            isClicked = true
            onClick()
        }) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
    }

    LaunchedEffect(key1 = isClicked) {
        delay(100)
        isClicked = false
    }
}

@Composable
private fun SeekBar(
    seekPosition: Long,
    trackLength: Long,
    onValueChangeFinished: (Long) -> Unit,
) {

    var isUserInteracting by remember { mutableStateOf(false) }
    val mutableInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        mutableInteractionSource.interactions.collect { interaction ->
            /**
             * Collect interaction on [Slider] and when it is either [DragInteraction.Start] or
             * [PressInteraction.Press] enable the flag to indicate user is interacting with
             * component.
             */
            if (interaction is DragInteraction.Start ||
                interaction is PressInteraction.Press
            ) {
                isUserInteracting = true
            }
            /**
             * When interaction is of type either [DragInteraction.Start] or
             * [PressInteraction.Press] disable the flag to indicate user is has stopped interacting
             * with component.
             */
            else if (interaction is DragInteraction.Stop ||
                interaction is PressInteraction.Release
            ) {
                /**
                 * Adding some delay so that onValueChangeFinished callback on [Slider] is invoked
                 * and viewModel have sufficient time to update the state.
                 */
                delay(100)
                isUserInteracting = false
            }
        }
    }

    /**
     * The seek variable starts from current seekPosition and is updated in onValueChange callback in
     * [Slider]. When user starts interaction with [Slider] the value of slider is changed to
     * seek variable so that user can move the slider position left or right. This enables user to
     * interact with [Slider] without interrupting the current playback. When user stops interacting
     * with [Slider] onValueChangeFinished callback is invoked and seek variable is passed to the
     * calling function.
     */
    var seek by remember { mutableFloatStateOf(seekPosition.toFloat()) }
    val sliderValue = if (isUserInteracting) seek else seekPosition.toFloat()

    Box(modifier = Modifier) {
        Slider(
            modifier = Modifier,
            value = sliderValue,
            onValueChange = { seek = it },
            valueRange = 0f..trackLength.toFloat(),
            onValueChangeFinished = {
                onValueChangeFinished(seek.toLong())
            },
            interactionSource = mutableInteractionSource
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = seekPosition.toTime(),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = trackLength.toTime(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview
@Composable
private fun PreviewMusicPlayerController() {
    MusicPlaybackController(state = MusicPlaybackControllerState(
        isPlaying = false,
        isShuffleOn = false,
        repeatMode = RepeatMode.NONE,
        seekPosition = 50,
        trackLength = 10000
    ),
        onPlayPauseClick = {},
        onShuffleClick = {},
        onRepeatModeClick = {},
        onNextClick = {},
        onPreviousClick = {},
        onValueChangeFinished = {})
}