package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.Profile
import com.example.eventtrackerapp.model.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val profileDao = EventTrackerDatabase.getDatabase(application,viewModelScope).profileDao()

    private val _profileList = MutableStateFlow<List<Profile>>(arrayListOf())
    private val _profile = MutableStateFlow<Profile>(Profile())

    val profileList: StateFlow<List<Profile>> = _profileList
    val profile:StateFlow<Profile> = _profile

    fun getAll(){
        viewModelScope.launch(Dispatchers.IO) {
            _profileList.value = profileDao.getAll()
        }
    }

    fun getById(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            _profile.value = profileDao.getById(id)
        }
    }

    fun insertProfile(profile:Profile){
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.add(profile)
        }
    }

    fun addTag(tag: Tag){
        if (!_profile.value.selectedTagList.orEmpty().contains(tag)) {
            val updatedList = _profile.value.selectedTagList.orEmpty() + tag
            val updatedProfile = _profile.value.copy(selectedTagList = updatedList)
            _profile.value = updatedProfile
            updateProfile(updatedProfile)
        }
    }

    fun removeTag(tagId:Int){
        //id'ye göre silme
        val updatedList = _profile.value.selectedTagList.orEmpty()
            .filterNot { it.id == tagId }

        val updatedProfile = _profile.value.copy(selectedTagList = updatedList)
        _profile.value = updatedProfile
        updateProfile(updatedProfile)
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