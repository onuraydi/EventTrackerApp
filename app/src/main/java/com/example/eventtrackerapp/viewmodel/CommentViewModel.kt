package com.example.eventtrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.CommentRepository
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository:CommentRepository
): ViewModel() {

    private val _commentList = MutableStateFlow<List<CommentWithProfileAndEvent>>(emptyList())
    val commentList: StateFlow<List<CommentWithProfileAndEvent>> = _commentList

    private val _commentCount = MutableStateFlow<Int>(0)
    val commentCount: StateFlow<Int> = _commentCount


    fun addComment(comment: Comment){
        viewModelScope.launch {
            commentRepository.upsertComment(comment)
        }
    }

    // TODO buraya güncelleme gerekebilir dao kısmında da suspend yapmak gerekebilir.
    fun getComments(eventId: String) {
        viewModelScope.launch {
            commentRepository.getCommentsForEvent(eventId).collect{comments->
                _commentList.value = comments
            }
        }
    }

    fun getCommentCount(eventId: String)
    {
        viewModelScope.launch {
            commentRepository.getCommentCountForEvent(eventId).collect{
                _commentCount.value = it
            }
        }
    }
}