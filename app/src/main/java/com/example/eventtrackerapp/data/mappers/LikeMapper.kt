package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseLike
import com.example.eventtrackerapp.model.roommodels.Like

object LikeMapper {

    fun toEntity(firebaseLike: FirebaseLike): Like
    {
        return Like(
            eventId = firebaseLike.eventId,
            profileId = firebaseLike.profileId
        )
    }

    fun toFirebaseModel(like:Like):FirebaseLike
    {
        return FirebaseLike(
            eventId = like.eventId,
            profileId = like.profileId
        )
    }
}