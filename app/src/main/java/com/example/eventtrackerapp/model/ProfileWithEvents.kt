package com.example.eventtrackerapp.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ProfileWithEvents(
    @Embedded val profile: Profile,
    @Relation(
        parentColumn = "profileId",
        entityColumn = "eventId",
        associateBy = Junction(
            value = ProfileEventCrossRef::class,
            parentColumn = "profileId",
            entityColumn = "eventId"
        )
    )
    val eventList:List<Event>
) {
}