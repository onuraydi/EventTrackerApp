package com.example.eventtrackerapp.model

import androidx.room.Embedded
import androidx.room.Relation

data class CommentWithProfileAndEvent(
    @Embedded val comment:Comment,
    @Relation(
        parentColumn = "profileId",
        entityColumn = "id"
    )
    val profile: Profile,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "id"
    )
    val event: Event
) {
}