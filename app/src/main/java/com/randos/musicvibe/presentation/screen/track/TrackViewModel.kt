package com.randos.musicvibe.presentation.screen.track

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.musicvibe.data.MusicScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    musicScanner: MusicScanner,
    defaultMusicThumbnail: Bitmap
) : ViewModel() {

    private val _uiState = MutableLiveData(TrackScreenUiState())
    val uiState: LiveData<TrackScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            val audioFiles = musicScanner.getAllAudioFiles()
            val sortedAudioFilesByTitle = audioFiles.sortedBy { it.title }
            _uiState.postValue(_uiState.value?.copy(audioFiles = sortedAudioFilesByTitle, defaultMusicThumbnail = defaultMusicThumbnail))
        }
    }
}