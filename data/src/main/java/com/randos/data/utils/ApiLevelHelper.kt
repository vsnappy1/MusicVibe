package com.randos.data.utils

import android.os.Build

internal object ApiLevelHelper {

    fun isApiLevel30OrAbove(): Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}