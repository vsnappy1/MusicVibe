package com.randos.domain.model

/**
 * Represents a music file with metadata.
 *
 * @property id Unique identifier of the music file.
 * @property title Title of the music track.
 * @property artist Name of the artist.
 * @property album Name of the album the track belongs to.
 * @property duration Duration of the track in milliseconds.
 * @property path File path of the music file.
 * @property dateAdded Timestamp indicating when the file was added.
 * @property genre Optional genre of the music file.
 * @property size Size of the music file as a string.
 */
data class MusicFile(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val dateAdded: Long,
    val genre: String?,
    val size: String
) {
    companion object {
        /**
         * Provides a default empty [MusicFile] instance.
         *
         * @return A default [MusicFile] object with placeholder values.
         */
        fun default(): MusicFile {
            return MusicFile(
                id = "-1",
                title = "",
                artist = "",
                album = "",
                duration = 1000,
                path = "",
                dateAdded = 0,
                genre = null,
                size = ""
            )
        }
    }
}
