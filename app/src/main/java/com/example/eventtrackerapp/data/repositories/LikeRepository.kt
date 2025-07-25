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
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    private val likesCollection = firestore.collection("likes")

    suspend fun toggleLike(eventId: String, profileId: String) {
        val isCurrentlyLiked = likeDao.isEventLikedByUser(eventId, profileId).first()
        val like = Like(eventId, profileId)

        if (isCurrentlyLiked) {
            likeDao.deleteLike(like)
            try {
                likesCollection.document("${eventId}_$profileId").delete().await()
                Log.d("LikeRepo", "Like silindi")
            } catch (e: Exception) {
                Log.e("LikeRepo", "Firestore'dan like silinemedi", e)
            }
        } else {
            likeDao.insertLike(like)
            try {
                val firebaseLike = LikeMapper.toFirebaseModel(like)
                likesCollection.document("${eventId}_$profileId").set(firebaseLike).await()
                Log.d("LikeRepo", "Like eklendi")
            } catch (e: Exception) {
                Log.e("LikeRepo", "Firestore'a like eklenemedi", e)
            }
        }
    }

    fun getLikeCountForEvent(eventId: String): Flow<Int> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val snapshot = likesCollection.get().await()
                val firebaseLikes = snapshot.documents.mapNotNull { it.toObject(FirebaseLike::class.java) }
                val roomLikes = firebaseLikes.map { LikeMapper.toEntity(it) }

                likeDao.clearLikesForEvent(eventId) // varsa önce temizle
                likeDao.insertAllLikes(roomLikes) // sonra güncel veriyi ekle
            } catch (e: Exception) {
                Log.e("LikeRepo", "Firestore'dan like alınamadı", e)
            }
        }
        emit(likeDao.getLikeCountForEvent(eventId).first())
    }

    fun isEventLikedByUser(eventId: String, profileId: String): Flow<Boolean> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val snapshot = likesCollection.get().await()
                val firebaseLikes = snapshot.documents.mapNotNull { it.toObject(FirebaseLike::class.java) }
                val roomLikes = firebaseLikes.map { LikeMapper.toEntity(it) }

                likeDao.clearLikesForEvent(eventId)
                likeDao.insertAllLikes(roomLikes)
            } catch (e: Exception) {
                Log.e("LikeRepo", "Firestore'dan like alınamadı", e)
            }
        }
        emit(likeDao.isEventLikedByUser(eventId, profileId).first())
    }

    companion object {
        private const val TAG = "LikeRepository"
    }
}
