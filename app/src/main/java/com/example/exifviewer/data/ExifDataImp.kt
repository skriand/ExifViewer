package com.example.exifviewer.data

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExifDataImp(private val applicationContext: Context) : ExifData {
    override suspend fun getExifData(uri: Uri): Map<String, String> {
        return getExifData(uri, EXIF_TAGS)
    }

    override suspend fun getEditableTags(uri: Uri): Map<String, String> {
        return getExifData(
            uri, arrayOf(
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL
            )
        )
    }

    override suspend fun saveExifData(uri: Uri, tags: Map<String, String>) {
        val mediaUri = MediaStore.getMediaUri(applicationContext, uri)

        val tempInputOutput =
            applicationContext.contentResolver.openFileDescriptor(mediaUri!!, "rw", null)!!

        val exifInterface = ExifInterface(tempInputOutput.fileDescriptor)
        exifInterface.setAttribute(ExifData.TAG_MAKE, tags[ExifData.TAG_MAKE])
        exifInterface.setAttribute(ExifData.TAG_MODEL, tags[ExifData.TAG_MODEL])
        exifInterface.setAttribute(ExifData.TAG_DATETIME, tags[ExifData.TAG_DATETIME])
        tags[ExifData.TAG_LATITUDE]?.toDoubleOrNull()?.let { lat ->
            tags[ExifData.TAG_LONGITUDE]?.toDoubleOrNull()?.let { long ->
                exifInterface.setLatLong(lat, long)
            }
        }

        exifInterface.saveAttributes()

        withContext(Dispatchers.IO) {
            tempInputOutput.close()
        }
    }

    private suspend fun getExifData(uri: Uri, requiredTags: Array<String>): Map<String, String> {
        val input = applicationContext.contentResolver.openInputStream(uri)
        val exifInterface = ExifInterface(input!!)
        val exifData = mutableMapOf<String, String>()
        exifInterface.latLong?.let {
            exifData["Latitude"] = it[0].toString()
            exifData["Longitude"] = it[1].toString()
        }
        requiredTags.forEach { tag ->
            exifInterface.getAttribute(tag)?.let {
                exifData[tag] = it
            }
        }

        withContext(Dispatchers.IO) {
            input.close()
        }
        return exifData
    }

    companion object {
        private val EXIF_TAGS = arrayOf(
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_APERTURE_VALUE,
            ExifInterface.TAG_BRIGHTNESS_VALUE,
            ExifInterface.TAG_CAMERA_OWNER_NAME,
            ExifInterface.TAG_ARTIST,
            ExifInterface.TAG_BITS_PER_SAMPLE,
            ExifInterface.TAG_COLOR_SPACE,
            ExifInterface.TAG_CONTRAST,
            ExifInterface.TAG_DIGITAL_ZOOM_RATIO,
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_FLASH_ENERGY,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_IMAGE_UNIQUE_ID,
            ExifInterface.TAG_ISO_SPEED,
            ExifInterface.TAG_LENS_MAKE,
            ExifInterface.TAG_LENS_MODEL,
            ExifInterface.TAG_LENS_SPECIFICATION,
            ExifInterface.TAG_LIGHT_SOURCE,
            ExifInterface.TAG_MAKER_NOTE,
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_SATURATION,
            ExifInterface.TAG_SHARPNESS,
            ExifInterface.TAG_SHUTTER_SPEED_VALUE,
            ExifInterface.TAG_SOFTWARE,
        )
    }
}