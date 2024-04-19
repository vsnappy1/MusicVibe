package com.randos.musicvibe.presentation.screen.track

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.musicvibe.data.MusicScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(musicScanner: MusicScanner) : ViewModel() {

    private val _uiState = MutableLiveData(TrackScreenUiState())
    val uiState: LiveData<TrackScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            val audioFiles = musicScanner.getAllAudioFiles()
            _uiState.postValue(_uiState.value?.copy(audioFiles = audioFiles))
        }
    }

}