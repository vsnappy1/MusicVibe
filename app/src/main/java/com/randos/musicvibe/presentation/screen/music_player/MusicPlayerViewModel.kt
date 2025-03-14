package com.randos.musicvibe.presentation.screen.music_player

import android.graphics.Bitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.randos.domain.manager.MusicPlayer
import com.randos.domain.model.MusicFile
import com.randos.domain.type.PlayerEvent
import com.randos.domain.type.RepeatMode
import com.randos.musicvibe.presentation.theme.seed
import com.randos.musicvibe.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer
) : ViewModel() {

    companion object {
        private const val PLAY_UPDATE_DELAY = 100L
    }

    /**
     * Load initial uiState data for smoother experience.
     */
    private val _uiState = MutableLiveData(
        MusicPlayerState()
    )
    val uiState: LiveData<MusicPlayerState> = _uiState

    private var playPauseJob: Job? = null

    init {
        viewModelScope.launch {
            musicPlayer.prepare()
            viewModelScope.launch {
                musicPlayer.seekPosition().collect {
                    _uiState.value = _uiState.value?.copy(
                        controllerState = _uiState.value?.controllerState?.copy(
                            seekPosition = it
                        )!!
                    )
                }
            }
            val currentlyPlayingMusic = musicPlayer.currentMusic()
            if (currentlyPlayingMusic != null) {
                setupMusicPlayer(currentlyPlayingMusic)
            }
            musicPlayer.playerEvent().collect {
                var controllerState = _uiState.value?.controllerState!!

                when (it) {
                    is PlayerEvent.MusicChange -> {
                        setupMusicPlayer(it.musicFile)
                    }

                    is PlayerEvent.Pause -> {
                        updateIsPlayingState()
                    }

                    is PlayerEvent.Play -> {
                        updateIsPlayingState()
                    }

                    is PlayerEvent.RepeatMode -> {
                        controllerState = controllerState.copy(repeatMode = it.repeatMode)
                        _uiState.postValue(_uiState.value?.copy(controllerState = controllerState))
                    }

                    is PlayerEvent.ShuffleEnabled -> {
                        controllerState = controllerState.copy(shuffleEnabled = it.enabled)
                        _uiState.postValue(_uiState.value?.copy(controllerState = controllerState))

                    }
                }

            }
        }
    }

    private suspend fun MusicPlayerViewModel.setupMusicPlayer(
        currentlyPlayingMusic: MusicFile
    ) {
        val (bitmap, color) = getThumbnailAndBackgroundColor(currentlyPlayingMusic.path)
        val controllerState = _uiState.value?.controllerState?.copy(
            shuffleEnabled = musicPlayer.getShuffleEnabled(),
            repeatMode = musicPlayer.getRepeatMode(),
            seekPosition = 0,
            trackLength = currentlyPlayingMusic.duration,
            isNextEnabled = musicPlayer.isNextAvailable(),
            isPreviousEnabled = musicPlayer.isPreviousAvailable()
        )

        _uiState.value = _uiState.value?.copy(
            currentTrack = currentlyPlayingMusic,
            backgroundColor = color,
            bitmap = bitmap,
            controllerState = controllerState!!
        )

        updateIsPlayingState()
    }

    private fun updateIsPlayingState() {
        playPauseJob?.cancel()
        playPauseJob = viewModelScope.launch {
            delay(PLAY_UPDATE_DELAY)
            _uiState.value = _uiState.value?.copy(
                controllerState = _uiState.value?.controllerState?.copy(
                    isPlaying = musicPlayer.isPlaying()
                )!!
            )
        }
    }

    fun rescan() {
        viewModelScope.launch {
            musicPlayer.prepare()
        }
    }

    fun play() {
        musicPlayer.play()
    }

    fun playAtIndex(index: Int) {
        musicPlayer.playAtIndex(index)
    }

    /**
     * Toggle between play and pause.
     * Plays music when in paused state.
     * Pause music when is playing.
     */
    fun onPlayPauseClick() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause()
        } else {
            musicPlayer.play()
        }
    }

    /**
     * Toggle between shuffle enabled and disabled.
     * Enables the shuffle mode when disabled.
     * Disables the shuffle mode when enabled.
     */
    fun onShuffleClick() {
        musicPlayer.setShuffleEnabled(!musicPlayer.getShuffleEnabled())
    }

    /**
     * Updated the repeat mode each time invoked.
     */
    fun onRepeatModeClick() {
        val value = when (musicPlayer.getRepeatMode()) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        musicPlayer.setRepeatMode(value)
    }

    /**
     * Seek to the next media item available in playlist.
     */
    fun onNextClick() {
        musicPlayer.next()
    }

    /**
     * Seek to the previous item available in playlist.
     */
    fun onPreviousClick() {
        musicPlayer.previous()
    }

    /**
     * Update the seek position of current media playback to [position].
     */
    fun onSeekPositionChangeFinished(position: Long) {
        musicPlayer.updateSeek(position)
    }

    fun delete() {
        viewModelScope.launch {
            musicPlayer.delete()
        }
    }

    /**
     * Extract the thumbnail associated with media item also generate muted color using thumbnail
     * for background color.
     */
    private suspend fun getThumbnailAndBackgroundColor(path: String): Pair<Bitmap?, Int> {
        fun getSolidOpaqueColor(argb: Int) = (argb and 0x00FFFFFF) or (0xFF shl 24)
        val bitmap = Utils.getAlbumImage(path)
        if (bitmap != null) {
            val palette = Palette.from(bitmap).generate()
            val argb = palette.getMutedColor(seed.value.toInt())
            return Pair(bitmap, getSolidOpaqueColor(argb))

        } else {
            // If media does not have any thumbnail associated with it use defaultThumbnail.
            return Pair(null, seed.toArgb())
        }
    }

    fun onFileDeleted() {
        viewModelScope.launch {
            musicPlayer.delete()
        }
    }
}