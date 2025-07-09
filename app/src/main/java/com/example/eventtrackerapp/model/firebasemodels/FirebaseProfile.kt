package com.example.eventtrackerapp.model.firebasemodels

import com.google.firebase.database.PropertyName

data class FirebaseProfile(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id:String = "",

    @get:PropertyName("email")
    @set:PropertyName("email")
    var email:String = "",

    @get:PropertyName("fullName")
    @set:PropertyName("fullName")
    var fullName:String = "",

    @get:PropertyName("userName")
    @set:PropertyName("userName")
    var userName:String = "",

    @get:PropertyName("gender")
    @set:PropertyName("gender")
    var gender:String = "",

    @get:PropertyName("selectedCategoryIds")
    @set:PropertyName("selectedCategoryIds")
    var selectedCategoryIds:Map<String,Boolean> = emptyMap(),

    @get:PropertyName("selectedTagIds")
    @set:PropertyName("selectedTagIds")
    var selectedTagIds:Map<String,Boolean> = emptyMap(),

    @get:PropertyName("profileImageUrl")
    @set:PropertyName("profileImageUrl")
    var profileImageUrl:String = ""


    /// TODO Buraya added event eklenmedi o başka bir yerde yöentilecek (userevent düğümü)

){
    constructor() : this("", "", "", "", "", emptyMap(), emptyMap(), "")
}
