package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.CategoryRepository
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import com.example.eventtrackerapp.model.roommodels.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.State
import javax.inject.Inject


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel()
{
    init {
        categoryRepository.listenForFirestoreCategories()
        categoryRepository.listenForFirestoreTags()
    }

    val categoryWithTags: LiveData<List<CategoryWithTag>> = categoryRepository.getCategoriesWithTags().asLiveData()
    private val _selectedTags = MutableStateFlow<List<Tag>>(arrayListOf())
    private val _chosenTags = MutableStateFlow<List<Tag>>(arrayListOf())

    val selectedTag: StateFlow<List<Tag>> = _selectedTags
    val chosenTags: StateFlow<List<Tag>> = _chosenTags

    init {
        categoryRepository.listenForFirestoreCategories()
        categoryRepository.listenForFirestoreTags()
    }

    fun getCategoryWithTagsById(categoryId: String): LiveData<CategoryWithTag?> {
        return categoryRepository.getCategoryWithTagsByCategoryId(categoryId).asLiveData()
    }

    fun addCategory(category: Category){
        viewModelScope.launch {
            categoryRepository.addCategory(category.id,category.name)
        }
    }

    fun addTag(tag:Tag){
        viewModelScope.launch {
            categoryRepository.addTag(tag.id,tag.name,tag.categoryId)
        }
    }

    fun resetTag(){
        _chosenTags.value = emptyList()
        _selectedTags.value = emptyList()
    }

    fun updateSelectedCategoryTags(categoryWithTag: CategoryWithTag){
        _selectedTags.value = categoryWithTag.tags
        _chosenTags.value = emptyList() //kategori değişince seçili tag'leri sıfırlar
    }

    fun toggleTag(tag: Tag){
        val current = _chosenTags.value.toMutableList()
        if (current.any{ it.id == tag.id}){
            current.removeAll {it.id==tag.id}
        }else{
            current.add(tag)
        }
        _chosenTags.value = current
    }

    fun removeChosenTag(tag: Tag){
        _chosenTags.value = _chosenTags.value.filter{it.id!=tag.id}
    }

    fun resetChosenTagForCategory(categoryId: String)
    {
        _chosenTags.value = _chosenTags.value.filterNot { it.categoryId == categoryId }
    }
}