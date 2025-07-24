package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.CategoryRepository
import com.example.eventtrackerapp.data.repositories.EventRepository
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val categoryRepository: CategoryRepository
):ViewModel()
{
    //Bütün etkinlikler
    private val _allEventsWithRelations =  MutableStateFlow<List<EventWithTags>>(emptyList())
    val allEventsWithRelations:StateFlow<List<EventWithTags>> = _allEventsWithRelations

    //Tek bir etkinlik(1)
    private val _eventWithRelations = MutableStateFlow<EventWithTags?>(null)
    val eventWithRelations:StateFlow<EventWithTags?> = _eventWithRelations


    //Kullanıcının ana sayfasındaki etkinlikler(2)
    private val _eventsForUser = MutableStateFlow<List<EventWithTags?>>(emptyList())
    val eventsForUser:StateFlow<List<EventWithTags?>> = _eventsForUser


    //Kullanıcının eklediği etkinlikler(3)
    private val _eventsForOwner = MutableStateFlow<List<Event?>>(emptyList())
    val eventsForOwner:StateFlow<List<Event?>> = _eventsForOwner


    //Etkinliğe ait tag(4)
    private val _tagForEvent = MutableStateFlow<String?>(null)
    val tagForEvent:StateFlow<String?> = _tagForEvent

    fun getAllEventsWithRelations(){
        viewModelScope.launch {
            eventRepository.getAllEventsWithRelations().collect{
                _allEventsWithRelations.value = it
            }
        }
    }

    //(1)
    fun getEventWithRelationsById(eventId:String)
    {
        viewModelScope.launch {
            eventRepository.getEventWithRelationsById(eventId).collect{
                _eventWithRelations.value = it
            }
        }
    }

    val allcategoriesWithTags: StateFlow<List<CategoryWithTag>> =
        categoryRepository.getCategoriesWithTags().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addEvent(event:Event,selectedTags: List<Tag>)
    {
        viewModelScope.launch {
            eventRepository.insertEvent(event,selectedTags)
        }
    }

    //(2)
    fun getEventsForUser(tagIds:List<String>)
    {
        viewModelScope.launch {
            eventRepository.getEventsForUser(tagIds).collect{
                _eventsForUser.value = it
            }
        }
    }

    fun updateEvent(event: Event,selectedTags: List<Tag>)
    {
        viewModelScope.launch {
            eventRepository.updateEvent(event,selectedTags)
        }
    }

    fun deleteEvent(event: Event)
    {
        viewModelScope.launch {
            eventRepository.deleteEvent(event)
        }
    }

    //(3)
    fun getEventsByOwnerId(profileId:String){
        viewModelScope.launch {
            eventRepository.getEventsForOwner(profileId).collect{
                _eventsForOwner.value = it
            }
        }
    }


    // TODO bu metoda daha sonra bakılabilir liste döndürmesi sağlanabilir şuan tüm tagları tek bir string olarak dönüyor
    //(4)
    fun getTagNamesForEvent(eventId:String)
    {
        viewModelScope.launch {
            eventRepository.getEventWithRelationsById(eventId).map { eventWithTags ->
                eventWithTags?.tags?.joinToString { it.name }
            }.collect{
                _tagForEvent.value = it
            }
        }
    }

    // TODO etkinliğe ait kategorinin adını getirecek metot. Gerekirse tamamlanabilir

//    fun getCategoryNameForEvent(eventId: String):LiveData<String>
//    {
//        return eventRepository.getEventWithRelationsById(eventId).filter {eventWithTags ->
//            eventWithTags.event.categoryId ==
//        }
//    }
}