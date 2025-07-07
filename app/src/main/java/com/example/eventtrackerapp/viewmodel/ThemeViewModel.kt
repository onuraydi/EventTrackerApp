package com.example.eventtrackerapp.viewmodel

import android.app.Application
import android.content.pm.PackageManager.ComponentEnabledSetting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerapp.data.source.local.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application):AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _isDarkTheme =MutableStateFlow(false)
    val isDarkTheme:StateFlow<Boolean> = _isDarkTheme


    init {
        viewModelScope.launch {
            ThemePreferences.getThemePreference(context).collect{
                _isDarkTheme.value = it
            }
        }
    }

    fun toogleTheme(enabled: Boolean){
        viewModelScope.launch {
            ThemePreferences.saveThemePreference(context,enabled)
        }
    }
}