package com.randos.core.di

import android.content.Context
import com.randos.core.data.MusicScanner
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
}