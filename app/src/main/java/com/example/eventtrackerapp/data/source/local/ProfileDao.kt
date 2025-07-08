package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eventtrackerapp.model.roommodels.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles")
    fun getAll(): Flow<List<Profile>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getById(id: String): Flow<Profile>

    @Insert
    suspend fun add(profile: Profile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProfiles(profiles: List<Profile>)

    @Update
    suspend fun update(profile: Profile)

    @Delete
    suspend fun delete(profile: Profile)

    @Query("SELECT * FROM profiles")
    suspend fun getAllProfilesOnce():List<Profile>

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    suspend fun getProfileByIdOnce(profileId:String):Profile


}