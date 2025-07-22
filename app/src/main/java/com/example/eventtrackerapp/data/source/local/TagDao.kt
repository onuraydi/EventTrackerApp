package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.eventtrackerapp.model.roommodels.Tag

@Dao
interface TagDao {

    @Query("select * from tags")
    suspend fun getAll():List<Tag>

    @Query("select * from tags where id = :id")
    suspend fun getById(id:Int): Tag

    @Insert
    suspend fun insert(tag: Tag):Long

    @Query("SELECT * FROM tags WHERE categoryId = :categoryId")
    suspend fun getTagsByCategory(categoryId: Int): List<Tag>
}