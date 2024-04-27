package com.randos.core.data.model

data class MusicFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val dateAdded: Long,
    val genre: String?,
) {
    companion object {
        fun default(): MusicFile {
            return MusicFile(
                id = -1,
                title = "",
                artist = "",
                album = "",
                duration = 1000,
                path = "",
                dateAdded = 0,
                genre = null
            )
        }
    }
}

