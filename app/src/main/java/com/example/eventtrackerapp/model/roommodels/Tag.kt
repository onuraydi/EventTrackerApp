package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Tag(
    @PrimaryKey
    val id:String = "",
    val name:String = "",
    val categoryId:String = ""
)
