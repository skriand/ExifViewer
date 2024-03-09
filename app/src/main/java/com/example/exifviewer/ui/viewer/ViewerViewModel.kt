package com.example.exifviewer.ui.viewer

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exifviewer.data.ExifData
import kotlinx.coroutines.launch

class ViewerViewModel(
    private val exifData: ExifData
) : ViewModel() {
    val uiState = mutableStateOf(UiState())

    fun onImageChosen(uri: Uri) {
        uiState.value = uiState.value.copy(imgSource = uri)
        updateExifData()
    }

    private fun updateExifData() {
        viewModelScope.launch {
            uiState.value.imgSource?.let {
                val exifData = exifData.getExifData(it)
                uiState.value = uiState.value.copy(exifTags = exifData)
            }
        }
    }

    data class UiState(
        val imgSource: Uri? = null,
        val exifTags: Map<String, String> = mapOf()
    )
}