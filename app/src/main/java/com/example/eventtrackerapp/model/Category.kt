package com.example.eventtrackerapp.model

import androidx.room.Entity

@Entity(tableName = "categories")
data class Category(
    val categoryName:String,
    val tagList:List<Tag>
)
