package com.randos.core.data

import android.content.Context
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.randos.core.data.model.MusicFile
import com.randos.core.utils.ApiLevelHelper
import com.randos.core.utils.Utils
import com.randos.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * MusicScanner is a helper class which helps in getting audio files present on device.
 *
 * @author Vishal Kumar
 */
class MusicScanner(private val context: Context) {

    val musicFiles = mutableListOf<MusicFile>()
    val mediaItems = mutableListOf<MediaItem>()

    init {
        scan()
    }

    fun scan(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            getAllMusicFiles()
            getMediaItems()
        }
    }

    /**
     * Find audio files present on device and inflates [musicFiles].
     */
    private fun getAllMusicFiles() {

        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, // Path to the file
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE,
        )

        /**
         * The genre of the audio file can only be accessed in Api level 30 or above
         */
        if (ApiLevelHelper.isApiLevel30OrAbove()) {
            projection.add(MediaStore.Audio.Media.GENRE)
        }

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            null,
            null,
            null
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                cursor.apply {
                    val idColumnIndex = getColumnIndex(MediaStore.Audio.Media._ID)
                    val titleColumnIndex = getColumnIndex(MediaStore.Audio.Media.TITLE)
                    val artistColumnIndex = getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val albumColumIndex = getColumnIndex(MediaStore.Audio.Media.ALBUM)
                    val durationColumIndex = getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val dateAddedColumnIndex = getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                    val dataColumIndex = getColumnIndex(MediaStore.Audio.Media.DATA)
                    val sizeColumIndex = getColumnIndex(MediaStore.Audio.Media.SIZE)

                    val id = getLong(idColumnIndex)
                    val title = getString(titleColumnIndex)
                    val artist = getString(artistColumnIndex)
                    val album = getString(albumColumIndex)
                    val duration = getInt(durationColumIndex).toLong()
                    val path = getString(dataColumIndex)
                    val dateAdded = getInt(dateAddedColumnIndex).toLong()
                    val size = getLong(sizeColumIndex)

                    /**
                     * The genre of the audio file can only be accessed in Api level 30 or above
                     */
                    val genre = if (ApiLevelHelper.isApiLevel30OrAbove()) {
                        val genreColumnIndex = getColumnIndex(MediaStore.Audio.Media.GENRE)
                        getString(genreColumnIndex)
                    } else {
                        ""
                    }

                    val musicFile = MusicFile(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        path = path,
                        dateAdded = dateAdded,
                        genre = genre,
                        size = Utils.toReadableSize(size)
                    )
                    musicFiles.add(musicFile)
                }

            }
            musicFiles.sortBy { it.title }
            cursor.close()
            Logger.i(this@MusicScanner, "Media scanning completed. Items: ${musicFiles.size}")
        }
    }

    /**
     * Maps [MusicFile] to [MediaItem] for all items in [musicFiles] and store them in [mediaItems].
     */
    private fun getMediaItems() {
        CoroutineScope(Dispatchers.Default).launch {
            mediaItems.addAll(musicFiles.map {
                MediaItem.Builder()
                    .setMediaId("${it.id}")
                    .setUri(it.path)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(it.artist)
                            .setTitle(it.title)
                            .setAlbumTitle(it.album)
                            /**
                             * When null is passed to setArtworkUri it automatically gets
                             * the artwork associated with media file, else it sets the
                             * provided uri.
                             */
//                            .setArtworkUri(getArtworkUri(it.albumId))
                            .setGenre(it.genre)
                            .build()
                    )
                    .build()
            })
            Logger.i(this@MusicScanner, "Mapping to MediaItem completed. Items: ${mediaItems.size}")
        }
    }
}