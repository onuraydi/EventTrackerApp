package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.eventtrackerapp.model.EventWithProfile
import com.example.eventtrackerapp.model.ProfileEventCrossRef
import com.example.eventtrackerapp.model.ProfileWithEvents

@Dao
interface ProfileEventDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfileEventCrossRef(crossRef: ProfileEventCrossRef)

    @Transaction
    @Query("SELECT * FROM profiles WHERE id = :profileId")
    suspend fun getProfilesWithEvent(profileId:String): ProfileWithEvents

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventWithProfile(eventId: Int):EventWithProfile
}
