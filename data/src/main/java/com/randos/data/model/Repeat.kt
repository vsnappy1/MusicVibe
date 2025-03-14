package com.randos.data.model

import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import com.randos.domain.type.RepeatMode

internal fun RepeatMode.toMediaControllerRepeat(): Int {
    return when (this) {
        RepeatMode.OFF -> REPEAT_MODE_OFF
        RepeatMode.ONE -> REPEAT_MODE_ONE
        RepeatMode.ALL -> REPEAT_MODE_ALL
    }
}

internal fun Int.toRepeatMode(): RepeatMode{
    return when (this) {
        REPEAT_MODE_OFF -> RepeatMode.OFF
        REPEAT_MODE_ONE -> RepeatMode.ONE
        REPEAT_MODE_ALL -> RepeatMode.ALL
        else -> RepeatMode.ALL
    }
}
