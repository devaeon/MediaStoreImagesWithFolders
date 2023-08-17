package com.devaeon.mediastoreimageswithfolders.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devaeon.mediastoreimageswithfolders.model.FolderListWithData
import com.devaeon.mediastoreimageswithfolders.model.ListItems
import com.devaeon.mediastoreimageswithfolders.repository.Repository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MediaViewModelLogs"

class MediaViewModel(private val repository: Repository) : ViewModel() {
    private var functionName = "Nill"

    private val _mediaImages = MutableStateFlow<List<ListItems>>(emptyList())
    val mediaImages: StateFlow<List<ListItems>> = _mediaImages

    private val _mediaFolders = MutableStateFlow<List<FolderListWithData>>(emptyList())
    val mediaFolders: StateFlow<List<FolderListWithData>> = _mediaFolders

    private var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "CoroutineExceptionHandler: $functionName", throwable)
    }

    fun loadLibraryContent() = viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
        fetchMediaImages()
        fetchMediaFoldersListWithData()
    }

    private suspend fun fetchMediaImages() {
        repository.getMediaImages().collect { imageUri ->
            _mediaImages.value = imageUri
        }
    }

    private suspend fun fetchMediaFoldersListWithData() {
        repository.getMediaFolders().collect {
            _mediaFolders.value = it
        }
    }


}