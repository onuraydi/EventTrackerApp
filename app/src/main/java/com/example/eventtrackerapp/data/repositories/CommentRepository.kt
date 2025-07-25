package com.example.eventtrackerapp.data.repositories

import android.content.Context
import android.util.Log
import com.example.eventtrackerapp.common.NetworkUtils
import com.example.eventtrackerapp.data.mappers.CommentMapper
import com.example.eventtrackerapp.data.source.local.CommentDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseComment
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommentRepository(
    private val commentDao: CommentDao,
    private val firestore: FirebaseFirestore,
    private val context:Context
){
    private val commentsCollection = firestore.collection("comments")

    //Etkinliğe özel yorumları Room'dan al ve Firestore'dan dinle
    fun getCommentsForEvent(eventId:String):Flow<List<CommentWithProfileAndEvent>> = flow {

        if (NetworkUtils.isNetworkAvailable(context))
        {
            try {
                val commentSnapshot = commentsCollection.get().await()
                val firebaseComments = commentSnapshot.documents.mapNotNull { it.toObject(FirebaseComment::class.java) }
                val roomComments = firebaseComments.map { CommentMapper.toEntity(it) }

                commentDao.insertAllComment(roomComments)
                emit(commentDao.getCommentsForEvent(eventId).first())
            }
            catch (e:Exception){
                Log.e(TAG,"Firestore'dan veri alınamadı",e)
                emit(commentDao.getCommentsForEvent(eventId).first())
            }
        }else
        {
            emit(commentDao.getCommentsForEvent(eventId).first())
        }
//        return commentDao.getCommentsForEvent(eventId)
////            .onEach {
////                listenForFirestoreCommentsForEvent(eventId)
////            }
    }

    //Etkinliğe ait yorum sayısını getirme (Room'dan alınıyor)
    fun getCommentCountForEvent(eventId:String):Flow<Int> = flow {
        if (NetworkUtils.isNetworkAvailable(context))
        {
            try {
                val commentSnapshot = commentsCollection.get().await()
//                val firebaseComments = commentSnapshot.documents.mapNotNull { it.toObject(FirebaseComment::class.java) }
//                val roomComments = firebaseComments.map { CommentMapper.toEntity(it) }


                emit(commentDao.getCommentCount(eventId).first())
            }
            catch (e:Exception){
                Log.e(TAG,"Firestore'dan veri alınamadı",e)
                emit(commentDao.getCommentCount(eventId).first())
            }
        }else
        {
            emit(commentDao.getCommentCount(eventId).first())
        }
//        return commentDao.getCommentCount(eventId)
    }

    //Firestore'dan belirli bir etkinliğe ait yorumları dinleme
    @OptIn(DelicateCoroutinesApi::class)
    private fun listenForFirestoreCommentsForEvent(eventId:String){
        commentsCollection.whereEqualTo("eventId",eventId)
            .addSnapshotListener{snapshot,e->
                if(e!=null){
                    Log.w(TAG,"Listen failed for comments of event $eventId")
                    return@addSnapshotListener
                }

                if(snapshot!=null){
                    val firebaseComments = snapshot.documents.mapNotNull { it.toObject(FirebaseComment::class.java) }
                    GlobalScope.launch(Dispatchers.IO){
                        val currentRoomComments = commentDao.getCommentsForEvent(eventId).first()
                        val currentRoomCommentIds = currentRoomComments.map { it.comment.id }.toSet()
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
    suspend fun upsertComment(comment: Comment) {
        val firebaseComment = CommentMapper.toFirebaseModel(comment)

        commentsCollection.add(firebaseComment)
            .addOnSuccessListener { documentRef ->
                Log.d(TAG, "Comment successfully added to Firestore with auto ID: ${documentRef.id}")

                // Yeni ID'yi veriye yaz
                val updatedFirebaseComment = firebaseComment.copy(id = documentRef.id)
                val roomComment = CommentMapper.toEntity(updatedFirebaseComment)

                // Artık Room'a da hatasız eklenir
                CoroutineScope(Dispatchers.IO).launch {
                    commentDao.insertComment(roomComment)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding comment to Firestore", e)
            }
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