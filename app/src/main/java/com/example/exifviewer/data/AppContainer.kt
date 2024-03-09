package com.example.exifviewer.data

import android.content.Context

interface AppContainer {
    val exifData: ExifData
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val exifData: ExifData by lazy {
        ExifDataImp(context)
    }
}