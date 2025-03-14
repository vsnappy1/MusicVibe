package com.randos.domain.manager

/**
 * Interface for managing media-related permissions.
 *
 * @param T The type of the permission launcher, typically used for handling permission requests.
 */
interface PermissionManager<T> {

    /**
     * Requests permission to read media files from storage.
     */
    fun requestMediaReadPermission()

    /**
     * Requests permission to write media files to storage.
     */
    fun requestMediaWritePermission()

    /**
     * Checks whether the read media permission has been granted.
     * @return `true` if the permission is granted, `false` otherwise.
     */
    fun isMediaReadPermissionGranted(): Boolean

    /**
     * Checks whether the write media permission has been granted.
     * @return `true` if the permission is granted, `false` otherwise.
     */
    fun isMediaWritePermissionGranted(): Boolean

    /**
     * Sets the permission launcher for requesting read permission.
     * @param launcher The launcher used to handle permission requests.
     */
    fun setReadPermissionLauncher(launcher: T)

    /**
     * Sets the permission launcher for requesting write permission.
     * @param launcher The launcher used to handle permission requests.
     */
    fun setWritePermissionLauncher(launcher: T)
}