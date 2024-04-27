package com.randos.core.data

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.MediaItem
import com.randos.core.data.model.MusicFile
import com.randos.core.utils.ApiLevelHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * MusicScanner is a helper class which helps in getting audio files present on device.
 *
 * @author Vishal Kumar
 */
class MusicScanner(private val context: Context) {

    private val TAG = "MusicScanner"

    val musicFiles = mutableListOf<MusicFile>()
    val mediaItems = mutableListOf<MediaItem>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
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
            MediaStore.Audio.Media.DATA // Path to the file
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
                val id =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID) ?: 0)
                val title =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) ?: 0)
                val artist =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST) ?: 0)
                val album =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM) ?: 0)
                val duration =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) ?: 0)
                        .toLong()
                val dateAdded =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED) ?: 0)
                        .toLong()

                /**
                 * The genre of the audio file can only be accessed in Api level 30 or above
                 */
                val genre = if (ApiLevelHelper.isApiLevel30OrAbove()) {
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.GENRE) ?: 0)
                } else {
                    ""
                }
                val path =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA) ?: 0)

                val musicFile = MusicFile(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    path = path,
                    dateAdded = dateAdded,
                    genre = genre
                )
                musicFiles.add(musicFile)
            }
            musicFiles.sortBy { it.title }
            cursor.close()
            Log.d(TAG, "Media scanning completed. Items: ${musicFiles.size}")
        }
    }

    /**
     * Maps [MusicFile] to [MediaItem] for all items in [musicFiles] and store them in [mediaItems].
     */
    private fun getMediaItems() {
        mediaItems.addAll(musicFiles.map { MediaItem.fromUri(it.path) })
        Log.d(TAG, "Mapping to MediaItem completed. Items: ${mediaItems.size}")
    }
}