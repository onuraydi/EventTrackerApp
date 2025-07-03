package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.EventTagCrossRef
import com.example.eventtrackerapp.model.EventWithTags
import com.example.eventtrackerapp.model.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Thread.State

class EventViewModel(application:Application): AndroidViewModel(application) {

    private val eventDao = EventTrackerDatabase.getDatabase(application, viewModelScope).eventDao()

    private val _eventList = MutableStateFlow<List<Event>>(arrayListOf())
    private val _event = MutableStateFlow<Event>(Event())

    private val _eventWithTag = MutableStateFlow<List<EventWithTags>>(arrayListOf())

    val eventList:StateFlow<List<Event>> = _eventList
    val event:StateFlow<Event> = _event

    val eventWithTag:StateFlow<List<EventWithTags>> = _eventWithTag

    private val _eventsByOwner = MutableStateFlow<List<Event>>(arrayListOf())
    val eventsByOwner:StateFlow<List<Event>> = _eventsByOwner

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


    fun getEventBySelectedTag(tagIds :List<Int>){
        viewModelScope.launch(Dispatchers.IO) {
            _eventWithTag.value = eventDao.getEventBySelectedTag(tagIds)
        }
    }


    fun getFilteredEvents(selectedTagIds: List<Tag>): Flow<List<EventWithTags>> {
        return eventDao.getAllEventsWithTags().map { allEvents ->
            allEvents.filter { event ->
                event.tags.any { tag -> tag in selectedTagIds }
            }
        }
    }

    fun insertEventWithTags(event: Event,tags: List<Tag>){
        viewModelScope.launch(Dispatchers.IO) {
            val eventId = eventDao.add(event).toInt()
            val refs = tags.map { tag ->
                EventTagCrossRef(eventId = eventId,tagId = tag.id)
            }
            eventDao.insertEventTags(refs)
        }
    }

    val allEventsWithTags: Flow<List<EventWithTags>> = eventDao.getAllEventsWithTags()

    fun getEventByOwner(ownerId:String){
        viewModelScope.launch {
            eventDao.getEventsByOwner(ownerId).collect{data->
                _eventsByOwner.value = data
            }
        }
    }
}