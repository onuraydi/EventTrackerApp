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
    var selectedCategoryIds:List<String> = listOf(),

    @get:PropertyName("selectedTagIds")
    @set:PropertyName("selectedTagIds")
    var selectedTagIds:List<String> = listOf(),

    @get:PropertyName("addedEventIds")
    @set:PropertyName("addedEventIds")
    var addedEventIds:List<String> = listOf(),

    @get:PropertyName("profileImageUrl")
    @set:PropertyName("profileImageUrl")
    var profileImageUrl:String = ""
){
}
