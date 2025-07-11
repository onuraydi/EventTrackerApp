package com.example.eventtrackerapp.data.source.local

import android.security.identity.AccessControlProfileId
import androidx.room.Dao
import androidx.room.Delete
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

    @Transaction
    @Query("SELECT * FROM comments WHERE eventId = :eventId")
    fun getCommentsForEvent(eventId:String):Flow<List<Comment>>

    @Query("SELECT COUNT(*) FROM comments WHERE eventId = :eventId")
    fun getCommentCount(eventId: Int):Flow<Int>

    @Query("DELETE FROM comments WHERE eventId = :eventId")
    suspend fun deleteCommentsForEvent(eventId: String)
}