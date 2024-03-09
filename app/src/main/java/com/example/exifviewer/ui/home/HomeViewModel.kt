package com.example.exifviewer.ui.home

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    val uiState = mutableStateOf(UiState())

    fun onImageChosen(uri: Uri) {
        uiState.value = uiState.value.copy(imgSource = uri)
    }

    data class UiState(
        val imgSource: Uri? = null,
    )
}