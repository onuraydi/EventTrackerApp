package com.example.eventtrackerapp.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ThemePreferences {
    private val Context.dataStore by preferencesDataStore("ThemePreferences")
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    suspend fun saveThemePreference(context: Context,isDarkMode:Boolean)
    {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDarkMode
        }
    }

    suspend fun getThemePreference(context: Context): Flow<Boolean>{
        return context.dataStore.data.map { prefs -> prefs[DARK_MODE_KEY] ?: false }
    }
}