package com.randos.musicvibe.presentation.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.domain.model.MusicFile
import com.randos.musicvibe.utils.Utils

@Composable
fun MusicItem(
    modifier: Modifier = Modifier,
    musicFile: MusicFile,
    defaultMusicThumbnail: Bitmap,
    onClick: () -> Unit
) {
    var bitmap by remember { mutableStateOf(defaultMusicThumbnail) }
    LaunchedEffect(key1 = Unit) {
        bitmap = Utils.getAlbumImage(musicFile.path) ?: defaultMusicThumbnail
    }

    val rowModifier = remember {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp))
            .clickable { onClick() }
    }

    Row(modifier = rowModifier) {
        Image(
            bitmap = bitmap.asImageBitmap(), contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.height(75.dp)) {
            Text(
                text = musicFile.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = musicFile.artist,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMusicItem() {
    MusicItem(
        musicFile = MusicFile("1", "Title", "Artist", "Album", 100, "", 100, "Rock", "3.45Mb"),
        defaultMusicThumbnail = Utils.getDefaultThumbnail(LocalContext.current),
        onClick = {}
    )
}
