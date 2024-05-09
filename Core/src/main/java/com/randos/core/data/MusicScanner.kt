package com.randos.core.data

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.randos.core.R
import com.randos.core.data.model.MusicFile
import com.randos.core.utils.ApiLevelHelper
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

    private val TAG = "MusicScanner"

    val musicFiles = mutableListOf<MusicFile>()
    val mediaItems = mutableListOf<MediaItem>()

    init {
        scan()
    }

    fun scan(): Job{
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
                    genre = genre,
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
            Log.d(TAG, "Mapping to MediaItem completed. Items: ${mediaItems.size}")
        }
    }

    private val defaultArtworkUri =
        Uri.parse("android.resource://com.randos.musicvibe/" + R.drawable.default_music_thumbnail)

    /**
     * Retrieve media metadata and if artwork is not present returns 
     * [defaultArtworkUri] else returns null.
     */
    private fun getArtworkUri(albumId: Long): Uri? {
        /**
         * TODO the idea is to
         */
//        MediaMetadataRetriever().apply {
//            setDataSource(path)
//            if (embeddedPicture == null) return defaultArtworkUri
//        }
//        Log.d(TAG, "getArtworkUri: ${getAlbumArtUri(albumId)}")
        getAlbumArtUri(albumId)
        return null
    }

    fun getAlbumArtUri(albumId: Long): Uri {
        val uri = Uri.parse("content://media/external/audio/albumart")
        val u =  Uri.withAppendedPath(uri, albumId.toString())

        u.path?.let { Log.d(TAG, "getAlbumArtUri: ${u.userInfo}") }
        return u
    }
}