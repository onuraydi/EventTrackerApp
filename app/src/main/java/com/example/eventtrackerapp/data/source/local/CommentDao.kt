package com.example.eventtrackerapp.data.source.local

import android.security.identity.AccessControlProfileId
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments:List<Comment>)

    @Query("SELECT * FROM comments")
    fun getAllComment():Flow<List<Comment>>

    @Transaction
    @Query("SELECT * FROM comments WHERE eventId = :eventId")
    fun getCommentsForEvent(eventId:String):Flow<List<Comment>>

    @Transaction
    @Query("SELECT * FROM comments WHERE eventId = :eventId")
    fun getCommentForEventWithDetail(eventId: String):Flow<List<CommentWithProfileAndEvent>>

    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentByIdOnce(commentId: String):Comment

    @Query("SELECT * FROM comments WHERE eventId = :eventId")
    suspend fun getCommentsForEventOnce(eventId: String):List<Comment>

    @Query("SELECT * FROM comments WHERE profileId = :profileId")
    suspend fun getCommentsForProfileOnce(profileId: String):List<Comment>

    @Query("SELECT COUNT(*) FROM comments WHERE eventId = :eventId")
    fun getCommentCount(eventId: Int):Flow<Int>

    @Query("SELECT * FROM comments WHERE id = :commentId")
    fun getCommentById(commentId: String):Flow<Comment>
}