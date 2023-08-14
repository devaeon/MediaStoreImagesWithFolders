package com.devaeon.mediastoreimageswithfolders.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FolderListWithData(
    /** the full path on local storage containing the image items in this folder/bucket  on the device Mediastore */
    var folderPath: String = "",

    /** name of the folder on local storage containing the image items in this folder/bucket on the device Mediastore */
    var folderName: String = "",

    /** an ArrayList<ListItems> containing all image items located in this folder/bucket */
    var images: ArrayList<ListItems> = ArrayList(),

    /** the total number of images located in this folder/bucket */
    var imageFolderSize: Int = images.size,

    /** the bucket id of this folder/bucket on the device Mediastore */
    var bucketId: Int = 0,
) : Parcelable