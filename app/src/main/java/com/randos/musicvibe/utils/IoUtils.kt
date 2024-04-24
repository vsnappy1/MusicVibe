package com.randos.musicvibe.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.coroutines.coroutineContext

private const val TAG = "IoUtils"

object IoUtils {

    /**
     * @param path Absolute path to the audio file
     * @return Bitmap thumbnail associated with an audio file or null when not found.
     */
    suspend fun getAlbumImage(path: String): Bitmap? {
        return CoroutineScope(coroutineContext).async(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(path)
                val audioImage = retriever.embeddedPicture
                retriever.release()
                BitmapFactory.decodeByteArray(audioImage, 0, audioImage?.size ?: 0)
            } catch (e: Exception) {
                Log.e(TAG, "getAlbumImage: $e")
                null
            }
        }.await()
    }
}