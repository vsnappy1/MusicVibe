package com.randos.domain.repository

import com.randos.domain.model.MusicFile

/**
 * Repository for managing music files.
 */
interface MusicRepository {

    /**
     * Retrieves a list of available music files.
     * @return A list of [MusicFile] objects.
     */
    suspend fun getMusicFiles(): List<MusicFile>

    /**
     * Deletes a specified music file.
     * @param musicFile The [MusicFile] to be deleted.
     * @return `true` if the deletion was successful, `false` otherwise.
     */
    suspend fun delete(musicFile: MusicFile): Boolean
}