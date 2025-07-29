package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.CommentMapper
import com.example.eventtrackerapp.model.firebasemodels.FirebaseComment
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.views.MyAccountScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firestore.v1.StructuredAggregationQuery.Aggregation.Count
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommentRepository(
    private val firestore: FirebaseFirestore,
){
    private val commentsCollection = firestore.collection("comments")

    //Etkinliğe özel yorumları Firestore'dan dinle
    fun getCommentsForEvent(eventId: String): Flow<List<CommentWithProfileAndEvent>> = callbackFlow {
        val listenerRegistration = commentsCollection
            .whereEqualTo("eventId",eventId)
            .addSnapshotListener {snapshot, error ->
                if (error != null){
                    Log.e(TAG,"Yorum dinleme başarısız")
                    close(error)
                    return@addSnapshotListener
                }
                val documents = snapshot?.documents ?: emptyList()

                CoroutineScope(Dispatchers.IO).launch {
                    val result = documents.mapNotNull { doc ->
                        val firebaseComment = doc.toObject(FirebaseComment::class.java) ?: return@mapNotNull null
                        val comment = CommentMapper.toEntity(firebaseComment)

                        try
                        {
                            val profileDeferred = async {
                                firestore.collection("profiles")
                                    .document(firebaseComment.profileId)
                                    .get()
                                    .await()
                                    .toObject(Profile::class.java)
                            }

                            val eventDeferred = async {
                                firestore.collection("events")
                                    .document(firebaseComment.eventId)
                                    .get()
                                    .await()
                                    .toObject(Event::class.java)
                            }

                            val profile = profileDeferred.await()
                            val event = eventDeferred.await()

                            if (profile != null && event != null)
                            {
                                CommentWithProfileAndEvent(comment,profile,event)
                            }
                            else{
                                null
                            }
                        }catch (e: Exception) {
                            Log.e(TAG, "Yorum profil/event verisi çekilirken hata", e)
                            null
                        }
                    }
                    trySend(result).onFailure {
                        Log.e(TAG,"Yorum listesi gönderilemedi",it)
                    }
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    //Etkinliğe ait yorum sayısını getirme (Room'dan alınıyor)
    fun getCommentCountForEvent(eventId:String):Flow<Int> = callbackFlow {
        val listenerRegistration = commentsCollection
            .whereEqualTo("eventId",eventId)
            .addSnapshotListener{snapshot,error ->
                if (error != null)
                {
                    Log.e(TAG,"Yorum dinleme başarısız",error)
                    close(error)
                    return@addSnapshotListener
                }

                val count = snapshot?.size() ?: 0
                trySend(count).onFailure {
                    Log.e(TAG,"Yorum sayısı gönderilemedi",it)
                }
            }
        awaitClose{
            listenerRegistration.remove()
        }
    }

    //Firestore'a yorum ekleme
    suspend fun upsertComment(comment: Comment) {
        val firebaseComment = CommentMapper.toFirebaseModel(comment)

        try {
            val docRef = commentsCollection.add(firebaseComment).await()

            val updatedFirebaseComment = firebaseComment.copy(id = docRef.id)
            commentsCollection.document(docRef.id).set(updatedFirebaseComment)

            Log.d(TAG,"Comment eklendi")
        }catch (e:Exception){
            Log.e(TAG,"Comment eklenirken hata oluştu")
        }
    }

    companion object{
        private const val TAG = "CommentsRepository"
    }

}