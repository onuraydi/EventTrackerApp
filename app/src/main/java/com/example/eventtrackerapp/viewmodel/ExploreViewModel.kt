package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(application:Application):AndroidViewModel(application) {

    val exploreDao = EventTrackerDatabase.getDatabase(application,viewModelScope).exploreDao()

    val _searchList = MutableStateFlow<List<Event>>(arrayListOf())
    val searchList: StateFlow<List<Event>> = _searchList

    fun searchEvents(query:String){
        viewModelScope.launch {
            exploreDao.searchEvents(query).collect{data->
                _searchList.value = data
            }
        }
    }
}