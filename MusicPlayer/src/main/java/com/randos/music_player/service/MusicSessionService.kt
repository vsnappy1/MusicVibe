package com.randos.music_player.service

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicSessionService: MediaSessionService() {
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        TODO("Not yet implemented")
    }
}