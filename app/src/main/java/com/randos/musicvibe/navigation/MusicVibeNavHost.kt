package com.randos.musicvibe.navigation

import android.net.Uri
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.randos.music_player.presentation.screen.music_player.MusicPlayer
import com.randos.music_player.presentation.screen.music_player.MusicPlayerNavigationDestination
import com.randos.musicvibe.presentation.screen.track.TrackScreen
import com.randos.musicvibe.presentation.screen.track.TrackScreenNavigationDestination

@Composable
fun MusicVibeNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TrackScreenNavigationDestination.route
    ) {

        composable(TrackScreenNavigationDestination.route) {
            TrackScreen(
                onItemClick = { path ->
                    navController.navigate(
                        route = "${MusicPlayerNavigationDestination.route}/${Uri.encode(path)}"
                    )
                }
            )
        }

        composable(
            route = MusicPlayerNavigationDestination.routeWithParams,
            arguments = listOf(navArgument(MusicPlayerNavigationDestination.argument) {
                type = NavType.StringType
            }),
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) },
            popEnterTransition = { slideInVertically(initialOffsetY = { it }) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) { backStackEntry ->
            val filePath =
                backStackEntry.arguments?.getString(MusicPlayerNavigationDestination.argument)
                    .orEmpty()
            MusicPlayer(path = filePath)
        }


    }
}