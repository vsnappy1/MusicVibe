package com.randos.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.media3.common.Player
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
    }

    /**
     * Gets the stored shuffle enabled flag from data store as Boolean.
     */
    suspend fun getShuffleEnabled(): Boolean {
        return dataStore.data.map { preferences -> preferences[SHUFFLE_ENABLED_KEY] ?: false }
            .first()
    }

    /**
     * Sets the shuffle enabled flag into the data store.
     */
    suspend fun setShuffleEnabled(shuffleEnabled: Boolean) {
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
     * Sets the repeat mode into the data store.
     */
    suspend fun setRepeatMode(repeatMode: Int) {
        require(repeatMode >= 0 || repeatMode <= 2) { "repeatMode should be between 0 to 2." }
        dataStore.edit { preferences -> preferences[REPEAT_MODE_KEY] = repeatMode }
    }
}