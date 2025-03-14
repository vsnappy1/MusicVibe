package com.randos.domain.type

/**
 * Represents the repeat modes available in the music player.
 */
enum class RepeatMode {

    /**
     * No repeat mode; the playlist or song will stop after finishing.
     */
    OFF,

    /**
     * Repeats the currently playing song.
     */
    ONE,

    /**
     * Repeats the entire playlist.
     */
    ALL
}