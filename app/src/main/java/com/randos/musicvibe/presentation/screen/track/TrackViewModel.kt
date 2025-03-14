package com.randos.musicvibe.presentation.screen.track

import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.MusicPlayer
import com.randos.domain.manager.PermissionManager
import com.randos.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer,
    private val musicRepository: MusicRepository,
    val permissionManager: PermissionManager<ActivityResultLauncher<String>>
) : ViewModel() {

    private val indexMap = mutableMapOf<Char, Int>()
    private val _uiState = MutableLiveData(TrackScreenUiState())
    val uiState: LiveData<TrackScreenUiState> = _uiState

    fun rescan() {
        viewModelScope.launch {
            _uiState.postValue(
                _uiState.value?.copy(
                    musicFiles = musicRepository.getMusicFiles(),
                )
            )
        }
    }

    fun hasMediaItemCountChanged(): Boolean{
        val musicFilesCount = _uiState.value?.musicFiles?.size ?: 0
        return musicFilesCount != musicPlayer.getMediaItemCount()
    }

    /**
     * Find the index of first element which starts with [alphabet], if not found algorithm looks for
     * previous alphabet (i.e. if there is no element which starts with B, then algorithm looks for
     * element which starts A) and repeat the same for this alphabet.
     */
    private fun getIndex(alphabet: Char): Int {
        /**
         * Using a map [indexMap] as a cache mechanism for improved performance.
         */
        if (indexMap.containsKey(alphabet)) return indexMap[alphabet] ?: 0
        val sortedMusicFilesByTitle = _uiState.value?.musicFiles
        var index = -1
        var code = alphabet.code
        while (code >= 'A'.code &&
            code <= 'Z'.code &&
            index < 0
        ) {
            index =
                sortedMusicFilesByTitle?.indexOfFirst { it.title.startsWith(code.toChar()) } ?: 0
            code--
        }

        val result = if (index > 0) index else 0
        indexMap[alphabet] = result
        return result
    }

    fun updateSelectedIndex(alphabet: Char?) {
        viewModelScope.launch {
            _uiState.postValue(
                _uiState.value?.copy(
                    selectedIndex = alphabet?.let { getIndex(alphabet) }
                )
            )
        }
    }
}