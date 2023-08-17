package com.devaeon.mediastoreimageswithfolders.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.devaeon.mediastoreimageswithfolders.extensions.queryCursor
import com.devaeon.mediastoreimageswithfolders.model.FolderListWithData
import com.devaeon.mediastoreimageswithfolders.model.ListItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.Date
import java.util.concurrent.TimeUnit

interface Repository {
    suspend fun getMediaImages(): Flow<ArrayList<ListItems>>
    suspend fun getMediaFolders(): Flow<List<FolderListWithData>>
}

class RepositoryImpl(private val context: Context) : Repository {
    private val imageUri: Uri = MediaStore.Images.Media.getContentUri("external")

    private val projection get() = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE
        )

    private val orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC"

    override suspend fun getMediaImages(): Flow<ArrayList<ListItems>> = flow {
        val listItems = ArrayList<ListItems>()
        context.queryCursor(
            uri = imageUri,
            projection = projection,
            selection = null,
            selectionArgs = null,
            sortOrder = orderBy,
            showErrors = true
        ) {
            listItems.addAll(addImagesList(it))
        }.apply {
            emit(listItems)
        }

    }

    override suspend fun getMediaFolders(): Flow<List<FolderListWithData>> = flow {
        val imageFolders: ArrayList<FolderListWithData> = ArrayList()
        val folderIds: ArrayList<Int> = ArrayList()
        context.queryCursor(
            imageUri,
            projection,
            null,
            null,
            orderBy,
            true
        ) {
            val bucketId: Int = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))
            when {
                !folderIds.contains(bucketId) -> {
                    folderIds.add(bucketId)
                    val folderName: String = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val dataPath: String = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    var folderPath = dataPath.substring(0, dataPath.lastIndexOf("$folderName/"))
                    folderPath = "$folderPath$folderName/"
                    val images = getFolderImages(bucketId)
                    imageFolders.addAll(arrayListOf(FolderListWithData(folderPath, folderName, images, images.size, bucketId)))
                }
            }
        }.apply {
            emit(imageFolders)
        }
    }

    private fun getFolderImages(bucketId: Int): java.util.ArrayList<ListItems> {
        val list = ArrayList<ListItems>()
        context.queryCursor(
            imageUri,
            projection,
            MediaStore.Images.Media.BUCKET_ID + " like ? ",
            arrayOf("%$bucketId%"),
            orderBy,
            true,
        ) { list.addAll(addImagesList(it)) }
        return list
    }

    private fun addImagesList(cursor: Cursor): ArrayList<ListItems> {
        cursor.let {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val dateAddedCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val dateModifiedCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val sizeCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            val imageId = it.getInt(idCol)
            val imageUri = Uri.withAppendedPath(imageUri, imageId.toString()).toString()
            val filePath = it.getString(dataCol)
            val name = it.getString(displayNameCol)
            val size = it.getLong(sizeCol)

            val dateCreated = Date(TimeUnit.SECONDS.toMillis(it.getLong(dateAddedCol)))
            val dateModified = Date(TimeUnit.SECONDS.toMillis(it.getLong(dateModifiedCol)))
            return arrayListOf(ListItems(File(filePath), name, size, imageUri, imageId, filePath, dateCreated, dateModified))
        }
    }
}