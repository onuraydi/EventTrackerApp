package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.CategoryWithTag
import com.example.eventtrackerapp.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.State

class CategoryViewModel(application: Application): AndroidViewModel(application){

    private val categoryDao = EventTrackerDatabase.getDatabase(application,viewModelScope).categoryDao()


    private val _category = MutableStateFlow<Category>(Category());
    private val _categoryWithTags = MutableStateFlow<List<CategoryWithTag>>(arrayListOf())

    val category: MutableStateFlow<Category> = _category
    val categoryWithTags:StateFlow<List<CategoryWithTag>> = _categoryWithTags

    fun getAllCategoryWithTags(){
        viewModelScope.launch{
            _categoryWithTags.value = categoryDao.getCategoryWithTags()
        }
    }

    fun getCategoryById(categoryId:Int){
        viewModelScope.launch(Dispatchers.IO) {
            _category.value = categoryDao.getById(categoryId)
        };
    }
}