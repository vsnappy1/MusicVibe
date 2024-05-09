package com.randos.music_player.di

import android.app.Application
import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.randos.core.data.MusicScanner
import com.randos.music_player.utils.MusicVibeMediaController
import com.randos.music_player.service.MusicSessionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaModule {

    @Provides
    fun providesMediaController(application: Application): ListenableFuture<MediaController> {
        val sessionToken =
            SessionToken(application, ComponentName(application, MusicSessionService::class.java))
        return MediaController.Builder(application, sessionToken).buildAsync()
    }

    @Provides
    @Singleton
    fun provideMusicVibeMediaController(
        mediaControllerFuture: ListenableFuture<MediaController>,
        musicScanner: MusicScanner,
    ): MusicVibeMediaController {
        return MusicVibeMediaController(mediaControllerFuture, musicScanner)
    }
}