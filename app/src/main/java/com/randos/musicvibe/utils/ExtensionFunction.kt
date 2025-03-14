package com.randos.musicvibe.utils

import android.content.res.Resources
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

/**
 * Turns milliseconds into formatted time (i.e. turns 100 into "1:40")
 */
internal fun Long.toTime(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

internal fun Long.toDate(): String {
    val format = "yyyy-MM-dd HH:mm:ss"
    val locale = Locale.getDefault()
    val date = Date(this)
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(date)
}

fun Long.toReadableSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return String.format(
        Locale.getDefault(),
        "%.2f %s",
        this / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}

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