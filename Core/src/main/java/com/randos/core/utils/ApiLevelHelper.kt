package com.randos.core.utils

import android.os.Build

object ApiLevelHelper {

    fun isApiLevel30OrAbove(): Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}