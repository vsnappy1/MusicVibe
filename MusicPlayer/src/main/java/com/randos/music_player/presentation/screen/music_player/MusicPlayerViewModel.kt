package com.randos.music_player.presentation.screen.music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.exoplayer.ExoPlayer
import com.randos.core.data.MusicScanner
import com.randos.music_player.presentation.component.RepeatMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MusicPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    musicScanner: MusicScanner,
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    /**
     * Get the index passed as argument when navigating to [MusicPlayer].
     */
    private var index: Int =
        (savedStateHandle[MusicPlayerNavigationDestination.param] ?: "0").toInt()


    private val musicFiles = musicScanner.musicFiles

    private val mediaItems = musicScanner.mediaItems

    /**
     * Load initial uiState data for smoother experience.
     */
    private val _uiState = MutableLiveData(
        MusicPlayerState(
            index = index,
            currentTrack = musicFiles[index]
        )
    )
    val uiState: LiveData<MusicPlayerState> = _uiState

    init {
        viewModelScope.launch {
            preparePlayer()
            initialTrackPlay(index)
            syncSeekPosition()
        }
    }

    /**
     * Update the seek position and keep it in sync with [ExoPlayer], so that user can see accurate
     * UI.
     */
    private fun syncSeekPosition() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                onSeekPositionChange(exoPlayer.currentPosition)
            }
        }
    }

    /**
     * Prepare player for media playback.
     */
    private fun preparePlayer() {
        exoPlayer.prepare()
        exoPlayer.addMediaItems(mediaItems)
        exoPlayer.addListener(object : Player.Listener {
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                /**
                 * When currently playing song ends this method is invoked with
                 * [Player.DISCONTINUITY_REASON_AUTO_TRANSITION] reason, update the current track
                 * so that it matches the media being played.
                 */
                if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
                    updateCurrentTrack()
                }
            }
        })
    }

    /**
     * Called when music player is first time launched.
     */
    private fun initialTrackPlay(index: Int) {
        exoPlayer.seekTo(index, 0)
        exoPlayer.play()
        updateCurrentTrack()
    }

    /**
     * Update the UI based on current playing track, [MusicPlayerViewModel] is using [MusicScanner]
     * to get [musicFiles] and [mediaItems], these two are separate lists [mediaItems] are used by
     * [ExoPlayer] while [musicFiles] has the data associated with each media file.
     * [ExoPlayer] plays a track this method gets details of this track and update the UI.
     */
    private fun updateCurrentTrack() {
        viewModelScope.launch {
            /**
             * Added some delay so that [ExoPlayer] get stable before reading values from it.
             */
            delay(250)
            _uiState.value?.apply {
                val currentTrack = musicFiles[exoPlayer.currentMediaItemIndex]
                _uiState.postValue(
                    this.copy(
                        index = index,
                        currentTrack = currentTrack,
                        controllerState = controllerState.copy(
                            isPlaying = exoPlayer.isPlaying,
                            isShuffleOn = exoPlayer.shuffleModeEnabled,
                            repeatMode = getRepeatMode(exoPlayer.repeatMode),
                            seekPosition = 0,
                            trackLength = currentTrack.duration,
                            isNextEnabled = exoPlayer.hasNextMediaItem(),
                            isPreviousEnabled = exoPlayer.hasPreviousMediaItem()
                        )
                    )
                )
            }
        }
    }

    /**
     * Toggle between play and pause.
     * Plays music when in paused state.
     * Pause music when is playing.
     */
    fun onPlayPauseClick() {
        val isPlaying = exoPlayer.isPlaying

        if (isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }

        _uiState.value?.apply {
            _uiState.postValue(this.copy(controllerState = controllerState.copy(isPlaying = !isPlaying)))
        }
    }

    /**
     * Toggle between shuffle enabled and disabled.
     * Enables the shuffle mode when disabled.
     * Disables the shuffle mode when enabled.
     */
    fun onShuffleClick() {
        val shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
        exoPlayer.shuffleModeEnabled = shuffleModeEnabled
        _uiState.value?.apply {
            _uiState.postValue(this.copy(controllerState = controllerState.copy(isShuffleOn = shuffleModeEnabled)))
        }
    }

    /**
     * Updated the repeat mode each time invoked.
     */
    fun onRepeatModeClick() {
        val repeatMode: Int = when (exoPlayer.repeatMode) {
            REPEAT_MODE_OFF -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            REPEAT_MODE_ONE -> REPEAT_MODE_OFF
            else -> REPEAT_MODE_ALL
        }
        exoPlayer.repeatMode = repeatMode
        _uiState.value?.apply {
            _uiState.postValue(
                this.copy(
                    controllerState = controllerState.copy(
                        repeatMode = getRepeatMode(repeatMode)
                    )
                )
            )
        }
    }

    /**
     * Maps the repeat mode form [ExoPlayer] library to [RepeatMode].
     */
    private fun getRepeatMode(repeatMode: Int): RepeatMode {
        return when (repeatMode) {
            REPEAT_MODE_OFF -> RepeatMode.NONE
            REPEAT_MODE_ALL -> RepeatMode.ALL
            REPEAT_MODE_ONE -> RepeatMode.ONE
            else -> RepeatMode.ALL
        }
    }

    /**
     * Seek to the next media item available in playlist.
     */
    fun onNextClick() {
        exoPlayer.seekToNextMediaItem()
        updateCurrentTrack()
    }

    /**
     * Seek to the previous item available in playlist.
     */
    fun onPreviousClick() {
        exoPlayer.seekToPreviousMediaItem()
        updateCurrentTrack()
    }

    /**
     * Update the seek position of current media playback to [position].
     */
    fun onSeekPositionChangeFinished(position: Long) {
        exoPlayer.seekTo(position)
        onSeekPositionChange(position)
    }

    /**
     * Update the seek position for UI.
     */
    private fun onSeekPositionChange(seekPosition: Long) {
        _uiState.value?.apply {
            _uiState.postValue(this.copy(controllerState = controllerState.copy(seekPosition = seekPosition)))
        }
    }
}