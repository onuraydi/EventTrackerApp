package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.roommodels.Event

object EventMapper {
    fun toEntity(firebaseEvent: FirebaseEvent):Event{
        return Event(
            id = firebaseEvent.id,
            name = firebaseEvent.name,
            date = firebaseEvent.date,
            detail = firebaseEvent.detail,
            likeCount = firebaseEvent.likeCount,
            location = firebaseEvent.location,
            duration = firebaseEvent.duration,
            categoryId = firebaseEvent.categoryId,
            imageUrl = firebaseEvent.imageUrl,
            ownerId = firebaseEvent.ownerId
        )
    }

    fun toFirebaseModel(event: Event):FirebaseEvent
    {
        return FirebaseEvent(
            id = event.id,
            name = event.name,
            date = event.date,
            ownerId = event.ownerId,
            location = event.location,
            duration = event.duration,
            imageUrl = event.imageUrl,
            likeCount = event.likeCount,
            categoryId = event.categoryId,
            detail = event.detail,
        )
    }

    fun toEntityList(firebaseEvent: List<FirebaseEvent>):List<Event>
    {
        return firebaseEvent.map { toEntity(it) }
    }
}