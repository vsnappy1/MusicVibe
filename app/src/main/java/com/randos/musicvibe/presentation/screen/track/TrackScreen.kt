package com.randos.musicvibe.presentation.screen.track

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.core.data.MusicScanner
import com.randos.core.data.model.MusicFile
import com.randos.core.navigation.NavigationDestination
import com.randos.core.utils.Utils
import com.randos.core.utils.defaultPadding
import com.randos.music_player.presentation.screen.music_player.MusicPlayer
import com.randos.musicvibe.presentation.component.AlphabetSlider
import com.randos.musicvibe.presentation.component.MusicItem

object TrackScreenNavigationDestination : NavigationDestination {
    override val name: String = "Track"
    override val route: String = "/track"
}

data class TrackScreenUiState(
    val musicFiles: List<MusicFile> = emptyList(),
    val selectedIndex: Int? = 0,
)

/**
 * Screen to represent all tracks present on device.
 *
 * @param onItemClick Invoked when any of the items in list is clicked, the int parameter is the
 * index of selected item.
 * [TrackScreen] and [MusicPlayer] uses the same data source, that is [MusicScanner].
 */

@Composable
fun TrackScreen(
    onItemClick: (Int) -> Unit,
    viewModel: TrackViewModel = hiltViewModel<TrackViewModel>()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState(initial = TrackScreenUiState())
    val defaultMusicThumbnail by remember { mutableStateOf(Utils.getDefaultThumbnail(context)) }
    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .defaultPadding(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp),
        ) {
            uiState.musicFiles.let { items ->
                items(
                    count = items.size,
                    key = { items[it].id }) { index ->
                    MusicItem(
                        musicFile = items[index],
                        defaultMusicThumbnail = defaultMusicThumbnail,
                        onClick = {
                            onItemClick(index)
                        }
                    )
                }
            }

        }
        AlphabetSlider(modifier = Modifier.align(Alignment.CenterEnd),
            onSelectionChange = { viewModel.updateSelectedIndex(it) },
            /**
             * Setting selectedIndex to null when user finished scrolling, so that when user scrolls
             * and then navigates to a different screen and returns backs to this screen he/she/they
             * should see the same scroll state as he/she/they left before navigating.
             */
            onSelectionChangeFinished = { viewModel.updateSelectedIndex(null) })
    }

    LaunchedEffect(key1 = uiState.selectedIndex) {
        uiState.selectedIndex?.let { lazyListState.scrollToItem(it) }
    }
}

@Preview
@Composable
fun PreviewTrackScreen() {
    TrackScreen(onItemClick = { })
}