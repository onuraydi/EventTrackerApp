package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.CategoryRepository
import com.example.eventtrackerapp.data.repositories.EventRepository
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val categoryRepository: CategoryRepository
):ViewModel()
{
    val allEventsWithRelations: LiveData<List<EventWithTags>> = eventRepository.getAllEventsWithRelations().asLiveData()

    fun getEventWithRelationsById(eventId:String):LiveData<EventWithTags?>
    {
        return eventRepository.getEventWithRelationsById(eventId).asLiveData()
    }

    val allcategoriesWithTags: LiveData<List<CategoryWithTag>> = categoryRepository.getCategoriesWithTags().asLiveData()

    fun addEvent(event:Event,selectedTags: List<Tag>)
    {
        viewModelScope.launch {
            eventRepository.insertEvent(event,selectedTags)
        }
    }

    fun getEventsForUser(tagIds:List<String>):LiveData<List<EventWithTags>>
    {
        return eventRepository.getEventsForUser(tagIds).asLiveData()
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

    // TODO etkinliğe ait kategorinin adını getirecek metot. Gerekirse tamamlanabilir

//    fun getCategoryNameForEvent(eventId: String):LiveData<String>
//    {
//        return eventRepository.getEventWithRelationsById(eventId).filter {eventWithTags ->
//            eventWithTags.event.categoryId ==
//        }
//    }

    // TODO bu metoda daha sonra bakılabilir liste döndürmesi sağlanabilir şuan tüm tagları tek bir string olarak dönüyor

    fun getTagNamesForEvent(eventId:String): LiveData<String?>
    {
        return eventRepository.getEventWithRelationsById(eventId).map { eventWithTags ->
            eventWithTags?.tags?.joinToString { it.name }
        }.asLiveData()
    }
}