package com.example.eventtrackerapp.Authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel:ViewModel() {
    private val authRepository = AuthRepository()

    var signUpRequest by mutableStateOf(SignUpRequest())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun signUp(onResult: (Boolean, String?) -> Unit){
        isLoading = true
        authRepository.signUp(signUpRequest.email,signUpRequest.password) {
            success,error ->
            isLoading = false
            errorMessage = error
            onResult(success,error)
        }

    //Only the ViewModel can post new values for this mutableState.
    // The LoginScreen will only be able to observe these new states and
    // react to them.
    var loginRequest = mutableStateOf(LoginRequest())
        private set

    fun login(email:String, password:String, onResult: (Boolean, String?) ->Unit){
        authRepository.login(email,password,onResult)
    }