package com.randos.musicvibe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.randos.data.R
import androidx.core.graphics.createBitmap
import com.randos.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.coroutines.coroutineContext

object Utils {
    private lateinit var defaultThumbnail: Bitmap

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
                Logger.e( "Failed to get thumbnail for music file at path: $path")
                null
            }
        }.await()
    }

    fun getDefaultThumbnail(context: Context): Bitmap {
        if (this::defaultThumbnail.isInitialized) return defaultThumbnail
        defaultThumbnail = generateBitmapFromDrawable(context, R.drawable.default_music_thumbnail)
        return defaultThumbnail
    }

    private fun generateBitmapFromDrawable(
        context: Context,
        @DrawableRes drawableResourceId: Int
    ): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableResourceId)
        drawable?.apply {
            val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
        return createBitmap(10, 10)
    }
}