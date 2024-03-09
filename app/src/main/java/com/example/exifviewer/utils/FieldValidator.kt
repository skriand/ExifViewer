package com.example.exifviewer.utils

import kotlin.math.absoluteValue

object FieldValidator {
    fun validateLatitude(input: String) = Regex("""-?\d+(\.\d+)*""").matches(input)
            && input.toDouble().absoluteValue <= 90.0

    fun validateLongitude(input: String) = Regex("""-?\d+(\.\d+)*""").matches(input)
            && input.toDouble().absoluteValue <= 180.0

    fun validateManufacturer(input: String) = Regex("""[\wА-Яа-я\- ]+""").matches(input)

    fun validateDeviceModel(input: String) = Regex("""[\wА-Яа-я\-_+*() ]+""").matches(input)
}