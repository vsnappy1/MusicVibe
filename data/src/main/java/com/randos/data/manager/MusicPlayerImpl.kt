package com.randos.data.manager

import androidx.activity.result.ActivityResultLauncher
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.randos.data.di.Dispatcher
import com.randos.data.model.toMediaControllerRepeat
import com.randos.data.model.toMediaItem
import com.randos.data.model.toMusicFile
import com.randos.data.model.toRepeatMode
import com.randos.domain.manager.DataStoreManager
import com.randos.domain.manager.MusicPlayer
import com.randos.domain.manager.PermissionManager
import com.randos.domain.model.MusicFile
import com.randos.domain.repository.MusicRepository
import com.randos.domain.type.PlayerEvent
import com.randos.domain.type.RepeatMode
import com.randos.logger.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MusicPlayerImpl @Inject constructor(
    private val mediaControllerListenableFuture: ListenableFuture<MediaController>,
    private val musicRepository: MusicRepository,
    private val permissionManager: PermissionManager<ActivityResultLauncher<String>>,
    private val dataStoreManager: DataStoreManager,
    @Dispatcher.IO private val dispatcher: CoroutineDispatcher,
    @Dispatcher.Main private val mainDispatcher: CoroutineDispatcher
) : MusicPlayer {

    companion object {
        private const val TAG = "MusicPlayerImpl"
    }

    private lateinit var mediaController: Player

    override suspend fun prepare() = withContext(dispatcher) {
        if (this@MusicPlayerImpl::mediaController.isInitialized) return@withContext
        mediaController = mediaControllerListenableFuture.get()
        if (!permissionManager.isMediaReadPermissionGranted()) {
            return@withContext
        }

        val mediaItems = musicRepository.getMusicFiles().map { it.toMediaItem() }

        if (mediaItems.isEmpty()) {
            Logger.i(TAG, "No media files found.")
            return@withContext
        }

        withContext(mainDispatcher) {
            mediaController.setMediaItems(mediaItems)
            mediaController.repeatMode = dataStoreManager.getRepeatMode().toMediaControllerRepeat()
            mediaController.shuffleModeEnabled = dataStoreManager.getShuffleEnabled()

            // Check if last played media item is present
            val (lastPayedMusicId, lastPlayerMusicDuration) = dataStoreManager.getLastPlayedMusicDetails()

            val lastPlayedMediaItem = mediaItems.indexOfFirst { it.mediaId == lastPayedMusicId }

            if (lastPlayedMediaItem != -1) {
                mediaController.seekTo(lastPlayedMediaItem, lastPlayerMusicDuration)
            }
        }
    }

    override fun currentMusic(): MusicFile? {
        return mediaController.currentMediaItem?.toMusicFile()
    }

    override fun play() {
        mediaController.play()
    }

    override fun playAtIndex(index: Int) {
        mediaController.seekTo(index, 0)
        mediaController.play()
    }

    override fun pause() {
        mediaController.pause()
    }

    override fun next() {
        mediaController.seekToNext()
    }

    override fun previous() {
        mediaController.seekToPrevious()
    }

    override fun isPlaying(): Boolean {
        return mediaController.isPlaying
    }

    override fun isNextAvailable(): Boolean {
        return mediaController.hasNextMediaItem()
    }

    override fun isPreviousAvailable(): Boolean {
        return mediaController.hasPreviousMediaItem()
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        mediaController.shuffleModeEnabled = enabled
    }

    override fun getShuffleEnabled(): Boolean {
        return mediaController.shuffleModeEnabled
    }

    override fun setRepeatMode(repeatMode: RepeatMode) {
        mediaController.repeatMode = repeatMode.toMediaControllerRepeat()
    }

    override fun getRepeatMode(): RepeatMode {
        return mediaController.repeatMode.toRepeatMode()
    }

    override fun updateSeek(position: Long) {
        mediaController.seekTo(position)
    }

    override fun playerEvent(): Flow<PlayerEvent> = callbackFlow {
        mediaController.addListener(object : Player.Listener {

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
                val repeat = repeatMode.toRepeatMode()
                dataStoreManager.setRepeatMode(repeat)
                trySend(PlayerEvent.RepeatMode(repeat))
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                dataStoreManager.setShuffleEnabled(shuffleModeEnabled)
                trySend(PlayerEvent.ShuffleEnabled(shuffleModeEnabled))
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    trySend(PlayerEvent.Play)
                } else {
                    // When media is paused, store its id and played duration as it will be used when app is launched again.
                    val playedDuration = mediaController.currentPosition
                    val id = mediaController.currentMediaItem?.mediaId.orEmpty()
                    dataStoreManager.storeLastPlayedMusicDetails(id, playedDuration)
                    trySend(PlayerEvent.Pause)
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                mediaItem?.let {
                    trySend(PlayerEvent.MusicChange(it.toMusicFile()))
                }
            }
        })
        awaitClose()
    }

    private var playbackSyncJob: Job? = null
    override fun seekPosition(): Flow<Long> = callbackFlow {
        playbackSyncJob?.cancel()
        playbackSyncJob = CoroutineScope(mainDispatcher).launch {
            while (playbackSyncJob?.isActive == true) {
                delay(100)
                trySend(mediaController.currentPosition)
            }
        }
        awaitClose()
    }

    override fun getMediaItemCount(): Int {
        return if (this::mediaController.isInitialized) mediaController.mediaItemCount else -1
    }

    override suspend fun delete() {
        currentMusic()?.let { musicRepository.delete(it) }
        mediaController.removeMediaItem(mediaController.currentMediaItemIndex)
        if (isNextAvailable()) {
            next()
        } else if (isPreviousAvailable()) {
            previous()
        }
    }

    override fun share() {
        TODO("Not yet implemented")
    }
}