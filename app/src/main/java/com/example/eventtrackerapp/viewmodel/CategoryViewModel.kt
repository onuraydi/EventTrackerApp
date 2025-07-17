package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.eventtrackerapp.data.repositories.CategoryRepository
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel()
{
    // TODO ileride farklı işlemler de gelebilir

    val categoryWithTags: LiveData<List<CategoryWithTag>> = categoryRepository.getCategoriesWithTags().asLiveData()

    fun getCategoryWithTagsById(categoryId: String): LiveData<CategoryWithTag?> {
        return categoryRepository.getCategoryWithTagsByCategoryId(categoryId).asLiveData()
    }
}