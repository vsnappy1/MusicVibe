package com.randos.music_player.utils


/**
 * Turns milliseconds into formatted time (i.e. turns 100 into "1:40")
 */
internal fun Long.toTime(): String {
    val milliseconds = this / 1000
    val minutes = milliseconds / 60
    val seconds = milliseconds % 60

    if (seconds == 0L) {
        return "$minutes:00"
    } else if (seconds < 10L) {
        return "$minutes:0$seconds"
    }
    return "$minutes:$seconds"
}