package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.eventtrackerapp.model.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles")
    suspend fun getAll(): List<Profile>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getById(id: String): Profile

    @Insert
    suspend fun add(profile: Profile)

    @Update
    suspend fun update(profile: Profile)

    @Delete
    suspend fun delete(profile: Profile)
}