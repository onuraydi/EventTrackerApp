package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.repositories.EventRepository
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.data.source.local.HistoryDao
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.SearchHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val historyDao: HistoryDao // DI ile gelecek
): ViewModel() {

    private val _searchList = MutableStateFlow<List<EventWithTags>>(emptyList())
    val searchList: StateFlow<List<EventWithTags>> = _searchList

    val historyList: StateFlow<List<SearchHistory>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun searchEvents(query: String) {
        viewModelScope.launch {
            eventRepository.searchEvents(query).collect { results ->
                _searchList.value = results
            }
        }
    }

    fun insertHistory(keyword: String) {
        viewModelScope.launch {
            historyDao.insertHistory(SearchHistory(keyword = keyword))
        }
    }

    fun deleteHistoryItem(keyword: String) {
        viewModelScope.launch {
            historyDao.deleteItem(keyword)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyDao.clearHistory()
        }
    }
}
