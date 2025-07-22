package com.example.eventtrackerapp.model.roommodels

import androidx.room.Embedded
import androidx.room.Relation

data class CommentWithProfileAndEvent(
    @Embedded val comment: Comment,
    @Relation(
        parentColumn = "profileId",
        entityColumn = "id",
        entity = Profile::class
    )
    val profile: Profile,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "id",
        entity = Event::class
    )
    val event: Event
) {
}