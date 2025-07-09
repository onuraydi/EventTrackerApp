package com.example.eventtrackerapp.model.firebasemodels

import com.google.firebase.database.PropertyName

data class FirebaseCategoryTags(
    @get:PropertyName("categoryId")
    @set:PropertyName("categoryId")
    var categoryId:String = "",

    @get:PropertyName("tagIds")
    @set:PropertyName("tagIds")
    var tagIds:List<String> = listOf()
) {
}