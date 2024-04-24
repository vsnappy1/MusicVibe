package com.randos.music_player.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer

/**
 * A Singleton class that holds the instance of [ExoPlayer].
 */
internal object MusicVibeExoPlayer {

    private lateinit var instance: ExoPlayer

    /**
     * @return Singleton instance of [ExoPlayer].
     */
    fun getInstance(context: Context): ExoPlayer {
        if (this::instance.isInitialized) return instance
        instance = ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
        return instance
    }
}