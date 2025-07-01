package com.example.eventtrackerapp.Authentication
import com.google.firebase.auth.FirebaseAuth


class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email:String, password:String,passwordRepeat:String,onResult: (Boolean, String?) -> Unit){
        if(password == passwordRepeat)
        {
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        onResult(true,"Kayıt olma işlemi başarılı")
                    }
                    else {
                        onResult(false,task.exception?.message)
                    }
                }
        }else if(passwordRepeat == ""){
            onResult(false,"lütfen şifrenizi doğrulayınız")
        }
        else{
            onResult(false,"Girdiğiniz şifreler eşleşmiyor")
        }


}

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

    fun logOut(){
        firebaseAuth.signOut()
    }
}