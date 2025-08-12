package com.rxdindia.templevehicletracker.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object ImageUriFactory {
    data class Created(val uri: Uri, val file: File)

    fun createImageUri(context: Context): Created {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val dir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(dir, "IMG_$ts.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Created(uri, file)
    }
}
