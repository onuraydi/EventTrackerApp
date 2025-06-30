package com.example.eventtrackerapp.Authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {

    private val authRepository = AuthRepository()

    //Only the ViewModel can post new values for this mutableState.
    // The LoginScreen will only be able to observe these new states and
    // react to them.
    var loginRequest = mutableStateOf(LoginRequest())
        private set

    fun login(email:String, password:String, onResult: (Boolean, String?) ->Unit){
        authRepository.login(email,password,onResult)
    }
}