package com.randos.music_player.presentation.screen.music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import com.randos.core.data.DataStoreManager
import com.randos.core.data.MusicScanner
import com.randos.music_player.utils.MusicVibeMediaController
import com.randos.music_player.presentation.component.RepeatMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MusicPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    musicScanner: MusicScanner,
    musicVibeMediaController: MusicVibeMediaController,
    private val dataStore: DataStoreManager,
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

    private lateinit var mediaController: MediaController

    init {
        musicVibeMediaController.mediaController?.let { mediaController ->
            this.mediaController = mediaController
            viewModelScope.launch {
                setRepeatMode(dataStore.getRepeatMode())
                setShuffleEnabled(dataStore.getShuffleEnabled())
                mediaController.addListener(playerListener())
                mediaController.seekTo(index, 0)
                mediaController.play()
                updateCurrentTrack()
                syncSeekPosition()
            }
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
                onSeekPositionChange(mediaController.currentPosition)
            }
        }
    }

    private fun playerListener() = object : Player.Listener {

        /**
         * When media item is changed, update the UI accordingly.
         */
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateCurrentTrack()
        }

        var updateIsPlayingJob: Job? = null
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            /**
             * This method is invoked when the value of isPlaying() changes. When user move
             * to the next or previous media item in the playlist there is brief pause and this
             * method is invoked twice within 150ms first with [isPlaying] as false and then true
             * and this caused some flickering in UI, therefore to smoothen the experience I have
             * added this mechanism.
             *
             * Initially [updateIsPlayingJob] is not active, first time when this method is
             * invoked either we get [isPlaying] as false and create the job as follow next
             * this method is again invoked within 150ms then we cancel the existing job and
             * create a new one so this rapid change does not propagate to UI.
             */
            if (updateIsPlayingJob?.isActive == true) {
                updateIsPlayingJob?.cancel()
            }
            updateIsPlayingJob = viewModelScope.launch {
                /**
                 * Based on my observation two consecutive method calls happens within 150ms so
                 * to be on safe side I have added the 250ms delay.
                 */
                /**
                 * Based on my observation two consecutive method calls happens within 150ms so
                 * to be on safe side I have added the 250ms delay.
                 */
                delay(250)
                _uiState.value?.apply {
                    _uiState.postValue(
                        this.copy(
                            controllerState = controllerState.copy(
                                isPlaying = isPlaying,
                            )
                        )
                    )
                }
            }
        }
    }

    /**
     * Update the UI based on current playing track, [MusicPlayerViewModel] is using [MusicScanner]
     * to get [musicFiles] and [mediaItems], these two are separate lists [mediaItems] are used by
     * [ExoPlayer] while [musicFiles] has the data associated with each media file.
     * [ExoPlayer] plays a track this method gets details of this track and update the UI.
     */
    private fun updateCurrentTrack() {
        _uiState.value?.apply {
            val currentTrack = musicFiles[mediaController.currentMediaItemIndex]
            _uiState.postValue(
                this.copy(
                    index = index,
                    currentTrack = currentTrack,
                    controllerState = controllerState.copy(
                        shuffleEnabled = mediaController.shuffleModeEnabled,
                        repeatMode = getRepeatMode(mediaController.repeatMode),
                        seekPosition = 0,
                        trackLength = currentTrack.duration,
                        isNextEnabled = mediaController.hasNextMediaItem(),
                        isPreviousEnabled = mediaController.hasPreviousMediaItem()
                    )
                )
            )
        }
    }

    /**
     * Toggle between play and pause.
     * Plays music when in paused state.
     * Pause music when is playing.
     */
    fun onPlayPauseClick() {
        val isPlaying = mediaController.isPlaying

        if (isPlaying) {
            mediaController.pause()
        } else {
            mediaController.play()
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
        setShuffleEnabled(!mediaController.shuffleModeEnabled)
    }

    /**
     * Set shuffleEnabled on [mediaController] and update the UI.
     */
    private fun setShuffleEnabled(shuffleEnabled: Boolean) {
        mediaController.shuffleModeEnabled = shuffleEnabled
        _uiState.value?.apply {
            _uiState.postValue(this.copy(controllerState = controllerState.copy(shuffleEnabled = shuffleEnabled)))
        }
        /**
         * Store the shuffleEnabled into datastore.
         */
        viewModelScope.launch {
            dataStore.setShuffleEnabled(shuffleEnabled)
        }
    }

    /**
     * Updated the repeat mode each time invoked.
     */
    fun onRepeatModeClick() {
        val repeatMode: Int = when (mediaController.repeatMode) {
            REPEAT_MODE_OFF -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            REPEAT_MODE_ONE -> REPEAT_MODE_OFF
            else -> REPEAT_MODE_ALL
        }
        setRepeatMode(repeatMode)
    }

    /**
     * Set repeatMode on [mediaController] and update the UI.
     */
    private fun setRepeatMode(repeatMode: Int) {
        mediaController.repeatMode = repeatMode
        _uiState.value?.apply {
            _uiState.postValue(
                this.copy(
                    controllerState = controllerState.copy(
                        repeatMode = getRepeatMode(repeatMode)
                    )
                )
            )
        }
        /**
         * Store the repeat mode into datastore.
         */
        viewModelScope.launch {
            dataStore.setRepeatMode(repeatMode)
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
        mediaController.seekToNextMediaItem()
    }

    /**
     * Seek to the previous item available in playlist.
     */
    fun onPreviousClick() {
        mediaController.seekToPreviousMediaItem()
    }

    /**
     * Update the seek position of current media playback to [position].
     */
    fun onSeekPositionChangeFinished(position: Long) {
        mediaController.seekTo(position)
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