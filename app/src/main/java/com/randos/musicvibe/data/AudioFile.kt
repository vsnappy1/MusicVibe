package com.randos.musicvibe.data


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