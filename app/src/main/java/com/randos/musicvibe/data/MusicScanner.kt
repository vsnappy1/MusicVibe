package com.randos.musicvibe.data

import android.content.Context
import android.provider.MediaStore

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

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA // Path to the file
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID).and(1))
                val title =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE).and(1))
                val artist =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST).and(1))
                val album =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM).and(1))
                val filePath =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA).and(1))

                val audioFile = AudioFile(id, title, artist, album, filePath)
                audioFiles.add(audioFile)
            }
            cursor.close()
        }

        return audioFiles
    }
}