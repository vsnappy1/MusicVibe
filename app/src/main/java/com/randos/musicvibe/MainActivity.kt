package com.randos.musicvibe

import android.graphics.Color
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /*
    TODO add lint check and CICD for PR
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