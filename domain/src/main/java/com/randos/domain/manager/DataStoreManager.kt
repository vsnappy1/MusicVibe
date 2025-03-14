package com.randos.domain.manager

import com.randos.domain.type.RepeatMode

/**
 * Manages persistent storage of music player settings and last played music details.
 */
interface DataStoreManager {

    /**
     * Enables or disables shuffle mode in the music player.
     * @param enabled `true` to enable shuffle, `false` to disable it.
     */
    fun setShuffleEnabled(enabled: Boolean)

    /**
     * Retrieves the current shuffle mode status.
     * @return `true` if shuffle mode is enabled, `false` otherwise.
     */
    fun getShuffleEnabled(): Boolean

    /**
     * Sets the repeat mode for the music player.
     * @param repeatMode The [RepeatMode] to be set.
     */
    fun setRepeatMode(repeatMode: RepeatMode)

    /**
     * Retrieves the current repeat mode of the music player.
     * @return The current [RepeatMode] setting.
     */
    fun getRepeatMode(): RepeatMode

    /**
     * Stores the details of the last played music track.
     * @param id The unique identifier of the last played music file.
     * @param playedDuration The duration (in milliseconds) the track was played before stopping.
     */
    fun storeLastPlayedMusicDetails(id: String, playedDuration: Long)

    /**
     * Retrieves the details of the last played music track.
     * @return A [Pair] containing the track ID and the last played duration in milliseconds.
     */
    fun getLastPlayedMusicDetails(): Pair<String, Long>
}
