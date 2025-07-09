package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventtrackerapp.model.roommodels.Like
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLike(like: Like)


    @Query("DELETE FROM likes WHERE profileId = :profileId AND eventId = :eventId")
    suspend fun deleteLike(profileId:String,eventId:Int)

    @Query("SELECT COUNT(*) FROM likes WHERE eventId = :eventId")
    fun getLikeCount(eventId: Int):Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE profileId = :profileId AND eventId = :eventId)")
    fun hasUserLiked(eventId: Int, profileId: String): Flow<Boolean>

}