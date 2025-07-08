package com.example.eventtrackerapp.data.repositories

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.eventtrackerapp.data.mappers.CommentMapper
import com.example.eventtrackerapp.data.source.local.CommentDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseComment
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val commentDao: CommentDao,
    private val firebaseDatabase: FirebaseDatabase
){

    private val commentRef = firebaseDatabase.getReference("comments")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        syncCommentFromFirebase()
    }

    fun getAllCommentFromLocal(): Flow<List<Comment>> {
        return commentDao.getAllComment()
    }


    fun getCommentByIdFromLocal(commentId:String):Flow<Comment>
    {
        return commentDao.getCommentById(commentId)
    }

    fun getCommentForEventFromLocal(eventId:String):Flow<List<Comment>>
    {
        return commentDao.getCommentsForEvent(eventId)
    }

    fun getCommentForEventWithDetailFromLocal(eventId: String):Flow<List<CommentWithProfileAndEvent>>{
        return commentDao.getCommentForEventWithDetail(eventId)
    }

    suspend fun getCommentByIdOnce(commentId: String):Comment {
        return commentDao.getCommentByIdOnce(commentId)
    }

    suspend fun getCommentsForEventOnce(eventId:String):List<Comment>
    {
        return commentDao.getCommentsForEventOnce(eventId)
    }

    suspend fun getCommentsForProfileOnce(profileId:String):List<Comment>
    {
        return commentDao.getCommentsForProfileOnce(profileId)
    }


    suspend fun saveComment(comment: Comment){
        val finalComment = if(comment.id.isEmpty()){
            val newRef = commentRef.push()
            comment.copy(id = newRef.key ?: throw IllegalStateException("Firebase Comment ID could not be generated"))
        }else{
            comment
        }
        commentDao.insertComment(finalComment)

        var firebaseComment = CommentMapper.toFirebaseModel(finalComment)
        commentRef.child(firebaseComment.id).setValue(firebaseComment).await()
    }



    private fun syncCommentFromFirebase(){
        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val firebaseComments = mutableListOf<FirebaseComment>()
                    for (childSnapShot in snapshot.children){
                        val firebaseComment = childSnapShot.getValue(FirebaseComment::class.java)?.apply {
                            id = childSnapShot.key ?: ""
                        }
                        firebaseComment?.let {
                            firebaseComments.add(it)
                        }
                    }
                    commentDao.insertAllComments(CommentMapper.toEntityList(firebaseComments))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO HATA YÖNETİMİ
            }
        })
    }
}