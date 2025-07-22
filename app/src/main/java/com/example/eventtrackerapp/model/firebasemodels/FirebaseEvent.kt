package com.example.eventtrackerapp.model.firebasemodels

import com.google.firebase.database.PropertyName

data class FirebaseEvent(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id:String = "",

    @get:PropertyName("ownerId")
    @set:PropertyName("ownerId")
    var ownerId:String = "",

    @get:PropertyName("name")
    @set:PropertyName("name")
    var name:String = "",

    @get:PropertyName("detail")
    @set:PropertyName("detail")
    var detail:String = "",

    @get:PropertyName("date")
    @set:PropertyName("date")
    var date:Long = 0,

    @get:PropertyName("duration")
    @set:PropertyName("duration")
    var duration:String = "",

    @get:PropertyName("location")
    @set:PropertyName("location")
    var location:String = "",

    @get:PropertyName("categoryId")
    @set:PropertyName("categoryId")
    var categoryId:String = "",

    @get:PropertyName("imageUrl")
    @set:PropertyName("imageUrl")
    var imageUrl:String = "",

    @get:PropertyName("likeCount")
    @set:PropertyName("likeCount")
    var likeCount:Int = 0,

    @get:PropertyName("tagIds")
    @set:PropertyName("tagIds")
    var tagIds:List<String> = listOf()
) {

}