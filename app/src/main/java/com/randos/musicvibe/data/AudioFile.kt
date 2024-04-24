package com.randos.musicvibe.data

import android.graphics.Bitmap
import com.randos.music_player.data.model.MusicPlayerFile

data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val dateAdded: Int,
    val genre: String?,
    val path: String
)

fun AudioFile.toMusicPlayerFile(thumbnail: Bitmap): MusicPlayerFile {
    return MusicPlayerFile(id, title,artist, album, duration.toLong(), path, thumbnail)
}