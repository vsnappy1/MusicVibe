package com.randos.music_player.presentation.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.core.data.model.MusicFile
import com.randos.core.utils.Utils
import com.randos.music_player.utils.toTime

@Composable
fun TrackDetailLayout(musicFile: MusicFile) {
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background,
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var previewImage by remember { mutableStateOf<Bitmap?>(null) }
        val context = LocalContext.current
        LaunchedEffect(musicFile) {
            previewImage = Utils.getAlbumImage(musicFile.path) ?: Utils.getDefaultThumbnail(
                context
            )
        }

        musicFile.apply {
            previewImage?.let {
                Image(
                    bitmap = it.asImageBitmap(), contentDescription = "Preview Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            DetailRow("Title", title)
            DetailRow("Artist", artist)
            DetailRow("Album", album)
            DetailRow("Track length", duration.toTime())
            DetailRow("Genre", genre ?: "Unknown")
            DetailRow("Size", size)
            DetailRow("Path", path)
        }
    }
}

@Composable
private fun DetailRow(header: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = header,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider()
    }
}


@Preview
@Composable
fun PreviewTrackDetailLayout() {
    TrackDetailLayout(
        MusicFile(
            id = 1,
            title = "Title",
            artist = "Artist",
            album = "Album",
            duration = 10000,
            path = "Unknown",
            dateAdded = System.currentTimeMillis(),
            genre = null,
            size = "3.40 MB"
        )
    )
}
