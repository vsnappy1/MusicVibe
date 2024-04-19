package com.randos.musicvibe.data

data class AudioFile (
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val path: String
)