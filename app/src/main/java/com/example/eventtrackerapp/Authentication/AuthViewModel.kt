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
    }
}