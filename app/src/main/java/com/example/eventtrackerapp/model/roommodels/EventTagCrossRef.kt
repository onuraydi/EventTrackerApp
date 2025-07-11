package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity


@Entity(primaryKeys = ["eventId","tagId"])
data class EventTagCrossRef(
    val eventId:String,
    val tagId:String
    ) {
}