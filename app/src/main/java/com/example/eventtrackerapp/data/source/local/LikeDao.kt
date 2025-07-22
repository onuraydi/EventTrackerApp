package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Index
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventtrackerapp.model.roommodels.Like
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLike(like: Like)

    @Query("SELECT * FROM likes")
    fun getAllLikes():Flow<List<Like>>

    @Delete
    suspend fun deleteLike(like:Like)

    @Query("DELETE FROM likes WHERE eventId = :eventId")
    suspend fun deleteLikeForEvent(eventId: String)

    @Query("DELETE FROM likes WHERE profileId = :profileId")
    suspend fun deleteLikeForProfile(profileId: String)

    // LİMİT 1?
    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE eventId = :eventId AND profileId = :profileId LIMIT 1)")
    fun isEventLikedByUser(eventId: String,profileId: String):Flow<Boolean>

    @Query("SELECT COUNT(profileId) FROM likes WHERE eventId = :eventId")
    fun getLikeCountForEvent(eventId: String):Flow<Int>

}