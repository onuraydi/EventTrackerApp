package com.example.eventtrackerapp.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val IS_PROFILE_COMPLETED = booleanPreferencesKey("is_profile_completed")
    }

    suspend fun setHasSeenOnboarding(value:Boolean){
        context.dataStore.edit { it[HAS_SEEN_ONBOARDING] = value }
    }

    suspend fun getHasSeenOnborading() : Boolean {
        return context.dataStore.data.map { it[HAS_SEEN_ONBOARDING] ?: false }.first()
    }

    suspend fun setIsProfileCompleted(value:Boolean) {
        context.dataStore.edit { it[IS_PROFILE_COMPLETED] = value }
    }

    suspend fun getIsProfileCompleted() : Boolean {
        return context.dataStore.data.map { it[IS_PROFILE_COMPLETED] ?: false  }.first()
    }
}