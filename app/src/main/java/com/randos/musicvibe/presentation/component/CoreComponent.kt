package com.randos.musicvibe.presentation.component

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.musicvibe.R
import com.randos.musicvibe.data.AudioFile
import com.randos.musicvibe.utils.IoUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//--------------------------------------------------------------------------------------------------

@Composable
fun MusicItem(
    audioFile: AudioFile,
    defaultMusicThumbnail: Bitmap
) {
    var bitmap by remember { mutableStateOf(defaultMusicThumbnail) }
    LaunchedEffect(key1 = Unit) {
        bitmap = IoUtils.getAlbumImage(audioFile.path) ?: defaultMusicThumbnail
    }

    Row(modifier = Modifier.fillMaxWidth()) {
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
                text = audioFile.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audioFile.artist,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
fun PreviewMusicItem() {
    MusicItem(
        audioFile = AudioFile(1, "Title", "Artist", "Album", 100, 100, "Rock", ""),
        defaultMusicThumbnail = IoUtils.generateBitmapFromDrawable(
            LocalContext.current,
            R.drawable.default_music_thumbnail
        )
    )
}

//--------------------------------------------------------------------------------------------------

@Composable
fun AlphabetSlider(
    modifier: Modifier = Modifier,
    onSelectionChange: (Char) -> Unit
) {
    val alphabets = ('A'..'Z').toList()
    var heightOfSingleItem by remember { mutableIntStateOf(0) }
    var selectedAlphabet by remember { mutableStateOf("A") }
    var isLargeAlphabetVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val positionOfLargeAlphabet = (selectedAlphabet[0].code - 65) * 18.4

    Row(modifier = modifier) {
        AnimatedVisibility(
            isLargeAlphabetVisible,
            enter = EnterTransition.None,
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp, top = positionOfLargeAlphabet.dp)
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = selectedAlphabet,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Column(
            modifier = Modifier
                .onGloballyPositioned { coordinate ->
                    heightOfSingleItem = coordinate.size.height / alphabets.size
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { isLargeAlphabetVisible = true },
                        onDragEnd = { isLargeAlphabetVisible = false },
                        onDragCancel = { isLargeAlphabetVisible = false },
                        onVerticalDrag = { change, _ ->
                            val itemIndex = ((change.position.y) / heightOfSingleItem).toInt()
                            val alphabet =
                                if (itemIndex < 0) 'A' else if (itemIndex > 25) 'Z' else (65 + itemIndex).toChar()
                            if (selectedAlphabet != alphabet.toString()) {
                                selectedAlphabet = alphabet.toString()
                                onSelectionChange(alphabet)
                            }
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alphabets.forEach { alphabet ->
                Text(
                    text = "$alphabet",
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures { _ ->
                            selectedAlphabet = alphabet.toString()
                            onSelectionChange(alphabet)
                            isLargeAlphabetVisible = true
                            coroutineScope.launch {
                                delay(250)
                                isLargeAlphabetVisible = false
                            }
                        }
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlphabetSlider() {
    AlphabetSlider(onSelectionChange = {})
}

//--------------------------------------------------------------------------------------------------