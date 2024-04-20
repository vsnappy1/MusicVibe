package com.randos.musicvibe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

private const val TAG = "IoUtils"

object IoUtils {

    fun getAlbumImage(path: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val audioImage = retriever.embeddedPicture
        retriever.release()
        return try {
            BitmapFactory.decodeByteArray(audioImage, 0, audioImage?.size ?: 0)
        } catch (e: Exception) {
            Log.e(TAG, "getAlbumImage: $e")
            return null
        }
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