package com.example.eventtrackerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.SearchHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExploreViewModel(application:Application):AndroidViewModel(application) {

    val exploreDao = EventTrackerDatabase.getDatabase(application,viewModelScope).exploreDao()
    val historyDao = EventTrackerDatabase.getDatabase(application,viewModelScope).historyDao()

    val _searchList = MutableStateFlow<List<Event>>(arrayListOf())
    val searchList: StateFlow<List<Event>> = _searchList

    //DAO’dan gelen Flow’u StateFlow’a dönüştür,
    // UI'da gözlemlenebilir hale getir, sadece ihtiyaç duyulunca çalışsın,
    // ve başlangıçta boş liste taşısın
    val historyList: StateFlow<List<SearchHistory>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun searchEvents(query:String){
        viewModelScope.launch {
            exploreDao.searchEvents(query).collect{data->
                _searchList.value = data
            }
        }
    }

    fun insertHistory(keyword:String){
        viewModelScope.launch {
            historyDao.insertHistory(SearchHistory(keyword = keyword))
        }
    }

    fun deleteHistoryItem(keyword:String){
        viewModelScope.launch {
            historyDao.deleteItem(keyword)
        }
    }

    fun clearHistory(){
        viewModelScope.launch {
            historyDao.clearHistory()
        }
    }
}