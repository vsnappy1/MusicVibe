package com.randos.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.media3.common.Player
import com.randos.logger.Logger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Provides simpler methods for rest of the app to store and retrieve data stored in data store.
 *
 * @author Vishal Kumar
 */
class DataStoreManager(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val SHUFFLE_ENABLED_KEY = booleanPreferencesKey("shuffle_enabled")
        private val REPEAT_MODE_KEY = intPreferencesKey("repeat_mode")
        private val LAST_PLAYED_MUSIC_FILE_ID = longPreferencesKey("last_played_music_file_id")
        private val LAST_PLAYED_MUSIC_FILE_PLAYED_DURATION =
            longPreferencesKey("last_played_music_file_player_duration")
    }

    /**
     * Gets the stored shuffle enabled flag from data store as Boolean.
     */
    suspend fun getShuffleEnabled(): Boolean {
        return dataStore.data.map { preferences -> preferences[SHUFFLE_ENABLED_KEY] ?: false }
            .first()
    }

    /**
     * Stores the shuffle enabled flag into the data store.
     */
    suspend fun setShuffleEnabled(shuffleEnabled: Boolean) {
        Logger.i(this@DataStoreManager, "Storing shuffle enabled: $shuffleEnabled")
        dataStore.edit { preferences -> preferences[SHUFFLE_ENABLED_KEY] = shuffleEnabled }
    }

    /**
     * Gets the stored repeat mode from data store as Integer.
     *
     * I follow same scheme as [Player] where
     *
     * 0 represents REPEAT_MODE_OFF
     *
     * 1 represents REPEAT_MODE_ONE
     *
     * 2 represents REPEAT_MODE_ALL
     *
     * @return The stored repeat mode.
     */
    suspend fun getRepeatMode(): Int {
        return dataStore.data.map { preferences -> preferences[REPEAT_MODE_KEY] ?: 2 }.first()
    }

    /**
     * Stores the repeat mode into the data store.
     */
    suspend fun setRepeatMode(repeatMode: Int) {
        Logger.i(this@DataStoreManager, "Storing repeat mode: $repeatMode")
        require(repeatMode >= 0 || repeatMode <= 2) { "repeatMode should be between 0 to 2." }
        dataStore.edit { preferences -> preferences[REPEAT_MODE_KEY] = repeatMode }
    }

    /**
     * Stores the last played music file id and played duration into the data store.
     */
    suspend fun setLastPlayedMusicFileDetails(id: Long, playedDuration: Long) {
        Logger.i(this@DataStoreManager, "Storing last played music file details: (id: $id, playedDuration: $playedDuration)")
        dataStore.edit { preferences -> preferences[LAST_PLAYED_MUSIC_FILE_ID] = id }
        dataStore.edit { preferences ->
            preferences[LAST_PLAYED_MUSIC_FILE_PLAYED_DURATION] = playedDuration
        }
    }

    /**
     * Gets the last played music file `id` and `played_duration` from the data store.
     *
     * If `id` not found returns -1
     * If `played_duration` not found returns 0
     *
     * @return a [Pair] first value is `id` and second value is `played_duration` of music file.
     */
    suspend fun getLastPlayedMusicFileDetails(): Pair<Long, Long> {
        return Pair(
            dataStore.data.map { preferences ->
                preferences[LAST_PLAYED_MUSIC_FILE_ID] ?: -1
            }.first(),
            dataStore.data.map { preferences ->
                preferences[LAST_PLAYED_MUSIC_FILE_PLAYED_DURATION] ?: 0
            }.first())
    }
}