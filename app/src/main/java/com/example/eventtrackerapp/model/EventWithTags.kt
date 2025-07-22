package com.example.eventtrackerapp.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EventWithTags(
    @Embedded val event:Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EventTagCrossRef::class,
            parentColumn = "eventId",
            entityColumn = "tagId",
        )
    )
    val tags:List<Tag>
)
