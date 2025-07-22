package com.example.eventtrackerapp.data.repositories

import com.example.eventtrackerapp.data.mappers.CategoryMapper
import com.example.eventtrackerapp.data.mappers.TagMapper
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await


class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao,
    private val firestore: FirebaseFirestore
) {
    private val categoryCollection = firestore.collection("categories")
    private val tagCollection = firestore.collection("tags")

    suspend fun initializeCategoriesAndTags()
    {
        val categorySnapshot = categoryCollection.get().await()
        val firebaseCategories = categorySnapshot.documents.mapNotNull { it.toObject(FirebaseCategory::class.java) }
        firebaseCategories.forEach { firebaseCategory ->
            // TODO BURADAN KAYNAKLI YANLIŞ VERİLER GELEBİLİR
            val roomCategory = CategoryMapper.toEntity(firebaseCategory)
            categoryDao.insert(roomCategory)
        }

        val tagSnapshot = tagCollection.get().await()
        val firebaseTags = tagSnapshot.documents.mapNotNull { it.toObject(FirebaseTag::class.java) }
        firebaseTags.forEach { firebaseTag ->
            val roomTags = TagMapper.toEntity(firebaseTag)
            tagDao.insert(roomTags)
        }
    }

    fun getCategoriesWithTags(): Flow<List<CategoryWithTag>>
    {
        return categoryDao.getCategoryWithTags()
    }

    fun getCategoryWithTagsByCategoryId(categoryId:String):Flow<CategoryWithTag?>
    {
        return categoryDao.getCategoryWithTagsById(categoryId)
    }
}

