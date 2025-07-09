package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Comment
import com.example.eventtrackerapp.model.CommentWithProfileAndEvent
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel(application: Application):AndroidViewModel(application) {
    private val commentDao = EventTrackerDatabase.getDatabase(application,viewModelScope).commentDao()


    private val _comment = MutableStateFlow<CommentWithProfileAndEvent>(
        CommentWithProfileAndEvent(
            comment = Comment(),
            profile = Profile(),
            event = Event()
        )
    )

    val comment:StateFlow<CommentWithProfileAndEvent> = _comment

    fun addComment(profileId:String,eventId: Int,comment: String){
        viewModelScope.launch(Dispatchers.IO) {
            commentDao.insertComment(
                Comment(profileId = profileId, eventId = eventId, comment = comment)
            )
        }
    }

    // TODO buraya güncelleme gerekebilir dao kısmında da suspend yapmak gerekebilir.
    fun getComments(eventId: Int):Flow<List<CommentWithProfileAndEvent>>{
            return commentDao.getCommentsForEvent(eventId)
    }

    fun getCommentCount(eventId: Int):Flow<Int>
    {
        return commentDao.getCommentCount(eventId)
    }
}