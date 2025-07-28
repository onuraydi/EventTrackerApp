package com.example.eventtrackerapp.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.StorageCacheRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageCacheRepository: StorageCacheRepository
):ViewModel() {

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath

    private val _cachedImagePath = MutableStateFlow<String?>(null)
    val cachedImagePath:StateFlow<String?> = _imagePath

    fun setImageToStorage(uri: Uri, eventId:String){
        viewModelScope.launch {
            _imagePath.value = storageCacheRepository.uploadImageToStorage(uri,eventId)
        }
    }

    fun getImagePathToCache(firebaseUrl:String){
        viewModelScope.launch {
            _cachedImagePath.value = storageCacheRepository.getImagePath(firebaseUrl)
        }
    }
}