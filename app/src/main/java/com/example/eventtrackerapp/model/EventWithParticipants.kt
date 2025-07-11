package com.example.eventtrackerapp.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EventWithParticipants(
    @Embedded val event:Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            ProfileEventCrossRef::class,
            parentColumn = "eventId",
            entityColumn = "profileId"
        )
    )
    val pariticipants:List<Profile>
) {
}