package com.example.eventtrackerapp.model.roommodels

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EventWithProfile(
    @Embedded val event:Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProfileEventCrossRef::class,
            parentColumn = "eventId",
            entityColumn = "profileId"
        )
    )
    val profiles: List<Profile>
)