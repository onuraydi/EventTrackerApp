package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Query
import com.example.eventtrackerapp.model.Tag

@Dao
interface TagDao {

    @Query("select * from tags")
    suspend fun getAll():List<Tag>

    @Query("select * from tags where id = :id")
    suspend fun getById(id:Int):Tag
}