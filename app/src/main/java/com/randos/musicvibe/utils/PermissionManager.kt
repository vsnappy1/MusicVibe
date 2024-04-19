package com.randos.musicvibe.utils

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


/**
 * Permission manager manages the logic to check whether a given permission is Granted or not and
 * request any given permission.
 *
 * @author Vishal Kumar
 */
object PermissionManager {

    /**
     * Launches the permission request
     *
     * @param permission The name of the permission being checked (i.e. android.Manifest.permission.*).
     * @param onPermissionGranted Callback invoked if user grants the permission requested.
     * @param onPermissionDenied Callback invoked if user denies the permission requested.
     */
    fun ComponentActivity.launchPermissionRequest(
        permission: String,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    onPermissionGranted()
                } else {
                    onPermissionDenied()
                }
            }
        requestPermissionLauncher.launch(permission)
    }

    /**
     * Check if the permission for certain feature is granted or not
     *
     * @param permission The name of the permission being checked (i.e. android.Manifest.permission.*).
     * @param permissionGranted Callback invoked if permission was already granted.
     * @param permissionNotGranted Callback invoked if permission was not already granted.
     * @param showEducationalUi Callback invoked if we need show UI with rationale before requesting a permission.
     */
    fun ComponentActivity.checkPermission(
        permission: String,
        permissionGranted: () -> Unit,
        permissionNotGranted: () -> Unit,
        showEducationalUi: () -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted()
        } else if (shouldShowRequestPermissionRationale(permission)) {
            showEducationalUi()
        } else {
            permissionNotGranted()
        }
    }
}