package com.randos.musicvibe.di

import android.content.Context
import android.graphics.Bitmap
import com.randos.musicvibe.R
import com.randos.musicvibe.data.MusicScanner
import com.randos.musicvibe.utils.IoUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MusicModule {

    @Singleton
    @Provides
    fun provideMusicScanner(@ApplicationContext context: Context): MusicScanner {
        return MusicScanner(context)
    }

    @Singleton
    @Provides
    fun provideDefaultMusicThumbnail(@ApplicationContext context: Context): Bitmap{
        return IoUtils.generateBitmapFromDrawable(context, R.drawable.default_music_thumbnail)
    }

}