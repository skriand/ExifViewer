package com.example.exifviewer.ui.edit

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exifviewer.R
import com.example.exifviewer.ui.ViewModelProvider
import com.example.exifviewer.ui.navigation.NavigationDestination
import com.example.exifviewer.utils.FieldValidator
import java.util.Locale

object EditDestination : NavigationDestination {
    override val route = "edit"
    override val titleResourceId: Int = R.string.edit_tags
    val uriArg = "uri"
    val fullRoute = "$route/{$uriArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navigateBack: () -> Unit,
    uriArg: String,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
    viewModel.onImageChosen(Uri.parse(uriArg))
    BackHandler(enabled = true, onBack = { navigateBack() })
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(EditDestination.titleResourceId)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveTagsToFile(Uri.parse(uriArg))
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.save)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        EditorBody(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorBody(
    viewModel: EditViewModel,
    modifier: Modifier
) {
    val uiState by viewModel.uiState

    Column(
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            TagTextField(
                value = uiState.latitude,
                label = stringResource(R.string.latitude),
                onValueChanged = { viewModel.onInputValueChanged(latitude = it) },
                validator = FieldValidator::validateLatitude,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f, true)
            )
            TagTextField(
                value = uiState.longitude,
                label = stringResource(R.string.longitude),
                onValueChanged = { viewModel.onInputValueChanged(longitude = it) },
                validator = FieldValidator::validateLongitude,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f, true)
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(Modifier.fillMaxWidth()) {
            TagTextField(
                value = uiState.deviceManufacturer,
                label = stringResource(R.string.device_brand),
                onValueChanged = { viewModel.onInputValueChanged(deviceManufacturer = it) },
                validator = FieldValidator::validateManufacturer,
                modifier = Modifier.weight(1f, true)
            )
            TagTextField(
                value = uiState.deviceModel,
                label = stringResource(R.string.device_model),
                onValueChanged = { viewModel.onInputValueChanged(deviceModel = it) },
                validator = FieldValidator::validateDeviceModel,
                modifier = Modifier.weight(1f, true)
            )
        }

        Spacer(Modifier.height(4.dp))

        TagDatePicker(
            value = uiState.creationDate,
            onValueChanged = { viewModel.onInputValueChanged(creationDate = it) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagTextField(
    value: String,
    label: String,
    onValueChanged: (String) -> Unit,
    validator: (String) -> Boolean,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        shape = RoundedCornerShape(32.0.dp),
        value = value,
        onValueChange = onValueChanged,
        label = { Text(label) },
        singleLine = true,
        isError = !(validator(value) || value.isEmpty()),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.padding(4.dp)
    )
}

private enum class PickerStatus {
    Inactive, DateSelection, TimeSelection,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDatePicker(
    value: String,
    onValueChanged: (String) -> Unit,
) {
    var pickerStatus by remember { mutableStateOf(PickerStatus.Inactive) }
    val dateTimeFormat = remember { SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH) }
    val calendar = Calendar.getInstance().apply {
        if (value.isNotBlank()) {
            time = dateTimeFormat.parse(value)
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
    )

    OutlinedTextField(
        shape = RoundedCornerShape(32.0.dp),
        value = value,
        onValueChange = {},
        label = { Text(stringResource(R.string.date_time)) },
        singleLine = true,
        readOnly = true,
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.high),
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .focusable(false)
            .clickable {
                pickerStatus = PickerStatus.DateSelection
            }
    )

    if (pickerStatus != PickerStatus.Inactive) {
        Dialog(
            onDismissRequest = { pickerStatus = PickerStatus.Inactive },
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (pickerStatus == PickerStatus.DateSelection) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            .fillMaxWidth()
                    )
                } else {
                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            .fillMaxWidth()
                    )
                }


                if (pickerStatus == PickerStatus.DateSelection) {
                    TextButton(
                        onClick = {
                            pickerStatus = PickerStatus.TimeSelection
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    ) {
                        Text(stringResource(R.string.next))
                    }
                } else {
                    TextButton(
                        onClick = {
                            pickerStatus = PickerStatus.Inactive
                            datePickerState.selectedDateMillis?.let {
                                onValueChanged(dateTimeFormat.format(Calendar.getInstance().apply {
                                    timeInMillis = it
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                }.time))
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }

}