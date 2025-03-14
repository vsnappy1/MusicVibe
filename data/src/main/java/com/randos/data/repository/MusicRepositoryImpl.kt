package com.randos.data.repository

import android.content.Context
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import com.randos.data.di.Dispatcher
import com.randos.data.utils.ApiLevelHelper
import com.randos.data.utils.Utils
import com.randos.domain.manager.PermissionManager
import com.randos.domain.model.MusicFile
import com.randos.domain.repository.MusicRepository
import com.randos.logger.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MusicRepositoryImpl @Inject constructor(
    private val context: Context,
    @Dispatcher.IO private val dispatcher: CoroutineDispatcher,
    private val permissionManager: PermissionManager<ActivityResultLauncher<String>>
) : MusicRepository {

    companion object {
        private const val TAG = "MusicRepositoryImpl"
    }

    override suspend fun getMusicFiles(): List<MusicFile> = withContext(dispatcher) {
        if (permissionManager.isMediaReadPermissionGranted()) {
            getMusicFiles(context)
        } else {
            emptyList()
        }
    }

    override suspend fun delete(musicFile: MusicFile): Boolean = withContext(dispatcher) {
        deleteMediaFile(musicFile)
    }

    /**
     * Deletes a media record from the MediaStore using the provided media ID.
     */
    private fun deleteMediaFile(musicFile: MusicFile): Boolean {
        try {
            val deleted = context.contentResolver.delete(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media._ID + " = ?",
                arrayOf(musicFile.id)
            )
            if (deleted > 0) {
                Logger.i(TAG, "File deleted at path ${musicFile.path}.")
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to delete file at path ${musicFile.path}.", e)
        }
        return false
    }

    /**
     * Find audio files present on device.
     */
    private fun getMusicFiles(context: Context): List<MusicFile> {
        val musicFiles = mutableListOf<MusicFile>()

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
                        id = id.toString(),
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
            cursor.close()
            musicFiles.sortBy { it.title }
            Logger.i(TAG, "Media scanning completed. Items: ${musicFiles.size}")
        }
        return musicFiles
    }
}