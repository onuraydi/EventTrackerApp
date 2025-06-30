package com.example.eventtrackerapp.Authentication

data class SignUpRequest(
    val email:String = "",
    val password:String="",
    val repeatPassword:String = ""
)
