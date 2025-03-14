package com.randos.musicvibe.presentation.screen.music_player

import android.app.Activity
import android.content.ContentUris
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.domain.model.MusicFile
import com.randos.logger.Logger
import com.randos.musicvibe.navigation.NavigationDestinationWithParams
import com.randos.musicvibe.presentation.component.BouncyComposable
import com.randos.musicvibe.presentation.component.DeleteTrackConfirmationLayout
import com.randos.musicvibe.presentation.component.MusicPlaybackController
import com.randos.musicvibe.presentation.component.MusicPlaybackControllerState
import com.randos.musicvibe.presentation.component.TrackDetailLayout
import com.randos.musicvibe.utils.Utils
import com.randos.musicvibe.utils.defaultPadding
import dagger.hilt.android.UnstableApi

object MusicPlayerNavigationDestination : NavigationDestinationWithParams {
    override val name: String = "Music Player"
    override val route: String = "/music_player"
    override val param: String = "index"
}

@Immutable
data class MusicPlayerState(
    val index: Int = 0,
    val bitmap: Bitmap? = null,
    val backgroundColor: Int? = null,
    val currentTrack: MusicFile = MusicFile.default(),
    val controllerState: MusicPlaybackControllerState = MusicPlaybackControllerState()
)

@OptIn(UnstableApi::class)
@Composable
fun MusicPlayer(
    onBack: () -> Unit
) {
    val viewModel: MusicPlayerViewModel = hiltViewModel()
    val state by viewModel.uiState.observeAsState(MusicPlayerState())
    val context = LocalContext.current

    var isTrackDetailLayoutVisible by remember { mutableStateOf(false) }
    var isDeleteConfirmationLayoutVisible by remember { mutableStateOf(false) }
    val mediaWriteRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.delete()
            } else {
                Logger.i("Media Write permission denied")
            }
        }
    val deleteMediaIntentSenderLauncher = deleteMediaIntentSenderLauncher {
        viewModel.onFileDeleted()
    }

    /*
      When there is only one music file and user decides to delete it, index is set to -1 and this
      this sends user back to track screen.
     */
    if (state.index == -1) {
        onBack()
    }

    val backgroundColor =
        state.backgroundColor?.let { Color(it) } ?: MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .background(gradientBackgroundColor(backgroundColor))
            .fillMaxSize()
            .defaultPadding(top = 48.dp, bottom = 64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton(onBack = onBack)
            OptionMenu(
                onTrackDetailsClick = { isTrackDetailLayoutVisible = true },
                onShareClick = {},
                onDeleteClick = { isDeleteConfirmationLayoutVisible = true }
            )
        }

        PreviewImageTitleArtist(
            modifier = Modifier.align(Alignment.Center),
            isPlaying = state.controllerState.isPlaying,
            previewImage = state.bitmap,
            title = state.currentTrack.title,
            artist = state.currentTrack.artist,
        )

        MusicPlaybackController(
            modifier = Modifier.align(Alignment.BottomCenter),
            state = state.controllerState,
            onPlayPauseClick = { viewModel.onPlayPauseClick() },
            onShuffleClick = { viewModel.onShuffleClick() },
            onRepeatModeClick = { viewModel.onRepeatModeClick() },
            onNextClick = { viewModel.onNextClick() },
            onPreviousClick = { viewModel.onPreviousClick() },
            onValueChangeFinished = {
                viewModel.onSeekPositionChangeFinished(it)
            }
        )

        if (isTrackDetailLayoutVisible) {
            Dialog(onDismissRequest = { isTrackDetailLayoutVisible = false }) {
                TrackDetailLayout(musicFile = state.currentTrack)
            }
        }

        if (isDeleteConfirmationLayoutVisible) {
            Dialog(onDismissRequest = { isDeleteConfirmationLayoutVisible = false }) {
                DeleteTrackConfirmationLayout(
                    onCancelClick = { isDeleteConfirmationLayoutVisible = false },
                    onDeleteClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val uri = ContentUris.withAppendedId(
                                MediaStore.Audio.Media.getContentUri("external"),
                                state.currentTrack.id.toLong()
                            )
                            val intent =
                                MediaStore.createDeleteRequest(
                                    context.contentResolver,
                                    listOf(uri)
                                ).intentSender
                            deleteMediaIntentSenderLauncher.launch(IntentSenderRequest.Builder(intent).build())
                        } else {
                            viewModel.delete()
                        }
                        isDeleteConfirmationLayoutVisible = false
                    },
                )
            }
        }
    }
}

/**
 * Creates and returns a [ManagedActivityResultLauncher] that is used to handle the result of
 * an intent sender request for deleting media files on devices running API level 30 or higher.
 *
 * @param onMediaFileDeleted The callback to be invoked when the media file is successfully deleted.
 * @return A [ManagedActivityResultLauncher] to be used for deleting media files.
 */
@Composable
private fun deleteMediaIntentSenderLauncher(
    onMediaFileDeleted: () -> Unit
): ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Logger.i("File delete request granted and file deleted successfully")
            onMediaFileDeleted()
        } else {
            Logger.i("File delete request denied.")
        }
    }
}


@Composable
private fun OptionMenu(
    onTrackDetailsClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }

    Column {

        BouncyComposable(onClick = { isContextMenuVisible = true }) {
            ThreeDotsOptionMenu()
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false }) {
            DropdownMenuItem(
                text = {
                    Text(text = "Track Details")
                },
                onClick = {
                    onTrackDetailsClick()
                    isContextMenuVisible = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text(text = "Share")
                },
                onClick = {
                    onShareClick()
                    isContextMenuVisible = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text(text = "Delete")
                },
                onClick = {
                    onDeleteClick()
                    isContextMenuVisible = false
                }
            )
        }
    }
}

@Composable
private fun BackButton(
    onBack: () -> Unit
) {

    BouncyComposable(onClick = {
        onBack()
    }) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Arrow back",
            modifier = Modifier
                .size(32.dp)
        )
    }
}

/**
 * Uses muted color from media thumbnail create vertical gradient background color.
 */
@Composable
private fun gradientBackgroundColor(backgroundColor: Color): Brush {
    val color by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 500), label = "Music player background animation"
    )

    return Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to MaterialTheme.colorScheme.background,
            0.5f to color,
            1f to MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun PreviewImageTitleArtist(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    previewImage: Bitmap?,
    title: String,
    artist: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val previewImageSize by animateDpAsState(
            targetValue = if (isPlaying) 300.dp else 150.dp,
            animationSpec = tween(),
            label = "music player thumbnail preview animation"
        )

        val thumbnail = previewImage ?: Utils.getDefaultThumbnail(LocalContext.current)
        Image(
            bitmap = thumbnail.asImageBitmap(), contentDescription = "Preview Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(previewImageSize)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun ThreeDotsOptionMenu() {
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .size(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val size = 7.dp
        Icon(
            imageVector = Icons.Rounded.Circle,
            modifier = Modifier.size(size),
            contentDescription = "Circle"
        )
        Icon(
            imageVector = Icons.Rounded.Circle,
            modifier = Modifier.size(size),
            contentDescription = "Circle"
        )
        Icon(
            imageVector = Icons.Rounded.Circle,
            modifier = Modifier.size(size),
            contentDescription = "Circle"
        )
    }
}
