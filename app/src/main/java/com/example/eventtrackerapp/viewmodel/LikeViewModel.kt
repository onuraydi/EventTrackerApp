package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Like
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LikeViewModel(application: Application):AndroidViewModel(application) {
    private val likeDao = EventTrackerDatabase.getDatabase(application,viewModelScope).likeDao()

    private val _like = MutableStateFlow<Like>(Like())

    val like:StateFlow<Like> = _like

    fun likeEvent(eventId:Int,profileId:String){
        viewModelScope.launch(Dispatchers.IO) {
            likeDao.insertLike(Like(eventId = eventId, profileId = profileId))
        }
    }

    fun unlikeEvent(eventId:Int,profileId: String){
        viewModelScope.launch(Dispatchers.IO) {
            likeDao.deleteLike(profileId,eventId)
        }
    }


    fun getLikeCount(eventId: Int):Flow<Int> {
        return likeDao.getLikeCount(eventId)
    }

    fun isLikedByUser(eventId: Int,profileId: String):Flow<Boolean>{
        return likeDao.hasUserLiked(eventId,profileId)
    }
}