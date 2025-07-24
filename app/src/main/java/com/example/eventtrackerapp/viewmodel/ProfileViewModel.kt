package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.ProfileRepository
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository:ProfileRepository
): ViewModel() {


    //Neden live data? ve bunu init {} içerisinde alabilir miyiz?
//    val profileList : LiveData<List<Profile>> = profileRepository.getAllProfiles()
////        .onEach {
////        profileRepository.listenForAllFirestoreProfiles()
////    }
//        .asLiveData()

    private val _profile = MutableStateFlow<Profile?>(Profile())
    val profile:StateFlow<Profile?> = _profile

    fun getById(id:String){
        viewModelScope.launch {
            profileRepository.getProfile(id).collect{profile->
                _profile.value = profile
            }
        }
    }

    //aynı kayıtı eklemeyi engelleyen upsert işlemi. Aynı zamanda güncelleme işlemi de yapıyor.
    fun upsertProfile(profile: Profile){
        viewModelScope.launch{
            profileRepository.upsertProfile(profile)
        }
    }

    //TODO Bunun için upsert var.
    // Hem insert hem de update i aynı anda kullanıyor. Ne kadar mantıklı??
    fun deleteProfile(profile: Profile){
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }

    fun addTag(tag: Tag,id:String){
        if (!profile.value?.selectedTagList.orEmpty().contains(tag)) {
            val updatedList = profile.value?.selectedTagList.orEmpty() + tag
            val updatedProfile = profile.value?.copy(selectedTagList = updatedList)
            if (updatedProfile != null) {
                updateProfile(updatedProfile)
            }
        }
    }

    fun removeTag(tagId:String, id:String){
        //id'ye göre silme
        val updatedList = profile.value?.selectedTagList.orEmpty()
            .filterNot { it.id == tagId }

        val updatedProfile = profile.value?.copy(selectedTagList = updatedList)
        if (updatedProfile != null) {
            updateProfile(updatedProfile)
        }
    }

    fun updateProfile(profile: Profile){
        viewModelScope.launch{
            profileRepository.upsertProfile(profile)
        }
    }
}