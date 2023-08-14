package com.devaeon.mediastoreimageswithfolders.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.Date

@Parcelize
data class ListItems(

    val file: File,
    /** the name of the image file as it is on device storage */
    var name: String = "",

    /** the file size in kb, Mo or GO of this image file */
    var size: Long = 0,

    /** the direct access path to this image file, can be used in an Imageview to display the image */
    var imageUri: String = "",

    /** image item id as stored in the Images MediaStore */
    var imageId: Int = 0,

    /** the full file path to this image item as it is on device storage, can no longer be used to access and display the image at runtime, use imageUri instead */
    var filePath: String = "",

    /** the date when image created */
    var dateCreated: Date = Date(),

    /** date representing the last time this image item was modified */
    var dateModified: Date = Date()
) : Parcelable