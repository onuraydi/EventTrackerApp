package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.model.firebasemodels.FirebaseLike
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
class LikeRepository(
    private val firestore: FirebaseFirestore
) {
    private val likesCollection = firestore.collection("likes")

    suspend fun toggleLike(eventId: String, profileId: String) {
        // Boş ID kontrolü
        if (eventId.isBlank() || profileId.isBlank()) {
            Log.w(TAG, "toggleLike: Boş eventId veya profileId")
            return
        }
        
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

    fun getLikeCountForEvent(eventId: String): Flow<Int> = callbackFlow {
        var listener: ListenerRegistration? = null
        
        // Boş event ID kontrolü
        if (eventId.isBlank()) {
            trySend(0)
        } else {
            listener = likesCollection
                .whereEqualTo("eventId",eventId)
                .addSnapshotListener { snapShot, error ->
                    if(error != null)
                    {
                        Log.e(TAG,"Like sayısı dinleme hatası",error)
                        close(error)
                        return@addSnapshotListener
                    }

                    val count = snapShot?.size() ?: 0
                    trySend(count).onFailure {
                        Log.e(TAG,"Like sayısı gönderilemedi",it)
                    }
                }
        }

        awaitClose {
            listener?.remove()
        }
    }

    fun isEventLikedByUser(eventId: String, profileId: String): Flow<Boolean> = callbackFlow {
        var listener: ListenerRegistration? = null
        
        // Boş ID kontrolü
        if (eventId.isBlank() || profileId.isBlank()) {
            trySend(false)
        } else {
            val docId = "${eventId}_$profileId"
            listener = likesCollection.document(docId)
                .addSnapshotListener {snapshot, error ->
                    if (error != null)
                    {
                        Log.e(TAG, "Like kontrolü dinleme hatası", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    val liked = snapshot?.exists() ?: false
                    trySend(liked).onFailure {
                        Log.e(TAG,"Like bilgisi gönderilmedi")
                    }
                }
        }

        awaitClose{
            listener?.remove()
        }
    }

    companion object {
        private const val TAG = "LikeRepository"
    }
}
