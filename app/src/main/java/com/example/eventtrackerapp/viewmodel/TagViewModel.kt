package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.CategoryWithTag
import com.example.eventtrackerapp.model.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TagViewModel(application: Application) : AndroidViewModel(application) {

    private val tagDao = EventTrackerDatabase.getDatabase(application,viewModelScope).tagDao();

    private val _selectedTags = MutableStateFlow<List<Tag>>(arrayListOf())
    private val _chosenTags = MutableStateFlow<List<Tag>>(arrayListOf())

    val selectedTag:StateFlow<List<Tag>> = _selectedTags
    val chosenTags:StateFlow<List<Tag>> = _chosenTags

    fun updateSelectedCategoryTags(categoryWithTag: CategoryWithTag){
        _selectedTags.value = categoryWithTag.tags
        _chosenTags.value = emptyList() //kategori değişince seçili tag'leri sıfırlar
    }

    fun toggleTag(tag:Tag){
        val current = _chosenTags.value.toMutableList()
        if (current.any{ it.id == tag.id}){
            current.removeAll {it.id==tag.id}
        }else{
            current.add(tag)
        }
        _chosenTags.value = current
    }

    fun removeChosenTag(tag:Tag){
        _chosenTags.value = _chosenTags.value.filter{it.id!=tag.id}
    }

    fun resetTag(){
        _chosenTags.value = emptyList()
        _selectedTags.value = emptyList()
    }
}