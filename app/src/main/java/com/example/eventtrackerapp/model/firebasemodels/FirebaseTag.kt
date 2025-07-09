package com.example.eventtrackerapp.model.firebasemodels

import com.google.firebase.database.PropertyName

data class FirebaseTag(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id:String = "",

    @get:PropertyName("name")
    @set:PropertyName("name")
    var name:String = "",

    @get:PropertyName("categoryId")
    @set:PropertyName("categoryId")
    var categoryId:String = ""
)
{
    constructor(): this("","","")
}
