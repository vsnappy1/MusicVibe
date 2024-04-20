package com.randos.musicvibe

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.randos.musicvibe.data.MusicScanner
import com.randos.musicvibe.presentation.screen.track.TrackScreen
import com.randos.musicvibe.presentation.theme.MusicVibeTheme
import com.randos.musicvibe.utils.PermissionManager.checkPermission
import com.randos.musicvibe.utils.PermissionManager.launchPermissionRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicScanner: MusicScanner

    /*
    TODO Complete Track Screen
    TODO Implement Music Player
    TODO Create Album Screen
    ...
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForReadStoragePermission {
            setContent {
                MusicVibeTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TrackScreen()
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
                    onPermissionGranted = permissionGranted,
                    onPermissionDenied = {})
            },
            showEducationalUi = {}
        )
    }
}