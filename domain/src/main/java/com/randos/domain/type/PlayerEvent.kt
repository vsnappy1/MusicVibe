package com.randos.domain.type

import com.randos.domain.model.MusicFile

/**
 * Represents different events that can occur in a music player.
 */
sealed class PlayerEvent {

    /**
     * Event to start playing the music.
     */
    data object Play : PlayerEvent()

    /**
     * Event to pause the currently playing music.
     */
    data object Pause : PlayerEvent()

    /**
     * Event triggered when the music file changes.
     * @property musicFile The new music file to be played.
     */
    data class MusicChange(val musicFile: MusicFile) : PlayerEvent()

    /**
     * Event triggered when shuffle mode is enabled or disabled.
     * @property enabled True if shuffle mode is enabled, false otherwise.
     */
    data class ShuffleEnabled(val enabled: Boolean) : PlayerEvent()

    /**
     * Event triggered when the repeat mode changes.
     * @property repeatMode The new repeat mode setting.
     */
    data class RepeatMode(val repeatMode: com.randos.domain.type.RepeatMode) : PlayerEvent()
}