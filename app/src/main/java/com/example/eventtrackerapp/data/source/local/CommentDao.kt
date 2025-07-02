package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.eventtrackerapp.model.Comment
import com.example.eventtrackerapp.model.CommentWithProfileAndEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment(comment:Comment)

    @Transaction
    @Query("SELECT * FROM comments WHERE eventId = :eventId")
    fun getCommentsForEvent(eventId:Int):Flow<List<CommentWithProfileAndEvent>>
}