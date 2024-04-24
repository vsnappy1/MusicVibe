package com.randos.core.data.model

import android.graphics.Bitmap

data class MusicFile(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val previewImage: Bitmap
) {
    companion object {
        fun default(): MusicFile {
            return MusicFile(
                title = "",
                artist = "",
                album = "",
                duration = 1000,
                path = "",
                previewImage = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
            )
        }
    }
}

