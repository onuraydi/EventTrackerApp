package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.ProfileEventCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ParticipantsViewModel(application: Application):AndroidViewModel(application) {

    private var participationDao = EventTrackerDatabase.getDatabase(application,viewModelScope).participationDao()


    fun deleteParticipation(eventId: String, profileId: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            participationDao.deleteParticipation(ProfileEventCrossRef(profileId,eventId))
        }
    }

    fun getParticipationState(eventId: String,profileId: String):Flow<Boolean>
    {
        return participationDao.getParticipationState(profileId,eventId)
    }

    fun getParticipantsCount(eventId: String):Flow<Int>{
        return participationDao.getParticipantsCount(eventId)
    }

    fun joinEvent(eventId:String,profileId:String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            participationDao.insertParticipation(ProfileEventCrossRef(profileId,eventId))
        }
    }

    fun getParticipants(eventId: String):Flow<List<Profile>>
    {
        return participationDao.getEventWithParticipants(eventId).map { it.pariticipants }
    }
}