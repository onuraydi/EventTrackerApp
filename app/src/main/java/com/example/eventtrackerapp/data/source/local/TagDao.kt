package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventtrackerapp.model.roommodels.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("select * from tags")
    suspend fun getAll():List<Tag>

    @Query("select * from tags where id = :id")
    suspend fun getById(id:Int): Tag

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTags(tags:List<Tag>)

    @Query("SELECT * FROM tags WHERE categoryId = :categoryId")
    fun getTagsByCategory(categoryId: Int): Flow<List<Tag>>
}