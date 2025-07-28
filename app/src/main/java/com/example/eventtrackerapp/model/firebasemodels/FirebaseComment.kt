package com.example.eventtrackerapp.model.firebasemodels

import com.google.firebase.database.PropertyName

data class FirebaseComment(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id:String = "",

    @get:PropertyName("profileId")
    @set:PropertyName("profileId")
    var profileId:String = "",

    @get:PropertyName("eventId")
    @set:PropertyName("eventId")
    var eventId:String = "",

    @get:PropertyName("comment")
    @set:PropertyName("comment")
    var comment:String = ""
) {
}