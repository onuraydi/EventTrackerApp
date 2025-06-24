package com.example.eventtrackerapp.model

import androidx.room.Entity

@Entity(tableName = "tags")
data class Tag(
    val tagName:String
)
