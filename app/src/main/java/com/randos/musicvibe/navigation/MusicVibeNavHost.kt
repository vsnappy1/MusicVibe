package com.randos.musicvibe.navigation

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.randos.music_player.presentation.screen.music_player.MusicPlayer
import com.randos.music_player.presentation.screen.music_player.MusicPlayerNavigationDestination
import com.randos.musicvibe.presentation.screen.track.TrackScreen
import com.randos.musicvibe.presentation.screen.track.TrackScreenNavigationDestination

/**
 * [MusicVibeNavHost] enables navigation in whole app.
 */
@Composable
fun MusicVibeNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TrackScreenNavigationDestination.route
    ) {

        composable(TrackScreenNavigationDestination.route) {
            TrackScreen(
                onBottomPlayerClick = {
                    navController.navigate(
                        route = MusicPlayerNavigationDestination.route
                    )
                }
            )
        }

        composable(
            route = MusicPlayerNavigationDestination.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) },
            popEnterTransition = { slideInVertically(initialOffsetY = { it }) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            MusicPlayer(onBack = {navController.popBackStack()})
        }
    }
}