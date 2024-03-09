package com.example.exifviewer.ui.edit

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exifviewer.data.ExifData
import kotlinx.coroutines.launch

class EditViewModel(
    private val exifData: ExifData
) : ViewModel() {
    val uiState = mutableStateOf(UiState())

    fun onImageChosen(uri: Uri) {
        viewModelScope.launch {
            uiState.value = exifData.getEditableTags(uri).toUiState()
        }
    }

    fun onInputValueChanged(
        creationDate: String? = null,
        latitude: String? = null,
        longitude: String? = null,
        deviceManufacturer: String? = null,
        deviceModel: String? = null
    ) {
        creationDate?.let {
            uiState.value = uiState.value.copy(creationDate = creationDate)
        }
        latitude?.let {
            uiState.value = uiState.value.copy(latitude = latitude)
        }
        longitude?.let {
            uiState.value = uiState.value.copy(longitude = longitude)
        }
        deviceManufacturer?.let {
            uiState.value = uiState.value.copy(deviceManufacturer = deviceManufacturer)
        }
        deviceModel?.let {
            uiState.value = uiState.value.copy(deviceModel = deviceModel)
        }
    }

    fun saveTagsToFile(uri: Uri) {
        viewModelScope.launch {
            exifData.saveExifData(uri, uiState.value.toMap())
        }
    }

    data class UiState(
        val creationDate: String = "",
        val latitude: String = "",
        val longitude: String = "",
        val deviceManufacturer: String = "",
        val deviceModel: String = "",
        val imgSource: Uri? = null
    )
}

fun Map<String, String>.toUiState(): EditViewModel.UiState {
    return EditViewModel.UiState(
        this[ExifData.TAG_DATETIME] ?: "",
        this[ExifData.TAG_LATITUDE] ?: "",
        this[ExifData.TAG_LONGITUDE] ?: "",
        this[ExifData.TAG_MAKE] ?: "",
        this[ExifData.TAG_MODEL] ?: ""
    )
}

fun EditViewModel.UiState.toMap(): Map<String, String> {
    return mapOf(
        ExifData.TAG_DATETIME to creationDate,
        ExifData.TAG_MAKE to deviceManufacturer,
        ExifData.TAG_MODEL to deviceModel,
        ExifData.TAG_LATITUDE to latitude,
        ExifData.TAG_LONGITUDE to longitude
    )
}