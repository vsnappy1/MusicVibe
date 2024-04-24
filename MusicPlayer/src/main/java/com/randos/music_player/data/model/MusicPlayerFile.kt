package com.randos.music_player.data.model

import android.graphics.Bitmap

data class MusicPlayerFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val thumbnail: Bitmap
)