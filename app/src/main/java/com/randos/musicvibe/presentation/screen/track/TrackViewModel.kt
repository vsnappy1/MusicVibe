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
            _uiState.postValue(
                _uiState.value?.copy(
                    audioFiles = sortedAudioFilesByTitle,
                    defaultMusicThumbnail = defaultMusicThumbnail
                )
            )
        }
    }

    /**
     * Find the index of first element which starts with [alphabet], if not found algorithm looks for
     * previous alphabet (i.e. if there is no element which starts with X, then algorithm looks for
     * element which starts W) and repeat the same for this alphabet.
     */
    private fun getIndex(alphabet: Char): Int {
        val sortedAudioFilesByTitle = _uiState.value?.audioFiles
        var index = -1
        var code = alphabet.code
        while (code >= 'A'.code &&
            code <= 'Z'.code &&
            index < 0
        ) {
            index =
                sortedAudioFilesByTitle?.indexOfFirst { it.title.startsWith(code.toChar()) } ?: 0
            code--
        }

        return if (index > 0) index else 0
    }

    fun updateSelectedIndex(alphabet: Char) {
        viewModelScope.launch {
            _uiState.postValue(
                _uiState.value?.copy(
                    selectedIndex = getIndex(alphabet)
                )
            )
        }
    }
}