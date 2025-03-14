package com.randos.data.manager

import android.content.SharedPreferences
import com.randos.domain.manager.DataStoreManager
import com.randos.domain.type.RepeatMode
import androidx.core.content.edit
import javax.inject.Inject

internal class DataStoreManagerImpl @Inject constructor(private val sharedPreferences: SharedPreferences) : DataStoreManager {

    companion object {
        private const val SHUFFLE_ENABLED = "shuffle_enabled"
        private const val REPEAT_MODE = "repeat_mode"
        private const val LAST_PLAYED_MUSIC_ID = "last_player_music_id"
        private const val LAST_PLAYED_MUSIC_DURATION = "last_player_music_duration"
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        sharedPreferences.edit() { putBoolean(SHUFFLE_ENABLED, enabled) }
    }

    override fun getShuffleEnabled(): Boolean {
        return sharedPreferences.getBoolean(SHUFFLE_ENABLED, false)
    }

    override fun setRepeatMode(repeatMode: RepeatMode) {
        sharedPreferences.edit() { putString(REPEAT_MODE, repeatMode.name) }
    }

    override fun getRepeatMode(): RepeatMode {
        return RepeatMode.valueOf(sharedPreferences.getString(REPEAT_MODE, RepeatMode.OFF.name)!!)
    }

    override fun storeLastPlayedMusicDetails(id: String, playedDuration: Long) {
        sharedPreferences.edit() {
            putString(LAST_PLAYED_MUSIC_ID, id)
            putLong(LAST_PLAYED_MUSIC_DURATION, playedDuration)
        }
    }

    override fun getLastPlayedMusicDetails(): Pair<String, Long> {
        val id = sharedPreferences.getString(LAST_PLAYED_MUSIC_ID, "0").orEmpty()
        val duration = sharedPreferences.getLong(LAST_PLAYED_MUSIC_DURATION, 0)
        return Pair(id, duration)
    }
}