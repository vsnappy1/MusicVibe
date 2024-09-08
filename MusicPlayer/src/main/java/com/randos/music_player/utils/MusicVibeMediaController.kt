package com.randos.music_player.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.toArgb
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.palette.graphics.Palette
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.randos.core.data.DataStoreManager
import com.randos.core.data.MusicScanner
import com.randos.core.data.model.MusicFile
import com.randos.core.presentation.theme.seed
import com.randos.core.utils.Utils
import com.randos.logger.Logger
import com.randos.music_player.presentation.component.RepeatMode
import com.randos.music_player.presentation.screen.music_player.MusicPlayerState
import com.randos.music_player.presentation.screen.music_player.MusicPlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A wrapper around [MediaController]
 */
class MusicVibeMediaController @Inject constructor(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val musicScanner: MusicScanner,
    private val dataStore: DataStoreManager,
    private val defaultThumbnail: Bitmap,
) {
    private var mediaController: MediaController? = null

    private val musicFiles = musicScanner.musicFiles

    private val mediaItems = musicScanner.mediaItems

    private var coroutineScope = CoroutineScope(Dispatchers.Default)

    /**
     * Load initial uiState data for smoother experience.
     */
    private val _uiState = MutableStateFlow(MusicPlayerState())

    val uiState: MutableStateFlow<MusicPlayerState> = _uiState

    init {
        preparePlayer()
    }

    /**
     * Rescans the storage and prepares the [mediaController]
     */
    fun rescan() {
        CoroutineScope(Dispatchers.IO).launch {
            musicScanner.scan()?.join()
            withContext(Dispatchers.Main) {
                mediaController?.apply {
                    prepare()
                    setMediaItems(mediaItems)
                    updateCurrentTrack()
                }
            }
        }
    }

    private fun preparePlayer() {
        Logger.i(this, "Preparing player.")
        mediaControllerFuture.addListener({
            mediaController = mediaControllerFuture.get()
            mediaController?.apply {
                if (mediaItems.isEmpty()) {
                    Logger.w(this@MusicVibeMediaController, "No media items found.")
                    _uiState.value.apply {
                        _uiState.value = (this.copy(currentTrack = MusicFile.default(), index = -1))
                    }
                    return@apply
                }
                prepare()
                setMediaItems(mediaItems)
                addListener(playerListener())
                CoroutineScope(Dispatchers.Main).launch {
                    shuffleModeEnabled = dataStore.getShuffleEnabled()
                    repeatMode = dataStore.getRepeatMode()
                    val (id, playedDuration) = dataStore.getLastPlayedMusicFileDetails()
                    val index = musicFiles.indexOfFirst { it.id == id }
                    if (index != -1) {
                        seekTo(index, playedDuration)
                    }
                    updateCurrentTrack(playedDuration)
                }
                Logger.i(this@MusicVibeMediaController, "Player is ready.")
            }
        }, MoreExecutors.directExecutor())
    }

    fun setMediaIndex(index: Int) {
        mediaController?.apply {
            seekTo(index, 0)
            updateCurrentTrack()
            play()
        }
    }

    /**
     * Update the seek position and keep it in sync with [MediaController], so that user can see
     * accurate UI.
     */
    private var playbackSyncJob: Job? = null
    private fun startPlaybackSync() {
        /*
         * Some times this method is invoked multiple times in a short span, to ensure there are not
         * multiple same job (memory leak) cancel the job and then start again.
         */
        stopPlaybackSync()
        playbackSyncJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(100) // 30 FPS
                _uiState.value.apply {
                    mediaController?.let { onSeekPositionChange(it.currentPosition) }
                }
            }
        }
    }

    /**
     * Stop the playback seek sync.
     */
    private fun stopPlaybackSync() {
        playbackSyncJob?.cancel()
    }

    private fun playerListener() = object : Player.Listener {

        /**
         * When media item is changed, update the UI accordingly.
         */
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateCurrentTrack()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateRepeatMode(repeatMode)
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            updateShuffleModeEnabled(shuffleModeEnabled)
        }

        private var shouldUpdateIsPlayingOnUi = true

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            /*
             I have observed when seek is changed onIsPlayingChanged callback is invoked multiple
             times resulting in a quick jerk between play and pause state, to avoid it I am
             using shouldUpdateIsPlayingOnUi flag.
            */
            shouldUpdateIsPlayingOnUi = false
            coroutineScope.launch {
                delay(50)
                shouldUpdateIsPlayingOnUi = true
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            /**
             * Start or Stop the playback sync when isPlaying changes as we don't want to
             * unnecessary CPU usage.
             */
            if (isPlaying) {
                startPlaybackSync()
            } else {
                stopPlaybackSync()
                updateLastPlayedMusicFileDetails()
            }

            if (shouldUpdateIsPlayingOnUi) {
                updateIsPlaying(isPlaying)
            }
        }
    }

    private fun updateIsPlaying(isPlaying: Boolean) {
        _uiState.value.apply {
            _uiState.value =
                (this.copy(controllerState = controllerState.copy(isPlaying = isPlaying)))
        }
    }

    private fun updateShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        _uiState.value.apply {
            _uiState.value =
                (this.copy(controllerState = controllerState.copy(shuffleEnabled = shuffleModeEnabled)))
        }
        /**
         * Store the shuffleEnabled into datastore.
         */
        coroutineScope.launch {
            dataStore.setShuffleEnabled(shuffleModeEnabled)
        }
    }

    private fun updateRepeatMode(repeatMode: Int) {
        _uiState.value.apply {
            _uiState.value =
                (this.copy(
                    controllerState = controllerState.copy(
                        repeatMode = getRepeatMode(repeatMode)
                    )
                )
                        )
        }
        /**
         * Store the repeat mode into datastore.
         */
        coroutineScope.launch {
            dataStore.setRepeatMode(repeatMode)
        }
    }

    private fun updateLastPlayedMusicFileDetails() {
        mediaController?.apply {
            val musicFileId = currentMediaItem?.mediaId?.toLong() ?: 0
            val playedDuration = currentPosition
            coroutineScope.launch {
                dataStore.setLastPlayedMusicFileDetails(musicFileId, playedDuration)
            }
        }
    }

    /**
     * Update the UI based on current playing track, [MusicPlayerViewModel] is using [MusicScanner]
     * to get [musicFiles] and [mediaItems], these two are separate lists [mediaItems] are used by
     * [ExoPlayer] while [musicFiles] has the data associated with each media file.
     * [ExoPlayer] plays a track this method gets details of this track and update the UI.
     */
    private fun updateCurrentTrack(seekPosition: Long = 0) {
        if (musicFiles.isEmpty()) return
        mediaController?.apply {
            _uiState.value.apply {
                val currentTrack = musicFiles[currentMediaItemIndex]
                _uiState.value = (
                        this.copy(
                            index = currentMediaItemIndex,
                            currentTrack = currentTrack,
                            controllerState = controllerState.copy(
                                shuffleEnabled = shuffleModeEnabled,
                                repeatMode = getRepeatMode(repeatMode),
                                seekPosition = seekPosition,
                                trackLength = currentTrack.duration,
                                isNextEnabled = hasNextMediaItem(),
                                isPreviousEnabled = hasPreviousMediaItem()
                            )
                        )
                        )
                setupThumbnailAndBackground(currentTrack.path)
            }
        }
    }

    /**
     * Extract the thumbnail associated with media item also generate muted color using thumbnail
     * for background color.
     */
    private fun setupThumbnailAndBackground(path: String) {
        coroutineScope.launch {
            val bitmap = Utils.getAlbumImage(path)
            if (bitmap != null) {
                Palette.from(bitmap).generate { palette ->
                    val argb = palette?.getMutedColor(seed.value.toInt())
                    _uiState.value.apply {
                        if (argb != null) {
                            _uiState.value = (
                                    this.copy(
                                        backgroundColor = getSolidOpaqueColor(argb), // This ensure color is not transparent
                                        bitmap = bitmap
                                    )
                                    )
                        }
                    }
                }
            } else {
                /*
                If media does not have any thumbnail associated with it use defaultThumbnail.
                 */
                _uiState.value.apply {
                    _uiState.value = (
                            this.copy(
                                backgroundColor = seed.toArgb(),
                                bitmap = defaultThumbnail
                            )
                            )
                }
            }
        }
    }

    private fun getSolidOpaqueColor(argb: Int) = (argb and 0x00FFFFFF) or (0xFF shl 24)

    /**
     * Toggle between play and pause.
     * Plays music when in paused state.
     * Pause music when is playing.
     */
    fun onPlayPauseClick() {
        mediaController?.isPlaying?.let { isPlaying ->
            if (isPlaying) {
                mediaController?.pause()
            } else {
                mediaController?.play()
            }
        }
    }

    /**
     * Toggle between shuffle enabled and disabled.
     * Enables the shuffle mode when disabled.
     * Disables the shuffle mode when enabled.
     */
    fun onShuffleClick() {
        mediaController?.shuffleModeEnabled?.let {
            setShuffleEnabled(!it)
        }
    }

    /**
     * Set shuffleEnabled on [mediaController] and update the UI.
     */
    private fun setShuffleEnabled(shuffleEnabled: Boolean) {
        mediaController?.shuffleModeEnabled = shuffleEnabled
    }

    /**
     * Updated the repeat mode each time invoked.
     */
    fun onRepeatModeClick() {
        val repeatMode: Int = when (mediaController?.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_ALL
        }
        setRepeatMode(repeatMode)
    }

    /**
     * Set repeatMode on [mediaController] and update the UI.
     */
    private fun setRepeatMode(repeatMode: Int) {
        mediaController?.repeatMode = repeatMode
    }

    /**
     * Maps the repeat mode form [ExoPlayer] library to [RepeatMode].
     */
    private fun getRepeatMode(repeatMode: Int): RepeatMode {
        return when (repeatMode) {
            Player.REPEAT_MODE_OFF -> RepeatMode.NONE
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            else -> RepeatMode.ALL
        }
    }

    /**
     * Seek to the next media item available in playlist.
     */
    fun onNextClick() {
        mediaController?.seekToNextMediaItem()
    }

    /**
     * Seek to the previous item available in playlist.
     */
    fun onPreviousClick() {
        mediaController?.seekToPreviousMediaItem()
    }

    /**
     * Update the seek position of current media playback to [position].
     */
    fun onSeekPositionChangeFinished(position: Long) {
        mediaController?.seekTo(position)
        onSeekPositionChange(position)
    }

    /**
     * Update the seek position for UI.
     */
    private fun onSeekPositionChange(seekPosition: Long) {
        _uiState.value.apply {
            _uiState.value =
                (this.copy(controllerState = controllerState.copy(seekPosition = seekPosition)))
        }
    }

    fun onFileDeleted(index: Int) {
        musicFiles.removeAt(index)
        mediaItems.removeAt(index)
        CoroutineScope(Dispatchers.Main).launch {
            mediaController?.setMediaItems(mediaItems)
            if (index in 0..<mediaItems.size) {
                mediaController?.seekTo(index % mediaItems.size, 0)
                updateCurrentTrack()
            } else {
                _uiState.value.apply {
                    _uiState.value = (this.copy(currentTrack = MusicFile.default(), index = -1))
                }
            }
        }
    }
}