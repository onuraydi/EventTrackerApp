package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.eventtrackerapp.model.EventWithParticipants
import com.example.eventtrackerapp.model.ProfileEventCrossRef
import com.example.eventtrackerapp.model.ProfileWithEvents
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParticipation(crossRef: ProfileEventCrossRef)

    @Delete
    suspend fun deleteParticipation(crossRef: ProfileEventCrossRef)

    @Transaction
    @Query("SELECT EXISTS(SELECT 1 FROM ProfileEventCrossRef WHERE profileId = :profileId AND eventId = :eventId)")
    fun getParticipationState(profileId: String, eventId: Int): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM profileeventcrossref WHERE eventId = :eventId")
    fun getParticipantsCount(eventId: Int):Flow<Int>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventWithParticipants(eventId:Int):Flow<EventWithParticipants?>

    @Transaction
    @Query("SELECT * FROM profiles WHERE id = :profileId")
    fun getProfileWithEvent(profileId:String):Flow<ProfileWithEvents?>
}