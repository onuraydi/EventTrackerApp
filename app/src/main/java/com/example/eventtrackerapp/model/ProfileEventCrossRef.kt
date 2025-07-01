package com.example.eventtrackerapp.model

import androidx.room.Entity

@Entity(
    tableName = "profile_events",
    primaryKeys = ["profileId,eventId"]
)
data class ProfileEventCrossRef(
    val profileId:String,
    val eventId:String,
    val isLiked:Boolean,
    val isAttending:Boolean
)
