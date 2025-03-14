package com.randos.data.model

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.randos.domain.model.MusicFile

private const val ID = "id"
private const val TITLE = "title"
private const val ARTIST = "artist"
private const val ALBUM = "album"
private const val DURATION = "duration"
private const val PATH = "path"
private const val DATE_ADDED = "date_added"
private const val GENRE = "genre"
private const val SIZE = "size"

/**
 * Maps [MusicFile] to [MediaItem].
 */
internal fun MusicFile.toMediaItem(): MediaItem {
    val bundle = Bundle().apply {
        putString(ID, id)
        putString(TITLE, title)
        putString(ARTIST, artist)
        putString(ALBUM, album)
        putLong(DURATION, duration)
        putString(PATH, path)
        putLong(DATE_ADDED, dateAdded)
        putString(GENRE, genre)
        putString(SIZE, size)
    }

    val metadata = MediaMetadata.Builder()
        .setExtras(bundle)
        .build()

    return MediaItem.Builder()
        .setMediaId(id)
        .setUri(path)
        .setMediaMetadata(metadata)
        .build()
}

/**
 * Maps [MediaItem] to [MusicFile].
 */
internal fun MediaItem.toMusicFile(): MusicFile {
    val bundle = mediaMetadata.extras!!
    return MusicFile(
        bundle.getString(ID, "-1"),
        bundle.getString(TITLE, "Title"),
        bundle.getString(ARTIST, "Artist"),
        bundle.getString(ALBUM, "Album"),
        bundle.getLong(DURATION, 0),
        bundle.getString(PATH, "Path"),
        bundle.getLong(DATE_ADDED, 0),
        bundle.getString(GENRE, "Genre"),
        bundle.getString(SIZE, "Size"),
    )
}