package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.model.firebasemodels.FirebaseLike
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
class LikeRepository(
    private val firestore: FirebaseFirestore
) {
    private val likesCollection = firestore.collection("likes")

    suspend fun toggleLike(eventId: String, profileId: String) {
        val docId = "${eventId}_$profileId"
        val documentSnapshot = likesCollection.document(docId).get().await()

        if (documentSnapshot.exists()) {
            // Beğeni varsa sil
            try {
                likesCollection.document(docId).delete().await()
                Log.d(TAG, "Like silindi Firestore'dan")
            } catch (e: Exception) {
                Log.e(TAG, "Like silme hatası", e)
            }
        } else {
            // Yoksa ekle
            val firebaseLike = FirebaseLike(eventId, profileId)
            try {
                likesCollection.document(docId).set(firebaseLike).await()
                Log.d(TAG, "Like eklendi Firestore'a")
            } catch (e: Exception) {
                Log.e(TAG, "Like ekleme hatası", e)
            }
        }
    }

    fun getLikeCountForEvent(eventId: String): Flow<Int> = flow {
        try {
            val snapshot = likesCollection
                .whereEqualTo("eventId", eventId)
                .get().await()
            emit(snapshot.size())
        } catch (e: Exception) {
            Log.e(TAG, "Like sayısı alınamadı", e)
            emit(0)
        }
    }

    fun isEventLikedByUser(eventId: String, profileId: String): Flow<Boolean> = flow {
        try {
            val docId = "${eventId}_$profileId"
            val snapshot = likesCollection.document(docId).get().await()
            emit(snapshot.exists())
        } catch (e: Exception) {
            Log.e(TAG, "Like kontrolü başarısız", e)
            emit(false)
        }
    }

    companion object {
        private const val TAG = "LikeRepository"
    }
}
