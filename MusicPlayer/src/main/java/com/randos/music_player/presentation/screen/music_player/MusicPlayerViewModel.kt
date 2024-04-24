package com.randos.music_player.presentation.screen.music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.randos.music_player.presentation.component.MusicPlaybackControllerState
import com.randos.music_player.presentation.component.RepeatMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MusicPlayerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableLiveData(MusicPlaybackControllerState())
    val uiState: LiveData<MusicPlaybackControllerState> = _uiState

    fun onPlayPauseClick() {
        val isPlaying = _uiState.value?.isPlaying ?: false
        _uiState.postValue(_uiState.value?.copy(isPlaying = !isPlaying))
    }

    fun onFirstLaunch(){
        _uiState.postValue(_uiState.value?.copy(isPlaying = true))
    }

    fun onShuffleClick() {
        val isShuffleOn = _uiState.value?.isShuffleOn ?: false
        _uiState.postValue(_uiState.value?.copy(isShuffleOn = !isShuffleOn))
    }

    fun onRepeatModeClick() {
        val currentRepeatMode = _uiState.value?.repeatMode ?: RepeatMode.NONE
        val newRepeatMode: RepeatMode =
            when (currentRepeatMode) {
                RepeatMode.NONE -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.NONE
            }
        _uiState.postValue(_uiState.value?.copy(repeatMode = newRepeatMode))
    }

    fun onNextClick() {}

    fun onPreviousClick() {}

    fun onSeekPositionChange(seekPosition: Long) {
        _uiState.postValue(_uiState.value?.copy(seekPosition = seekPosition))
    }

    fun onSeekPositionChangeFinished(seekPosition: Long) {
        _uiState.postValue(_uiState.value?.copy(seekPosition = seekPosition))
    }

    fun setTrackLength(trackLength: Long){
        _uiState.postValue(_uiState.value?.copy(trackLength = trackLength))
    }
}