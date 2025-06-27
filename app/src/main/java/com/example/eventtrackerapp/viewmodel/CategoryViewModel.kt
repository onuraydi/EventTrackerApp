package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.CategoryWithTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application): AndroidViewModel(application){

    private val categoryDao = EventTrackerDatabase.getDatabase(application,viewModelScope).categoryDao()

    private val _categoryWithTags = MutableStateFlow<List<CategoryWithTag>>(arrayListOf())
    val categoryWithTags:StateFlow<List<CategoryWithTag>> = _categoryWithTags

    fun getAllCategoryWithTags(){
        viewModelScope.launch{
            _categoryWithTags.value = categoryDao.getCategoryWithTags()
        }
    }
}