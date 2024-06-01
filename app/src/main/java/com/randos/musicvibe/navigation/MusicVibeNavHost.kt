package com.randos.musicvibe.navigation

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
                onItemClick = { index ->
                    navController.navigate(
                        route = "${MusicPlayerNavigationDestination.route}/$index"
                    )
                }
            )
        }

        composable(
            route = MusicPlayerNavigationDestination.routeWithParams,
            /**
             * This argument is directly used in viewmodel, so we don't need to pass it as an
             * argument to [MusicPlayer] composable function.
             */
            arguments = listOf(navArgument(MusicPlayerNavigationDestination.param) { NavType.IntType }),
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) },
            popEnterTransition = { slideInVertically(initialOffsetY = { it }) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            MusicPlayer(onBack = {navController.popBackStack()})
        }
    }
}