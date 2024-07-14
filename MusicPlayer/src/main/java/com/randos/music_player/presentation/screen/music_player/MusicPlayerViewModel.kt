package com.randos.music_player.presentation.screen.music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.music_player.utils.MusicVibeMediaController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val musicVibeMediaController: MusicVibeMediaController,
) : ViewModel() {

    /**
     * Load initial uiState data for smoother experience.
     */
    private val _uiState = MutableLiveData(
        MusicPlayerState()
    )
    val uiState: LiveData<MusicPlayerState> = _uiState


    init {
        viewModelScope.launch {
            musicVibeMediaController.uiState.collect {
                _uiState.postValue(it)
            }
        }
    }

    fun play(index: Int){
        musicVibeMediaController.setMediaIndex(index)
    }

    /**
     * Toggle between play and pause.
     * Plays music when in paused state.
     * Pause music when is playing.
     */
    fun onPlayPauseClick() {
        musicVibeMediaController.onPlayPauseClick()
    }

    /**
     * Toggle between shuffle enabled and disabled.
     * Enables the shuffle mode when disabled.
     * Disables the shuffle mode when enabled.
     */
    fun onShuffleClick() {
        musicVibeMediaController.onShuffleClick()
    }

    /**
     * Updated the repeat mode each time invoked.
     */
    fun onRepeatModeClick() {
        musicVibeMediaController.onRepeatModeClick()
    }

    /**
     * Seek to the next media item available in playlist.
     */
    fun onNextClick() {
        musicVibeMediaController.onNextClick()
    }

    /**
     * Seek to the previous item available in playlist.
     */
    fun onPreviousClick() {
        musicVibeMediaController.onPreviousClick()
    }

    /**
     * Update the seek position of current media playback to [position].
     */
    fun onSeekPositionChangeFinished(position: Long) {
        musicVibeMediaController.onSeekPositionChangeFinished(position)
    }
}