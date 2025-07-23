package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.ProfileRepository
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.Tag
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

    //TODO Bunun için upsert var.
    // Hem insert hem de update i aynı anda kullanıyor. Ne kadar mantıklı??
    //aynı kayıtı eklemeyi engelleyen upsert işlemi. Aynı zamanda güncelleme işlemi de yapıyor.
    fun upsertProfile(profile: Profile){
        viewModelScope.launch{
            profileRepository.upsertProfile(profile)
        }
    }

    fun deleteProfile(profile: Profile){
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }

    fun addTag(tag: Tag,id:String){
        val profile = getById(id)
        if (!profile.value?.selectedTagList.orEmpty().contains(tag)) {
            val updatedList = profile.value?.selectedTagList.orEmpty() + tag
            val updatedProfile = profile.value?.copy(selectedTagList = updatedList)
            if (updatedProfile != null) {
                updateProfile(updatedProfile)
            }
        }
    }

    fun removeTag(tagId:String, id:String){
        val profile = getById(id)
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