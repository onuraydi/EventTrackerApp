package com.example.eventtrackerapp.data.repositories

import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryTagRelationshipRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategoriesWithTagsFromLocal():Flow<List<CategoryWithTag>> {
        return categoryDao.getCategoryWithTags()
    }

    fun getCategoryWithTagsByIdFromLocal(categoryId:String):Flow<CategoryWithTag?>{
        return categoryDao.getCategoryWithTagsById(categoryId)
    }
}