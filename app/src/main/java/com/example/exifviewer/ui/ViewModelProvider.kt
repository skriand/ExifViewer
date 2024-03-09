package com.example.exifviewer.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.exifviewer.ExifViewerApplication
import com.example.exifviewer.ui.edit.EditViewModel
import com.example.exifviewer.ui.home.HomeViewModel
import com.example.exifviewer.ui.viewer.ViewerViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel()
        }
        initializer {
            ViewerViewModel(
                exifViewerApplication().container.exifData
            )
        }
        initializer {
            EditViewModel(
                exifViewerApplication().container.exifData
            )
        }
    }
}

fun CreationExtras.exifViewerApplication(): ExifViewerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ExifViewerApplication)