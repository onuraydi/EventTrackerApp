package com.example.eventtrackerapp.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import com.example.eventtrackerapp.viewmodel.PermissionViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PermissionHelper {
    //galeriye gidecek fonksiyon
    fun goToGallery(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>){
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)//uriler kalıcı olarak saklandı
        }
        launcher.launch(intent)
    }

    fun saveEventImageToInternalStorage(context:Context, uri:Uri):String?{
        val contentResolver = context.contentResolver
        try{
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "my_image${System.currentTimeMillis()}.jpg"
            val outputFile = File(context.filesDir,fileName)

            FileOutputStream(outputFile).use { outputStream->
                inputStream?.copyTo(outputStream)
            }
            return outputFile.absolutePath //kaydedilen dosyanın mutlak yolu
        }catch (e:IOException){
            e.printStackTrace()
            return null
        }
    }

    fun requestPermission(
        context: Context,
        permission:String,
        viewModel:PermissionViewModel,
        permissionLauncher: ManagedActivityResultLauncher<String,Boolean>,
        imagePickerLauncher: ManagedActivityResultLauncher<Intent,ActivityResult>
    ){
        if(ContextCompat.checkSelfPermission(context,permission)!=PackageManager.PERMISSION_GRANTED){
            if(viewModel.shouldShowRationale(context)){
                Toast.makeText(context,"Fotoğraf yüklemek için galeri izni lazım",Toast.LENGTH_LONG).show()
                permissionLauncher.launch(permission)
            }else{
                permissionLauncher.launch(permission)
            }
        }else{
            //izin verildi
            goToGallery(imagePickerLauncher)
        }
    }
}