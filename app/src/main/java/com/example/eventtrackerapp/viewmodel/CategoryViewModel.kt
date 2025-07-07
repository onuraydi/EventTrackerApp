package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application): AndroidViewModel(application){

    private val categoryDao = EventTrackerDatabase.getDatabase(application,viewModelScope).categoryDao()

    private val _categoryWithTags = MutableStateFlow<List<CategoryWithTag>>(arrayListOf())
    val categoryWithTags:StateFlow<List<CategoryWithTag>> = _categoryWithTags

    private val _category = MutableStateFlow<Category>(Category());
    val category: MutableStateFlow<Category> = _category

    fun getAllCategoryWithTags(){
        viewModelScope.launch{
            categoryDao.getCategoryWithTags()
                .collect{data->
                    _categoryWithTags.value = data
                }
        }
    }

    fun getCategoryById(categoryId:Int){
        viewModelScope.launch(Dispatchers.IO) {
            _category.value = categoryDao.getById(categoryId)
        };
    }
}