package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.CommentMapper
import com.example.eventtrackerapp.model.firebasemodels.FirebaseComment
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.Profile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class CommentRepository(
    private val firestore: FirebaseFirestore,
){
    private val commentsCollection = firestore.collection("comments")

    //Etkinliğe özel yorumları Firestore'dan dinle
    fun getCommentsForEvent(eventId: String): Flow<List<CommentWithProfileAndEvent>> = flow {
        try {
            val commentSnapshot = commentsCollection.whereEqualTo("eventId",eventId).get().await()

            val firebaseComments = commentSnapshot.documents.mapNotNull {
                it.toObject(FirebaseComment::class.java)
            }

            val commentWithProfileAndEventList = firebaseComments.mapNotNull { firebaseComment ->
                val comment = CommentMapper.toEntity(firebaseComment)

                // Firestore'dan profile ve event belgelerini al
                val profileSnapshot = firestore.collection("profiles")
                    .document(firebaseComment.profileId)
                    .get()
                    .await()

                val eventSnapshot = firestore.collection("events")
                    .document(firebaseComment.eventId)
                    .get()
                    .await()

                val profile = profileSnapshot.toObject(Profile::class.java)
                val event = eventSnapshot.toObject(Event::class.java)

                if (profile != null && event != null) {
                    CommentWithProfileAndEvent(
                        comment = comment,
                        profile = profile,
                        event = event
                    )
                } else {
                    null // eksik veri varsa dahil etme
                }
            }

            emit(commentWithProfileAndEventList)

        } catch (e: Exception) {
            Log.e(TAG, "Firestore'dan yorum verisi alınamadı", e)
            emit(emptyList())
        }
    }

    //Etkinliğe ait yorum sayısını getirme (Room'dan alınıyor)
    fun getCommentCountForEvent(eventId:String):Flow<Int> = flow {
        try {
            val commentSnapshot = commentsCollection.whereEqualTo("eventId",eventId).get().await()
            emit(commentSnapshot.size())
        }catch (e:Exception){
            Log.e(TAG,"Firestore'dan yorum sayısı alınamadı")
            emit(0)
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



    //Bir etkinliğe ait tüm yorumları silme(Event silinirken çağrılabilir)
    suspend fun deleteCommentsForEvent(eventId:String){
        //Firestore'dan sil (Firestore'da genelde Cloud Functions tercih ediliyormuş)
        commentsCollection.whereEqualTo("eventId",eventId).get().await().documents.forEach { doc->
            doc.reference.delete()
        }
        Log.d(TAG,"All comments for event $eventId deleted from Firestore.")
    }

    companion object{
        private const val TAG = "CommentsRepository"
    }

}