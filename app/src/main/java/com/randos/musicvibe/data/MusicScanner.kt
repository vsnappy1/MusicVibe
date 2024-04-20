package com.randos.musicvibe.data

import android.content.Context
import android.provider.MediaStore
import com.randos.musicvibe.utils.ApiLevelHelper

/**
 * MusicScanner is a helper class which helps in getting audio files present on device.
 *
 * @author Vishal Kumar
 */
class MusicScanner(private val context: Context) {

    private val audioFiles = mutableListOf<AudioFile>()

    /**
     * Find audio files present on device.
     *
     * @return The list of all audio files present on device.
     */
    fun getAllAudioFiles(): List<AudioFile> {

        if (audioFiles.isNotEmpty()) return audioFiles

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
                val dateAdded =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED) ?: 0)

                /**
                 * The genre of the audio file can only be accessed in Api level 30 or above
                 */
                val genre = if (ApiLevelHelper.isApiLevel30OrAbove()) {
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.GENRE) ?: 0)
                } else {
                    ""
                }
                val filePath =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA) ?: 0)

                val audioFile = AudioFile(
                    id,
                    title,
                    artist,
                    album,
                    duration,
                    dateAdded,
                    genre,
                    filePath
                )

                audioFiles.add(audioFile)
            }
            cursor.close()
        }

        return audioFiles
    }
}