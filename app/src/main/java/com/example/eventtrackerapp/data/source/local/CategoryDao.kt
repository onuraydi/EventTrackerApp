package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // TODO Kategori ve tag işlemleri ortak olarak kategori başlığı altında toplandığı için bu ve bazı dosyaların adı değişecek
    @Query("SELECT * FROM categories")
    fun getAll():Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getCategoryWithTags(): Flow<List<CategoryWithTag>>

    @Transaction
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryWithTagsById(categoryId:String):Flow<CategoryWithTag>

    @Insert
    suspend fun insert(category: Category):Long
}