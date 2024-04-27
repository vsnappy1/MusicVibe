package com.randos.music_player.di

import android.app.Application
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class MediaModule {

    /**
     * Provides a singleton instance of ExoPlayer scoped to ViewModel.
     * @param application The application context used to build ExoPlayer instance.
     * @return A singleton instance of ExoPlayer.
     */
    @Provides
    @ViewModelScoped
    fun provideExoPlayer(application: Application): ExoPlayer {
        return ExoPlayer.Builder(application)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
    }
}