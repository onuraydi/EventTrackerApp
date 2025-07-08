package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object EventMapper {
    private val gson = Gson()

    fun toEntity(firebaseEvent: FirebaseEvent):Event{
        var tagIds = gson.toJson(firebaseEvent.tagIds)

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
            ownerId = firebaseEvent.ownerId,
            tagIds = tagIds
        )
    }

    fun toFirebaseModel(event: Event):FirebaseEvent
    {
        val type = object : TypeToken<Map<String, Boolean>>() {}.type
        val tagIdsMap = gson.fromJson<Map<String,Boolean>>(event.tagIds,type)

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
            tagIds = tagIdsMap
        )
    }

    fun toEntityList(firebaseEvent: List<FirebaseEvent>):List<Event>
    {
        return firebaseEvent.map { toEntity(it) }
    }
}