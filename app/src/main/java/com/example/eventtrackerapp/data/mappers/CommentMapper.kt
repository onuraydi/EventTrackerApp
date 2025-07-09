package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseComment
import com.example.eventtrackerapp.model.roommodels.Comment

object CommentMapper {
    fun toEntity(firebaseComment: FirebaseComment):Comment
    {
        return Comment(
            id = firebaseComment.id,
            comment = firebaseComment.comment,
            eventId = firebaseComment.eventId,
            profileId = firebaseComment.profileId
        )
    }

    fun toFirebaseModel(comment: Comment):FirebaseComment
    {
        return FirebaseComment(
            id = comment.id,
            comment = comment.comment,
            eventId = comment.eventId,
            profileId = comment.profileId
        )
    }

    fun toEntityList(firebaseComment: List<FirebaseComment>):List<Comment>{
        return firebaseComment.map { toEntity(it) }
    }
}