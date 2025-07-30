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

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    fun setImageToStorage(uri: Uri, eventId:String){
        viewModelScope.launch {
            android.util.Log.d("StorageViewModel", "Fotoğraf yükleme başladı")
            _isUploading.value = true
            try {
                val result = storageCacheRepository.uploadImageToStorage(uri, eventId, "eventImage")
                android.util.Log.d("StorageViewModel", "Fotoğraf yükleme tamamlandı: $result")
                _imagePath.value = result
            } catch (e: Exception) {
                android.util.Log.e("StorageViewModel", "Fotoğraf yükleme hatası", e)
            } finally {
                _isUploading.value = false
                android.util.Log.d("StorageViewModel", "Fotoğraf yükleme durumu: ${_isUploading.value}")
            }
        }
    }

//    fun getImagePathFromCache(firebaseUrl:String){
//        viewModelScope.launch {
//            _cachedImagePath.value = storageCacheRepository.getImagePath(firebaseUrl)
//        }
//    }
}