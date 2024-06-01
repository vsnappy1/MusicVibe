package com.randos.core.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.randos.core.utils.pxToDp
import kotlinx.coroutines.delay

/**
 * A wrapper for composable to create a bouncy effect when clicked.
 *
 * @param modifier to modify the container.
 * @param onClick lambda triggered when item is clicked.
 * @param clickEnabled enable/disable click event.
 * @param content composable which needs to bounce on click.
 */
@Composable
fun BouncyComposable(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    clickEnabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {

    var isClicked by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 0.75f else 1.0f,
        label = "PlaybackController"
    )

    /*
    Finds the dimensions for the given composable.
     */
    if (size == IntSize.Zero) {
        Box(
            modifier = Modifier
                .onSizeChanged { size = it },
        ) {
            content()
        }
    } else {
        /*
        Next create a Box with dimensions we found earlier. This is important because to create
        bouncy effect. We are going to scale in and out the content within and we need fix size
        container so the other parts of the UI don't change based on it during animation.
         */
        Box(
            modifier = modifier
                .scale(scale)
                .size(
                    size.width
                        .pxToDp()
                        .plus(12.dp),
                    size.height
                        .pxToDp()
                        .plus(12.dp)
                )
                .clip(CircleShape)
                .clickable(clickEnabled) {
                    onClick()
                    isClicked = true
                },
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

    /*
    Once isClicked value changes we want to bring it back to false as this enables the animation to
    take place.
     */
    LaunchedEffect(key1 = isClicked) {
        delay(100)
        isClicked = false
    }
}