package com.randos.data.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.randos.data.service.MusicSessionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object MediaModule {

    @Provides
    fun provideMediaController(context: Context): ListenableFuture<MediaController> {
        val sessionToken = SessionToken(context, ComponentName(context, MusicSessionService::class.java))
        return MediaController.Builder(context, sessionToken).buildAsync()
    }
}