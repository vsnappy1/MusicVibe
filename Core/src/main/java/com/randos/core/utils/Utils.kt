package com.randos.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.randos.core.R
import com.randos.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.Locale
import kotlin.coroutines.coroutineContext
import kotlin.math.log10
import kotlin.math.pow

object Utils {

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
                Logger.e(this@Utils, "Failed to get thumbnail for music file at path: $path", e)
                null
            }
        }.await()
    }

    private lateinit var defaultThumbnail: Bitmap

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

    /**
     * Function to convert a file size in bytes to a human-readable format.
     *
     * This function takes a size in bytes represented as a `Long` and converts it into a
     * more readable string format using appropriate units (B, KB, MB, GB, TB). The conversion
     * is done by determining the appropriate unit based on the size and formatting the result
     * to two decimal places.
     *
     * @return A `String` representing the size in a human-readable format with two decimal places.
     *
     * @throws IllegalArgumentException if the size is negative.
     */
    fun toReadableSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return String.format(
            Locale.getDefault(),
            "%.2f %s",
            bytes / 1024.0.pow(digitGroups.toDouble()),
            units[digitGroups]
        )
    }
}