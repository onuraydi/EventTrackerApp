package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application): AndroidViewModel(application){

    private val categoryDao = EventTrackerDatabase(application).categoryDao()

    private val _categories = MutableStateFlow<List<Category>>(arrayListOf())
    private val _category = MutableStateFlow<Category>(Category())

    val categories:StateFlow<List<Category>> = _categories
    val category:StateFlow<Category> = _category

    fun getAllCategories(){
        viewModelScope.launch{
            _categories.value = categoryDao.getAll()
        }
    }

    fun getCategoryById(id:Int){
        viewModelScope.launch{
            _category.value = categoryDao.getById(id)
        }
    }
}