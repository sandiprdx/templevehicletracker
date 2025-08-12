package com.rxdindia.templevehicletracker.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

data class CreatedImage(val uri: Uri, val file: File)

object ImageUriFactory {
    fun createImageUri(ctx: Context): CreatedImage {
        val name = "tvt_${System.currentTimeMillis()}.jpg"
        val cvs = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TempleVehicleTracker")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cvs)
            ?: throw IllegalStateException("Failed to create image URI")
        return CreatedImage(uri, File("/sdcard/Pictures/TempleVehicleTracker/$name"))
    }
}
