package com.randos.musicvibe

import android.app.Application
import com.randos.music_player.utils.MusicVibeMediaController
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MusicVibeApplication: Application() {

    @Inject lateinit var musicVibeMediaController: MusicVibeMediaController

}