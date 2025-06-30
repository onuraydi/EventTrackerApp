package com.example.eventtrackerapp.Authentication


import androidx.compose.runtime.traceEventEnd
import com.google.firebase.auth.FirebaseAuth


class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email:String, password:String,onResult: (Boolean, String?) -> Unit){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    onResult(true,"Kayıt olma işlemi başarılı")
                }
                else {
                    onResult(false,task.exception?.message)
                }
            }
    }
}