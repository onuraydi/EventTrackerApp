package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.ProfileRepository
import com.example.eventtrackerapp.model.roommodels.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository:ProfileRepository
): ViewModel() {


    //Neden live data? ve bunu init {} içerisinde alabilir miyiz?
    val profileList : LiveData<List<Profile>> = profileRepository.getAllProfiles().asLiveData()

    //Repository de gelen veriyi dinlerken global scope kullanıyoruz.
    // Bunun için ekstra scope 'a gerek yok
    fun getById(id:String): LiveData<Profile?>{
        return profileRepository.getProfile(id).asLiveData()
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

    /*fun addTag(tag: Tag){
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
    }*/

    /*fun updateProfile(profile: Profile){
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.update(profile)
        }
    }*/
}