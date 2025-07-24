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

    private val _participationList = MutableStateFlow<List<Profile?>>(emptyList())
    val participationList:StateFlow<List<Profile?>> = _participationList

    private val _participationCount = MutableStateFlow<Int>(0)
    val participationCount: StateFlow<Int> = _participationCount

    private val _hasUserParticipated = MutableStateFlow<Boolean>(false)
    val hasUserParticipated: StateFlow<Boolean> = _hasUserParticipated


    fun getParticipantsForEvent(eventId: String)
    {
        viewModelScope.launch {
            eventRepository.getEventWithParticipants(eventId).map { it?.pariticipants ?: emptyList() }.collect{
                _participationList.value = it
            }
        }
    }


    fun getParticipationCount(eventId: String)
    {
        viewModelScope.launch {
            eventRepository.getParticipationCountForEvent(eventId).collect{
                _participationCount.value = it
            }
        }
    }

    fun hasUserParticipated(eventId: String, profileId:String)
    {
        viewModelScope.launch {
            eventRepository.hasUserParticipated(eventId,profileId).collect{
                _hasUserParticipated.value = it
            }
        }
    }

    fun toggleAttendance(eventId: String,profileId: String)
    {
        viewModelScope.launch {
            eventRepository.toggleAttendance(eventId,profileId)
        }
    }
}