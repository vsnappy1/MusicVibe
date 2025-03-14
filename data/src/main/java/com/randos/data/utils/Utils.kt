package com.randos.data.utils

import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

internal object Utils {

    /**
     * Function to convert a file size in bytes to a human-readable format.
     *
     * This function takes a size in bytes represented as a `Long` and converts it into a
     * more readable string format using appropriate units (B, KB, MB, GB, TB). The conversion
     * is done by determining the appropriate unit based on the size and formatting the result
     * to two decimal places.
     *
     * @return A `String` representing the size in a human-readable format with two decimal places.
     *
     * @throws IllegalArgumentException if the size is negative.
     */
    fun toReadableSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return String.format(
            Locale.getDefault(),
            "%.2f %s",
            bytes / 1024.0.pow(digitGroups.toDouble()),
            units[digitGroups]
        )
    }
}