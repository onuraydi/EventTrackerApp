package com.example.eventtrackerapp.common

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.collectAsState
import com.example.eventtrackerapp.data.repositories.EventRepository
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

object StorageHelper {

    //Storage'a upload eden fonkisyon. Bu bize downloadUrl dönecek ve bunu firestore'a kaydedeceğiz
    suspend fun uploadImageToStorage(
        uri: Uri,
        eventId:String,
        ):String?{

        val fileName = "images/$eventId/${UUID.randomUUID()}.jpg"
        val storageRef = Firebase.storage.reference.child(fileName)//görsele ait referans oluşturur

        try {
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            return downloadUrl

        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    //Görseli Storage'dan indirip yerel diske kaydet
    suspend fun downloadImageFromFirebase(
        context: Context,
        imageUrl:String
    ):String?{

        val storageref = Firebase.storage.getReferenceFromUrl(imageUrl)//görsele ait referansı alır. Bu sayede görseli getirir
        val fileName = "downloaded_$${UUID.randomUUID()}.jpg"
        val localFile = File(context.filesDir,fileName)

        try {
            storageref.getFile(localFile).await()
            return localFile.absolutePath
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

}