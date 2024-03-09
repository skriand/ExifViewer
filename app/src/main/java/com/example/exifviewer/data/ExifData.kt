package com.example.exifviewer.data

import android.net.Uri
import androidx.exifinterface.media.ExifInterface

interface ExifData {
    suspend fun getExifData(uri: Uri): Map<String, String>

    suspend fun getEditableTags(uri: Uri): Map<String, String>

    companion object {
        const val TAG_DATETIME = ExifInterface.TAG_DATETIME
        const val TAG_MAKE = ExifInterface.TAG_MAKE
        const val TAG_MODEL = ExifInterface.TAG_MODEL
        const val TAG_LATITUDE = "Latitude"
        const val TAG_LONGITUDE = "Longitude"
    }

    suspend fun saveExifData(uri: Uri, tags: Map<String, String>)
}