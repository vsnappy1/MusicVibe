package com.randos.musicvibe

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.randos.musicvibe.navigation.MusicVibeNavHost
import com.randos.musicvibe.presentation.theme.MusicVibeTheme
import com.randos.musicvibe.utils.PermissionManager.checkPermission
import com.randos.musicvibe.utils.PermissionManager.launchPermissionRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /*
    TODO Click on notification takes to app player
    TODO implement room database to improve performance for read media
    TODO implement broadcast receiver and a service to listen to storage changes
    TODO add get info about track and share media option in menu options
    TODO create a settings screen where user can select what folders to scan for media items
    TODO Create playback controller Widget
    TODO Create Album Screen
    TODO Add repeat and shuffle in notification.
    ...
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        This allows the app to use full screen, creates an amazing experience.
         */
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )

        checkForReadStoragePermission {
            setContent {
                MusicVibeTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MusicVibeNavHost(navController = rememberNavController())
                    }
                }
            }
        }
    }

    private fun checkForReadStoragePermission(permissionGranted: () -> Unit) {
        var permission = Manifest.permission.READ_EXTERNAL_STORAGE

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_AUDIO
        }

        checkPermission(
            permission = permission,
            permissionGranted = permissionGranted,
            permissionNotGranted = {
                launchPermissionRequest(permission = permission,
                    onPermissionGranted = {
                        permissionGranted()
                        /*
                        Rescan storage since app just got the read permission.
                         */
                        (application as MusicVibeApplication).musicVibeMediaController.rescan()
                    },
                    onPermissionDenied = {})
            },
            showEducationalUi = {}
        )
    }
}