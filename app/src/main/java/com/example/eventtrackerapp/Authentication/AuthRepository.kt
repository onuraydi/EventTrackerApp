package com.example.eventtrackerapp.Authentication

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun login(email:String, password:String, onResult: (Boolean,String?) -> Unit){
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    onResult(true,null)
                }else{
                    onResult(false,task.exception?.message)
                }
            }
    }
}