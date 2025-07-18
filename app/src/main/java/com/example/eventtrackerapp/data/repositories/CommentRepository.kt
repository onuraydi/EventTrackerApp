package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.CommentMapper
import com.example.eventtrackerapp.data.source.local.CommentDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseComment
import com.example.eventtrackerapp.model.roommodels.Comment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommentRepository(
    private val commentDao: CommentDao,
    private val firestore: FirebaseFirestore
){
    private val commentsCollection = firestore.collection("comments")

    //Etkinliğe özel yorumları Room'dan al ve Firestore'dan dinle
    fun getCommentsForEvent(eventId:String):Flow<List<Comment>>{
        return commentDao.getCommentsForEvent(eventId)
            .onEach {
                listenForFirestoreCommentsForEvent(eventId)
            }
    }

    //Etkinliğe ait yorum sayısını getirme (Room'dan alınıyor)
    fun getCommentCountForEvent(eventId:String):Flow<Int>{
        return commentDao.getCommentCount(eventId)
    }

    //Firestore'dan belirli bir etkinliğe ait yorumları dinleme
    @OptIn(DelicateCoroutinesApi::class)
    private fun listenForFirestoreCommentsForEvent(eventId:String){
        commentsCollection.whereEqualTo("eventId",eventId)
            .addSnapshotListener{snapshot,e->
                if(e!=null){
                    Log.w(TAG,"Listen failed for comments of event $eventId")
                }

                if(snapshot!=null){
                    val firebaseComments = snapshot.documents.mapNotNull { it.toObject(FirebaseComment::class.java) }
                    GlobalScope.launch(Dispatchers.IO){
                        val currentRoomComments = commentDao.getCommentsForEvent(eventId).first()
                        val currentRoomCommentIds = currentRoomComments.map { it.id }.toSet()
                        val firebaseCommentIds = firebaseComments.map { it.id }.toSet()

                        //Firestore'dan gelenleri Room'a ekle/güncelle
                        firebaseComments.forEach { fbComment->
                            val roomComment = CommentMapper.toEntity(fbComment)
                            commentDao.insertComment(roomComment)
                        }

                    }
                }
            }
    }

    //Yorum Ekle
    suspend fun upsertComment(comment:Comment){
        //Room'a kaydet
        commentDao.insertComment(comment)

        //Sonra Firestore'a kaydet
        val firebaseComment = CommentMapper.toFirebaseModel(comment)
        commentsCollection.document(firebaseComment.id).set(firebaseComment)
            .addOnSuccessListener { Log.d(TAG,"Comment successfully upserted to Firestore") }
            .addOnFailureListener { e-> Log.w(TAG,"Error upserting comment to Firestore",e) }
    }

    //Bir etkinliğe ait tüm yorumları silme(Event silinirken çağrılabilir)
    suspend fun deleteCommentsForEvent(eventId:String){
        //Room'dan sil
        commentDao.deleteCommentsForEvent(eventId)

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