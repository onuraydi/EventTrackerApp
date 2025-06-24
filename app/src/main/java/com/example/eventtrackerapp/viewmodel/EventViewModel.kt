package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(application:Application): AndroidViewModel(application) {

    private val eventDao = EventTrackerDatabase(application).eventDao()

    private val _eventList = MutableStateFlow<List<Event>>(arrayListOf())
    private val _event = MutableStateFlow<Event>(Event())

    val eventList:StateFlow<List<Event>> = _eventList
    val event:StateFlow<Event> = _event

    fun getAllEvents(){
        viewModelScope.launch(Dispatchers.IO) {
            _eventList.value = eventDao.getAll()
        }
    }

    fun getEventById(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            _event.value = eventDao.getById(id)
        }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            eventDao.add(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            eventDao.update(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            eventDao.delete(event)
        }
    }
}