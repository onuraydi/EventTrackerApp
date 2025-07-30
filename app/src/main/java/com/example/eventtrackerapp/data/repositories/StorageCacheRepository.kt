package com.example.eventtrackerapp.data.repositories

import android.content.Context
import android.net.Uri
import com.example.eventtrackerapp.data.source.local.CachedImageDao
import com.example.eventtrackerapp.model.roommodels.CachedImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class StorageCacheRepository(
    private val dao:CachedImageDao,
    private val context: Context
) {

    //Firebase & Storage'tan gelen url'e göre cache'te böyle bir dosya varsa onu getiren fonksiyon
    suspend fun getImagePath(firebaseUrl:String):String?{
        //1.Room'da varsa
        val cachedImage = dao.getByUrl(firebaseUrl)
        if(cachedImage!=null && File(cachedImage.localPath).exists()){
            return cachedImage.localPath
        }

        //2. Room'da yoksa -> Storage'tan indir, cihazda kaydet, room'a yaz
        val localPath = downloadAndSaveImage(firebaseUrl)
        if(localPath!=null){
            dao.insert(CachedImage(firebaseUrl,localPath))
        }

        return localPath?: firebaseUrl //indirme başarısızsa URL'yi döner
    }

    //Storage'a fotoğrafı yükleyen fonksiyon. Storage'a yükleyince download url döner
    suspend fun uploadImageToStorage(uri: Uri, eventId:String,filePath:String):String?{
        val fileName = "$filePath/$eventId/${UUID.randomUUID()}.jpg"
        val storageref = Firebase.storage.reference.child(fileName)

        try {
            storageref.putFile(uri).await()
            return storageref.downloadUrl.await().toString()
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    //Firebase & Storage'tan gelen url'i dosyaya çevirip kaydeden fonksiyon
    private suspend fun downloadAndSaveImage(imageUrl:String):String?{
        try {
            val storageRef = Firebase.storage.getReferenceFromUrl(imageUrl)
            val fileName = "cache_${UUID.randomUUID()}.jpg"
            val localFile = File(context.filesDir,fileName)

            storageRef.getFile(localFile).await()
            return localFile.absolutePath
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }
}