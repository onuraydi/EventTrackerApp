package com.example.eventtrackerapp.model

import androidx.room.Entity

@Entity(
    primaryKeys = ["eventId,profileId"]
)
data class ProfileEventCrossRef(
    val profileId:String,
    val eventId:Int,
    val isLiked:Boolean,
    val isAttending:Boolean
)
