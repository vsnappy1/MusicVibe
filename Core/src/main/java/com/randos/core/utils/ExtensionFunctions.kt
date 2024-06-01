package com.randos.core.utils

import android.content.res.Resources
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Since this app uses edge to edge screen its a good idea to have a default padding.
 */
fun Modifier.defaultPadding(
    start: Dp = 16.dp,
    end: Dp = 16.dp,
    top: Dp = 32.dp,
    bottom: Dp = 48.dp
): Modifier {
    return this.padding(start = start, end = end, top = top, bottom = bottom)
}


/**
 * Converts pixel to dp.
 */
fun Int.pxToDp(): Dp {
    return (this / Resources.getSystem().displayMetrics.density).dp
}