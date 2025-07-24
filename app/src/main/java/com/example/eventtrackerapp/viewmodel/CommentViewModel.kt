package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerapp.data.repositories.CommentRepository
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository:CommentRepository
): ViewModel() {

    private val _commentList = MutableLiveData<List<CommentWithProfileAndEvent>>(emptyList())
    val commentList: LiveData<List<CommentWithProfileAndEvent>> = _commentList


    fun addComment(comment: Comment){
        viewModelScope.launch {
            commentRepository.upsertComment(comment)
        }
    }

    // TODO buraya güncelleme gerekebilir dao kısmında da suspend yapmak gerekebilir.
    fun getComments(eventId: String) {
        viewModelScope.launch {
            val comments = commentRepository.getCommentsForEvent(eventId).first()
            _commentList.value = comments
        }
    }

    fun getCommentCount(eventId: String):LiveData<Int>
    {
        return commentRepository.getCommentCountForEvent(eventId).asLiveData()
    }
}