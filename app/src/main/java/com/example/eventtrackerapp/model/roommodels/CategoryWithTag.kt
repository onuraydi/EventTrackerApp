package com.example.eventtrackerapp.model.roommodels

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithTag(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val tags: List<Tag>
)
