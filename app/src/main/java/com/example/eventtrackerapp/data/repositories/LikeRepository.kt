package com.example.eventtrackerapp.data.repositories

import android.content.Context
import android.util.Log
import com.example.eventtrackerapp.common.NetworkUtils
import com.example.eventtrackerapp.data.mappers.LikeMapper
import com.example.eventtrackerapp.data.source.local.LikeDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseLike
import com.example.eventtrackerapp.model.roommodels.Like
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LikeRepository(
    private val likeDao: LikeDao,
    private val firestore:FirebaseFirestore,
    private val context:Context
) {
    private val likesCollection = firestore.collection("likes")

//    init
//    {
//        listenForFireStoreLikes()
//    }


    suspend fun toggleLike(eventId:String, profileId:String)
    {
        val isCurrentlyLiked = likeDao.isEventLikedByUser(eventId,profileId).first()

        if (isCurrentlyLiked)
        {
            val like = Like(eventId,profileId)
            likeDao.deleteLike(like)
            likesCollection.document("${eventId}_${profileId}").delete()
                .addOnSuccessListener { Log.d(TAG,"Like successfully deleted to Firestore.") }
                .addOnFailureListener { e -> Log.w(TAG,"Error deleting like to Firestore", e)}
        }
        else
        {
            val like = Like(eventId,profileId)
            val roomLike = LikeMapper.toFirebaseModel(like)
            likeDao.insertLike(like)
            likesCollection.document("${eventId}_${profileId}")
                .set(roomLike)
                .addOnSuccessListener { Log.d(TAG,"Like successfully added to Firestore.") }
                .addOnFailureListener { e -> Log.w(TAG,"Error adding like to Firestore", e)}

        }
    }

    fun getLikeCountForEvent(eventId: String):Flow<Int> = flow {
        if (NetworkUtils.isNetworkAvailable(context)){
            try {
                val likeSnapshot = likesCollection.get().await()
                val firebaseLike = likeSnapshot.documents.mapNotNull { it.toObject(FirebaseLike::class.java) }
                val roomlike = firebaseLike.map { LikeMapper.toEntity(it) }

                likeDao.getLikeCountForEvent(eventId)
                emit(likeDao.getLikeCountForEvent(eventId).first())
            }
            catch (e:Exception){
                Log.e(TAG,"Etkinlik firestore'dan çekilemedi, roomdan geliyor",e)
                emit(likeDao.getLikeCountForEvent(eventId).first())
            }
        }
        else{
            emit(likeDao.getLikeCountForEvent(eventId).first())
        }
//        return likeDao.getLikeCountForEvent(eventId)
    }

    fun isEventLikedByUser(eventId:String,profileId: String):Flow<Boolean> = flow {
        if (NetworkUtils.isNetworkAvailable(context)){
            try {
                val likeSnapshot = likesCollection.get().await()
                val firebaseLike = likeSnapshot.documents.mapNotNull { it.toObject(FirebaseLike::class.java) }
                val roomlike = firebaseLike.map { LikeMapper.toEntity(it) }

                likeDao.isEventLikedByUser(eventId,profileId)
                emit(likeDao.isEventLikedByUser(eventId,profileId).first())
            }
            catch (e:Exception){
                Log.e(TAG,"Etkinlik firestore'dan çekilemedi, roomdan geliyor",e)
                emit( likeDao.isEventLikedByUser(eventId,profileId).first())
            }
        }
        else{
            emit( likeDao.isEventLikedByUser(eventId,profileId).first())
        }
//        return likeDao.isEventLikedByUser(eventId,profileId)
    }

    suspend fun deleteLikesForEvent(eventId: String)
    {
        likeDao.deleteLikeForEvent(eventId)

        // ??
        val batch = firestore.batch()
        val likesSnapshot = likesCollection.whereEqualTo("eventId",eventId).get().await()
        likesSnapshot.documents.forEach { doc ->
            batch.delete(doc.reference)
        }

        batch.commit()
            .addOnSuccessListener { Log.d(TAG, "All likes for event $eventId successfully deleted from Firestore.") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting likes for event $eventId from Firestore", e) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun listenForFireStoreLikes()
    {
        likesCollection.addSnapshotListener { snapshot, e ->
            if (e != null)
            {
                Log.w(TAG,"like listen failed",e) // BURASI DAHA SONRA KALDIRILABİLİR
                return@addSnapshotListener
            }
            if (snapshot != null)
            {
                val firebaseLikes = snapshot.documents.mapNotNull { it.toObject(FirebaseLike::class.java) }
                GlobalScope.launch(Dispatchers.IO) {
                    // BURADAKİ MAP KULLANIMI ??
                    val currentRoomLikes = likeDao.getAllLikes().first().map { "${it.eventId}_${it.profileId}" }.toSet()
                    val firebaseLikeKeys = firebaseLikes.map { "${it.eventId}_${it.profileId}" }.toSet()

                    firebaseLikes.forEach {firebaseLike ->
                        val roomLike = LikeMapper.toEntity(firebaseLike)
                        likeDao.insertLike(roomLike)
                    }

                    // Roomda olup firebase'de olmayanları sil - firestore'da silienleri güncelle
                    val deletedLikeKeys = currentRoomLikes.minus(firebaseLikeKeys)
                    deletedLikeKeys.forEach { key ->
                        val parts = key.split("_")
                        if (parts.size == 2) {
                            likeDao.deleteLike(Like(eventId = parts[0], profileId = parts[1]))
                        }
                    }
                }
            }
        }
    }

    // ?
    companion object {
        private const val TAG = "LikeRepository"
    }
}