package com.randos.data.manager

import com.randos.domain.manager.PermissionManager

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionManagerImpl @Inject constructor(private val context: Context) : PermissionManager<ActivityResultLauncher<String>> {

    private lateinit var requestReadPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestWritePermissionLauncher: ActivityResultLauncher<String>

    override fun requestMediaReadPermission() {
        if (::requestReadPermissionLauncher.isInitialized) {
            requestReadPermissionLauncher.launch(getReadPermission())
        } else {
            throw IllegalStateException("Permission launcher not initialized")
        }
    }

    override fun requestMediaWritePermission() {
        if (::requestWritePermissionLauncher.isInitialized) {
            requestWritePermissionLauncher.launch(getWritePermission())
        } else {
            throw IllegalStateException("Permission launcher not initialized")
        }
    }

    override fun isMediaReadPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, getReadPermission()) == PackageManager.PERMISSION_GRANTED
    }

    override fun isMediaWritePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, getWritePermission()) == PackageManager.PERMISSION_GRANTED
    }

    override fun setReadPermissionLauncher(launcher: ActivityResultLauncher<String>) {
        this.requestReadPermissionLauncher = launcher
    }

    override fun setWritePermissionLauncher(launcher: ActivityResultLauncher<String>) {
        this.requestWritePermissionLauncher = launcher
    }

    private fun getReadPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun getWritePermission(): String {
        return android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}