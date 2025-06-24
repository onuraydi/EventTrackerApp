package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.eventtrackerapp.model.Event

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    suspend fun getAll():List<Event>


    @Query("Select * FROM events WHERE id = :id")
    suspend fun getById(id:Int):Event

    @Insert
    suspend fun add(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}