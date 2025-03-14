package com.randos.domain.manager

import com.randos.domain.model.MusicFile
import com.randos.domain.type.PlayerEvent
import com.randos.domain.type.RepeatMode
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a music player with playback controls and event handling.
 */
interface MusicPlayer {

    /**
     * Prepares the music player for playback.
     */
    suspend fun prepare()

    /**
     * Retrieves the currently playing.
     * @return The [MusicFile] currently loaded in the player, or `null` if no music is selected.
     */
    fun currentMusic(): MusicFile?

    /**
     * Starts or resumes music playback.
     */
    fun play()

    /**
     * Starts playback of the music at the specified index in the playlist.
     * @param index The position of the track in the playlist.
     */
    fun playAtIndex(index: Int)

    /**
     * Pauses the currently playing music.
     */
    fun pause()

    /**
     * Play next track in the playlist.
     */
    fun next()

    /**
     * Play previous track in the playlist.
     */
    fun previous()

    /**
     * Checks if the music player is currently playing a track.
     * @return `true` if a track is playing, `false` otherwise.
     */
    fun isPlaying(): Boolean

    /**
     * Checks if a next track is available in the playlist.
     * @return `true` if there is a next track, `false` otherwise.
     */
    fun isNextAvailable(): Boolean

    /**
     * Checks if a previous track is available in the playlist.
     * @return `true` if there is a previous track, `false` otherwise.
     */
    fun isPreviousAvailable(): Boolean

    /**
     * Enables or disables shuffle mode.
     * @param enabled `true` to enable shuffle, `false` to disable it.
     */
    fun setShuffleEnabled(enabled: Boolean)

    /**
     * Retrieves the current shuffle mode status.
     * @return `true` if shuffle mode is enabled, `false` otherwise.
     */
    fun getShuffleEnabled(): Boolean

    /**
     * Sets the repeat mode for playback.
     * @param repeatMode The [RepeatMode] to be set.
     */
    fun setRepeatMode(repeatMode: RepeatMode)

    /**
     * Retrieves the current repeat mode setting.
     * @return The current [RepeatMode].
     */
    fun getRepeatMode(): RepeatMode

    /**
     * Updates the current seek position of the track.
     * @param position The new playback position in milliseconds.
     */
    fun updateSeek(position: Long)

    /**
     * Provides a stream of player events such as play, pause, and track changes.
     * @return A [Flow] emitting [PlayerEvent]s.
     */
    fun playerEvent(): Flow<PlayerEvent>

    /**
     * Provides a stream of the current seek position updates.
     * @return A [Flow] emitting the playback position in milliseconds.
     */
    fun seekPosition(): Flow<Long>

    /**
     * Retrieves the total number of media items in the playlist.
     * @return The count of media items.
     */
    fun getMediaItemCount(): Int

    /**
     * Deletes the currently playing music file from the playlist.
     */
    suspend fun delete()

    /**
     * Shares the currently playing music track.
     */
    fun share()
}