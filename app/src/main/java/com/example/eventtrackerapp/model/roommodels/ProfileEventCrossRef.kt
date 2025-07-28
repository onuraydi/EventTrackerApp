package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "profile_event_cross_ref",
    primaryKeys = ["profileId","eventId"],
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        ),
    ForeignKey(
        entity = Event::class,
        parentColumns = ["id"],
        childColumns = ["eventId"],
        onDelete = ForeignKey.CASCADE
    )
    ],
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["eventId"])
    ]
)
data class ProfileEventCrossRef(
    val profileId:String,
    val eventId:String,
    val isAttending:Boolean = false
)
