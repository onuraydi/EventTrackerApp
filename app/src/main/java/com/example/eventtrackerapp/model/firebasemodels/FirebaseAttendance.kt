package com.example.eventtrackerapp.model.firebasemodels

import com.google.firebase.database.PropertyName

data class FirebaseAttendance(

    @get:PropertyName("eventId")
    @set:PropertyName("eventId")
    var eventId:String = "",

    @get:PropertyName("profileId")
    @set:PropertyName("profileId")
    var profileId:String = "",

    @get:PropertyName("isAttending")
    @set:PropertyName("isAttending")
    var isAttending:Boolean = false
) {
}