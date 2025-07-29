package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.CategoryMapper
import com.example.eventtrackerapp.data.mappers.TagMapper
import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await


class CategoryRepository(
    private val firestore: FirebaseFirestore
) {
    private val categoryCollection = firestore.collection("categories")
    private val tagCollection = firestore.collection("tags")

    fun getCategoriesWithTags(): Flow<List<CategoryWithTag>> = flow {
        try {
            val categorySnapshot = categoryCollection.get().await()
            val firebaseCategories = categorySnapshot.documents.mapNotNull {
                it.toObject(FirebaseCategory::class.java)
            }

            val tagSnapshot = tagCollection.get().await()
            val firebaseTags = tagSnapshot.documents.mapNotNull {
                it.toObject(FirebaseTag::class.java)
            }

            val categoryWithTagsList = firebaseCategories.map { category ->
                val relatedTags = firebaseTags.filter { it.categoryId == category.id }
                CategoryWithTag(
                    category = CategoryMapper.toEntity(category),
                    tags = relatedTags.map { TagMapper.toEntity(it) }
                )
            }

            emit(categoryWithTagsList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching categories and tags from Firestore", e)
            emit(emptyList())
        }
    }

    fun getCategoryWithTagsByCategoryId(categoryId: String): Flow<CategoryWithTag?> = flow {
        try {
            val categoryDoc = categoryCollection.document(categoryId).get().await()
            val firebaseCategory = categoryDoc.toObject(FirebaseCategory::class.java)

            val tagSnapshot = tagCollection
                .whereEqualTo("categoryId", categoryId)
                .get().await()
            val relatedTags = tagSnapshot.documents.mapNotNull {
                it.toObject(FirebaseTag::class.java)
            }

            firebaseCategory?.let {
                val result = CategoryWithTag(
                    category = CategoryMapper.toEntity(it),
                    tags = relatedTags.map { TagMapper.toEntity(it) }
                )
                emit(result)
            } ?: emit(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching category by ID from Firestore", e)
            emit(null)
        }
    }

    suspend fun addCategory(id: String, name: String) {
        val firebaseCategory = FirebaseCategory(id = id, name = name)

        try {
            categoryCollection.document(id).set(firebaseCategory).await()
            Log.d(TAG, "Category '$name' added to Firebase")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding category '$name'", e)
        }
    }

    suspend fun addTag(id: String, name: String, categoryId: String) {
        val firebaseTag = FirebaseTag(id = id, name = name, categoryId = categoryId)

        try {
            tagCollection.document(id).set(firebaseTag).await()
            Log.d(TAG, "Tag '$name' added to category '$categoryId' in Firebase")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding tag '$name'", e)
        }
    }

    companion object {
        private const val TAG = "CategoryRepository"
    }
}


