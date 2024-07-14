package com.randos.music_player.presentation.component

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.randos.core.presentation.component.BouncyComposable
import com.randos.music_player.utils.toTime
import kotlinx.coroutines.delay

enum class RepeatMode(val value: Int) {
    NONE(Player.REPEAT_MODE_OFF),
    ONE(Player.REPEAT_MODE_ONE),
    ALL(Player.REPEAT_MODE_ALL)
}

/**
 * State holder class for [MusicPlaybackController]
 *
 * @param isPlaying Specifies if track is playing or is in pause state.
 * @param shuffleEnabled Specifies if shuffle mode is enabled or disabled.
 * @param repeatMode Specifies the repeat mode [RepeatMode].
 * @param seekPosition It is current position of track play back in milliseconds.
 * @param trackLength It is the total length of track in milliseconds.
 * @param isNextEnabled enable when next item in playlist is available.
 * @param isPreviousEnabled enable when previous item in playlist is available.
 */
data class MusicPlaybackControllerState(
    val isPlaying: Boolean = false,
    val shuffleEnabled: Boolean = true,
    val repeatMode: RepeatMode = RepeatMode.ALL,
    val seekPosition: Long = 0,
    val trackLength: Long = 100,
    val isNextEnabled: Boolean = true,
    val isPreviousEnabled: Boolean = true
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
            onNextClick,
            onPreviousClick,
        )
    }
}

@Composable
private fun MusicPlaybackButtons(
    state: MusicPlaybackControllerState,
    onPlayPauseClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatModeClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaybackControllerIcon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = "Shuffle",
            tint = if (state.shuffleEnabled) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
            onClick = { onShuffleClick() },
        )
        PlaybackControllerIcon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = "Play Previous Track",
            enabled = state.isPreviousEnabled,
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
            enabled = state.isNextEnabled,
            onClick = { onNextClick() }
        )
        PlaybackControllerIcon(
            imageVector = if (state.repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
            contentDescription = "Repeat Mode",
            tint = if (state.repeatMode == RepeatMode.NONE) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.onBackground,
            onClick = { onRepeatModeClick() }
        )
    }
}

@Composable
fun MusicPlaybackButtons(
    modifier: Modifier = Modifier,
    state: MusicPlaybackControllerState,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaybackControllerIcon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = "Play Previous Track",
            enabled = state.isPreviousEnabled,
            modifier = Modifier.rotate(180f),
            onClick = { onPreviousClick() }
        )
        PlaybackControllerIcon(
            imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = "Play/Pause",
            size = 40.dp,
            onClick = { onPlayPauseClick() }
        )
        PlaybackControllerIcon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = "Play Next Track",
            enabled = state.isNextEnabled,
            onClick = { onNextClick() }
        )
    }
}

@Composable
private fun PlaybackControllerIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    size: Dp = 30.dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    BouncyComposable(
        modifier = modifier.padding(8.dp),
        clickEnabled = enabled,
        onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = if (enabled) tint else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
            modifier = Modifier
                .size(size)
        )
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
            colors = SliderDefaults.colors().copy(
                thumbColor = MaterialTheme.colorScheme.onBackground,
                activeTrackColor = MaterialTheme.colorScheme.onBackground,
                inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)),
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
        shuffleEnabled = false,
        repeatMode = RepeatMode.NONE,
        seekPosition = 50,
        trackLength = 10000,
        isNextEnabled = false,
        isPreviousEnabled = true
    ),
        onPlayPauseClick = {},
        onShuffleClick = {},
        onRepeatModeClick = {},
        onNextClick = {},
        onPreviousClick = {},
        onValueChangeFinished = {})
}