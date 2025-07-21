package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.EventRepository
import com.example.eventtrackerapp.model.roommodels.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    fun getParticipationCount(eventId: String):LiveData<Int>
    {
        return eventRepository.getParticipationCountForEvent(eventId).asLiveData()
    }

    fun hasUserParticipated(eventId: String, profileId:String):LiveData<Boolean>
    {
        return eventRepository.hasUserParticipated(eventId,profileId).asLiveData()
    }

    fun toggleAttendance(eventId: String,profileId: String)
    {
        viewModelScope.launch {
            eventRepository.toggleAttendance(eventId,profileId)
        }
    }

    fun getParticipantsForEvent(eventId: String):LiveData<List<Profile>>
    {
        return eventRepository.getEventWithParticipants(eventId).map { it?.pariticipants ?: emptyList() }.asLiveData()
    }
}