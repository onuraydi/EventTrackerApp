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
    fun getAll(): Flow<List<Tag>>

    @Query("select * from tags where id = :id")
    fun getById(id:String): Flow<Tag>

    @Insert
    suspend fun insert(tag: Tag):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tags:List<Tag>)

    @Query("SELECT * FROM tags WHERE categoryId = :categoryId")
    fun getTagsByCategory(categoryId: String): Flow<List<Tag>>

    @Query("SELECT * FROM tags")
    suspend fun getAllTagsOnce(): List<Tag>

    @Query("SELECT * FROM tags WHERE categoryId = :categoryId")
    suspend fun getTagForCategoryOnce(categoryId: String):List<Tag>
}