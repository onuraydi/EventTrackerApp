package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.CommentRepository
import com.example.eventtrackerapp.model.roommodels.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository:CommentRepository
): ViewModel() {


    fun addComment(comment: Comment){
        viewModelScope.launch {
            commentRepository.upsertComment(comment)
        }
    }

    // TODO buraya güncelleme gerekebilir dao kısmında da suspend yapmak gerekebilir.
    fun getComments(eventId: String):LiveData<List<Comment>>{
            return commentRepository.getCommentsForEvent(eventId).asLiveData()
    }

    fun getCommentCount(eventId: String):LiveData<Int>
    {
        return commentRepository.getCommentCountForEvent(eventId).asLiveData()
    }
}