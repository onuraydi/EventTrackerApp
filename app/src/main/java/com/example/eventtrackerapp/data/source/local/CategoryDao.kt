package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.CategoryWithTag

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    suspend fun getAll():List<Category>

    @Query("SELECT * FROM categories WHERE id = :id ")
    suspend fun getById(id:Int):Category

    @Transaction
    @Query("SELECT * FROM categories")
    suspend fun getCategoryWithTags():List<CategoryWithTag>

    @Insert
    suspend fun insert(category: Category):Long
}