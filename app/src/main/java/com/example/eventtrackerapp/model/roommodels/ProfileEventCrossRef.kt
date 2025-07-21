package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity

@Entity(
    primaryKeys = ["profileId","eventId"]
)
data class ProfileEventCrossRef(
    val profileId:String,
    val eventId:String,
//    val isLiked:Boolean,
//    val isAttending:Boolean
)
