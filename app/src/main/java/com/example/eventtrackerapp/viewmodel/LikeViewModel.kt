package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.LikeRepository
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.roommodels.Like
import com.example.eventtrackerapp.model.roommodels.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val likeRepository: LikeRepository
):ViewModel()
{
    private val _likeCount = MutableStateFlow<Int>(0)
    val likeCount:StateFlow<Int> = _likeCount

    private val _isEventLikedByUser = MutableStateFlow<Boolean>(false)
    val isEventLikedByUser:StateFlow<Boolean> = _isEventLikedByUser


    fun getLikeCountForEvent(eventId:String)
    {
        viewModelScope.launch {
            likeRepository.getLikeCountForEvent(eventId).collect{
                _likeCount.value = it
            }
        }
    }

    fun isEventLikedByUser(eventId: String,profileId:String)
    {
        viewModelScope.launch {
            likeRepository.isEventLikedByUser(eventId,profileId).collect{
                _isEventLikedByUser.value = it
            }
        }
    }

    fun toggleLike(eventId: String,profileId: String)
    {
        viewModelScope.launch {
            likeRepository.toggleLike(eventId,profileId)
        }
    }
}