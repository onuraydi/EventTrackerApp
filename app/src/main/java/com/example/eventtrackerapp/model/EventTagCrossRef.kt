package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(primaryKeys = ["eventId","tagId"])
data class EventTagCrossRef(
    val eventId:Int,
    val tagId:Int
    ) {
}