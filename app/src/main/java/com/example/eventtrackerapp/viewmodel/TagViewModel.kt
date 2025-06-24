package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TagViewModel(application: Application) : AndroidViewModel(application) {

    private val tagDao = EventTrackerDatabase(application).tagDao();

    private val _tagList = MutableStateFlow<List<Tag>>(arrayListOf())
    private val _tag = MutableStateFlow<Tag>(Tag())

    public val tagList:StateFlow<List<Tag>> = _tagList;
    public val tag:StateFlow<Tag> = _tag;

    fun getAllTags() {
        viewModelScope.launch(Dispatchers.IO) {
            _tagList.value = tagDao.getAll();
        }
    }

    fun getById(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            _tag.value = tagDao.getById(id);
        }
    }
}