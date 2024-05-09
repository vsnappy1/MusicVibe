package com.randos.music_player.utils

import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.randos.core.data.MusicScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A wrapper around [MediaController]
 */
class MusicVibeMediaController @Inject constructor(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val musicScanner: MusicScanner,
) {
    var mediaController: MediaController? = null
        private set

    init {
        preparePlayer()
    }

    /**
     * Rescans the storage and prepares the [mediaController]
     */
    fun rescan() {
        CoroutineScope(Dispatchers.IO).launch {
            musicScanner.scan().join()
            withContext(Dispatchers.Main) {
                mediaController?.apply {
                    prepare()
                    setMediaItems(musicScanner.mediaItems)
                }
            }
        }
    }

    private fun preparePlayer() {
        mediaControllerFuture.addListener({
            mediaController = mediaControllerFuture.get()
            mediaController?.apply {
                prepare()
                setMediaItems(musicScanner.mediaItems)
            }
        }, MoreExecutors.directExecutor())
    }
}