package com.example.exifviewer

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.exifviewer.data.AppContainer
import com.example.exifviewer.data.AppDataContainer
import com.example.exifviewer.ui.navigation.ExifViewerNavHost

@Composable
fun ExifViewerApp(navController: NavHostController = rememberNavController()) {
    ExifViewerNavHost(navController = navController)
}

class ExifViewerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}