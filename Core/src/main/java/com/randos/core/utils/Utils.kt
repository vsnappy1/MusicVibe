package com.randos.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.randos.core.R
import com.randos.core.data.model.MusicFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.coroutines.coroutineContext

private const val TAG = "Utils"

object Utils {

    /**
     * @param path Absolute path to the audio file
     * @return [MusicFile] or null when not found.
     */
    suspend fun getMusicFile(context: Context, path: String): MusicFile? {
        return CoroutineScope(coroutineContext).async(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(path)
                val embeddedPicture = retriever.embeddedPicture
                val title =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).orEmpty()
                val artist =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).orEmpty()
                val album =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM).orEmpty()
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong() ?: 0
                val previewImage = embeddedPicture?.let {
                    BitmapFactory.decodeByteArray(
                        embeddedPicture,
                        0,
                        embeddedPicture.size
                    )
                }?: generateBitmapFromDrawable(context, R.drawable.default_music_thumbnail)

                retriever.release()
                MusicFile(
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    path = path,
                    previewImage = previewImage
                )
            } catch (e: Exception) {
                Log.e(TAG, "getMusicFile: $e")
                null
            }
        }.await()
    }

    fun generateBitmapFromDrawable(context: Context, @DrawableRes drawableResourceId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableResourceId)
        drawable?.apply {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
        return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    }
}