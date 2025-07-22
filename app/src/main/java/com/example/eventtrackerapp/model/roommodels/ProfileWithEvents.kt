package com.example.eventtrackerapp.model.roommodels

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.eventtrackerapp.model.Event

data class ProfileWithEvents(
    @Embedded val profile: Profile,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProfileEventCrossRef::class,
            parentColumn = "profileId",
            entityColumn = "eventId"
        )
    )
    val events:List<Event>
) {
}