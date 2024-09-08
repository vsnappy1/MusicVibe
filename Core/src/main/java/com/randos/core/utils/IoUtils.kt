package com.randos.core.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.randos.core.data.model.MusicFile
import com.randos.core.presentation.theme.red
import com.randos.logger.Logger
import java.io.File

object IoUtils {

    /**
     * A Composable layout for confirming the deletion of a single music track.
     *
     * This layout presents a confirmation dialog with options to cancel or proceed with deleting the track.
     *
     * @param musicFile The [MusicFile] object representing the track to be deleted, containing its path and ID.
     * @param onCancelClick A lambda function that is called when the user clicks the "Cancel" button.
     * @param onDeleteClick A lambda function that is called when the user clicks the "Delete" button. Default is an empty lambda.
     * @param onMediaFileDeleted A lambda function that is called after the media file is successfully deleted. Default is an empty lambda.
     */

    @Composable
    fun DeleteTrackConfirmationLayout(
        musicFile: MusicFile,
        onCancelClick: () -> Unit,
        onDeleteClick: () -> Unit = {},
        onMediaFileDeleted: () -> Unit = {}
    ) {
        Card(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
        ) {
            val context = LocalContext.current
            val deleteMediaIntentSenderLauncher =
                deleteMediaIntentSenderLauncher(onMediaFileDeleted = onMediaFileDeleted)

            val mediaWritePermissionRequestLauncher =
                mediaWritePermissionRequestLauncher(
                    context = context,
                    musicFile = musicFile,
                    onMediaFileDeleted = onMediaFileDeleted
                )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Delete 1 track?",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cancel",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(0.5f)
                            .clip(CircleShape)
                            .clickable {
                                onCancelClick()
                                deleteMediaFile(
                                    context,
                                    musicFile,
                                    deleteMediaIntentSenderLauncher,
                                    mediaWritePermissionRequestLauncher
                                )
                            }
                    )
                    Text(
                        text = "Delete",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium.copy(color = red),
                        modifier = Modifier
                            .weight(0.5f)
                            .clip(CircleShape)
                            .clickable {
                                onDeleteClick()
                            }
                    )
                }
            }
        }
    }

    /**
     * Initiates the deletion of a media file from the device's storage.
     *
     * This function handles the deletion process differently based on the device's API level:
     * - For devices running API level 30 (Android R) or higher, it uses `MediaStore.createDeleteRequest`
     *   to request deletion of the media file and launches the request using the provided `intentSenderLauncher`.
     * - For devices running API levels between 26 and 29 inclusive, it requests write permission using the
     *   `writePermissionLauncher` and handles the deletion upon permission grant.
     *
     * @param context The context used to access the content resolver.
     * @param musicFile The media file to be deleted.
     * @param deleteMediaIntentSenderLauncher The launcher to handle intent sender requests for deleting the media file.
     * @param mediaWritePermissionRequestLauncher The launcher to request write permission for deleting the media file.
     */
    private fun deleteMediaFile(
        context: Context,
        musicFile: MusicFile,
        deleteMediaIntentSenderLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        mediaWritePermissionRequestLauncher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.getContentUri("external"),
                    musicFile.id
                )
                val intent =
                    MediaStore.createDeleteRequest(
                        context.contentResolver,
                        listOf(uri)
                    ).intentSender
                deleteMediaIntentSenderLauncher.launch(IntentSenderRequest.Builder(intent).build())
            } else {
                mediaWritePermissionRequestLauncher.launch(PermissionManager.getMediaWritePermissionString())
            }
        } catch (e: Exception) {
            Logger.w(context, "Could not launch media item delete request.", e)
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
    private fun ColumnScope.deleteMediaIntentSenderLauncher(
        onMediaFileDeleted: () -> Unit
    ): ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> {
        return rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Logger.i(this, "File delete request granted and file deleted successfully")
                onMediaFileDeleted()
            } else {
                Logger.i(this, "File delete request denied.")
            }
        }
    }

    /**
     * Creates and returns a [ManagedActivityResultLauncher] that requests write permission
     * to delete media files on devices with API levels between 26 and 29 inclusive.
     *
     * This launcher will request the WRITE_EXTERNAL_STORAGE permission. If the permission is granted,
     * it will attempt to delete the specified media file and its record from the MediaStore.
     *
     * @param context The context used to access the content resolver for deleting the MediaStore record.
     * @param musicFile The music file to be deleted.
     * @param onMediaFileDeleted The callback to be invoked when the media file is successfully deleted.
     * @return A [ManagedActivityResultLauncher] to be used for requesting write permission.
     */
    @Composable
    private fun ColumnScope.mediaWritePermissionRequestLauncher(
        context: Context,
        musicFile: MusicFile,
        onMediaFileDeleted: () -> Unit
    ): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                Logger.i(this, "Write external storage permission granted.")
                val file = File(musicFile.path)
                if (file.exists()) {
                    if (file.delete()) {
                        Logger.i(this, "File deleted at path ${file.path}")
                        deleteRecordFromMediaStore(context, musicFile.id.toString())
                        onMediaFileDeleted()
                    } else {
                        Logger.w(this, "Could not delete file at path ${file.path}")
                    }
                } else {
                    Logger.w(this, "File does not exist at path ${file.path}")
                }
            } else {
                Logger.w(this, "Write external storage request denied.")
            }
        }
    }

    /**
     * Deletes a media record from the MediaStore using the provided media ID.
     *
     * @param context The context used to access the content resolver.
     * @param mediaId The ID of the media file to be deleted from the MediaStore.
     */
    private fun deleteRecordFromMediaStore(
        context: Context,
        mediaId: String
    ) {
        val deleted = context.contentResolver.delete(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Audio.Media._ID + " = ?",
            arrayOf(mediaId)
        )
        if (deleted > 0) {
            Logger.i(context, "$deleted Media file is removed from MediaStore.")
        }
    }
}