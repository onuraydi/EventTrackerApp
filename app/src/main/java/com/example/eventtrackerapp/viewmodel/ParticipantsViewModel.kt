package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.EventRepository
import com.example.eventtrackerapp.model.roommodels.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    fun getParticipantsForEvent(eventId: String):Flow<List<Profile>>
    {
        return eventRepository.getEventWithParticipants(eventId).map { it?.pariticipants ?: emptyList() }
    }


    fun getParticipationCount(eventId: String):Flow<Int>
    {
        return eventRepository.getParticipationCountForEvent(eventId)
    }

    fun hasUserParticipated(eventId: String, profileId:String):Flow<Boolean>
    {
        return eventRepository.hasUserParticipated(eventId,profileId)
    }

    fun toggleAttendance(eventId: String,profileId: String)
    {
        viewModelScope.launch {
            eventRepository.toggleAttendance(eventId,profileId)
        }
    }
}