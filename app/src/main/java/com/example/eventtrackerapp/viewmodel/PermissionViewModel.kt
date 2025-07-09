package com.example.eventtrackerapp.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PermissionViewModel():ViewModel() {

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri:StateFlow<Uri?> = _profileImageUri

    private val _eventImageUri = MutableStateFlow<Uri?>(null)
    val eventImageUri:StateFlow<Uri?> = _eventImageUri

    fun setImageUri(uri:Uri?){
        _profileImageUri.value = uri
    }

    fun setEventImageUri(uri:Uri?){
        _eventImageUri.value = uri
    }

    fun getPermissionName():String{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Manifest.permission.READ_MEDIA_IMAGES
        }else{
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    //izin sebebi gösterme fonksiyonu
    fun shouldShowRationale(context:Context):Boolean{
        val activity = context as Activity
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,getPermissionName())
    }
}