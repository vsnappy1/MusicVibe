package com.randos.data.di

import androidx.activity.result.ActivityResultLauncher
import com.randos.data.manager.DataStoreManagerImpl
import com.randos.data.manager.MusicPlayerImpl
import com.randos.data.manager.PermissionManagerImpl
import com.randos.domain.manager.DataStoreManager
import com.randos.domain.manager.MusicPlayer
import com.randos.domain.manager.PermissionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindMusicPlayer(musicPlayer: MusicPlayerImpl): MusicPlayer

    @Binds
    abstract fun bindPermissionManager(permissionManager: PermissionManagerImpl): PermissionManager<ActivityResultLauncher<String>>

    @Binds
    abstract fun bindDataStoreManager(dataStoreManager: DataStoreManagerImpl): DataStoreManager
}