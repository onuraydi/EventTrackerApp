package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.Tag

object TagMapper {
    fun toEntity(firebaseTag: FirebaseTag):Tag
    {
        return Tag(
            id = firebaseTag.id,
            name = firebaseTag.name,
            categoryId = firebaseTag.categoryId
        )
    }


    fun toFirebaseModel(tag:Tag):FirebaseTag
    {
        return FirebaseTag(
            id = tag.id,
            name = tag.name,
            categoryId = tag.id
        )
    }

    fun toEntityList(firebaseTags: List<FirebaseTag>):List<Tag>
    {
        return firebaseTags.map { toEntity(it) }
    }

    fun toFirebaseModelList(tags:List<Tag>):List<FirebaseTag>
    {
        return tags.map { toFirebaseModel(it) }
    }
}