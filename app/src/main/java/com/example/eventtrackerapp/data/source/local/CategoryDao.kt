package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.eventtrackerapp.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    suspend fun getAll():List<Category>

    @Query("SELECT * FROM categories WHERE id = :id ")
    suspend fun getById(id:Int)
    
}