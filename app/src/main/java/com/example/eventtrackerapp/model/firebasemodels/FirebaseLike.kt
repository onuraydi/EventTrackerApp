package com.example.eventtrackerapp.model.firebasemodels

import com.google.firebase.database.PropertyName

data class FirebaseLike(
    @get:PropertyName("eventId")
    @set:PropertyName("eventId")
    var eventId:String = "",

    @get:PropertyName("profileId")
    @set:PropertyName("profileId")
    var profileId:String = ""
) {
}