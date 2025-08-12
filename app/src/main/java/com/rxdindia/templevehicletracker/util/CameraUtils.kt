package com.rxdindia.templevehicletracker.util

import android.content.Intent
import android.net.Uri

object CameraUtils {
    fun cameraIntent(output: Uri): Intent =
        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(android.provider.MediaStore.EXTRA_OUTPUT, output)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
}
