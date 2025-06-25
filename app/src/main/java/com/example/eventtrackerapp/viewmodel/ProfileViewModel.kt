package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val profileDao = EventTrackerDatabase(application).profileDao()

    private val _profileList = MutableStateFlow<List<Profile>>(arrayListOf())
    private val _profile = MutableStateFlow<Profile>(Profile())

    val profileList: StateFlow<List<Profile>> = _profileList
    val profile:StateFlow<Profile> = _profile

    fun getAll(){
        viewModelScope.launch(Dispatchers.IO) {
            _profileList.value = profileDao.getAll()
        }
    }

    fun getById(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            _profile.value = profileDao.getById(id)
        }
    }

    fun insertProfile(profile:Profile){
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.add(profile)
        }
    }

    fun updateProfile(profile:Profile){
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.update(profile)
        }
    }

    fun deleteProfile(profile:Profile){
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.delete(profile)
        }
    }
}