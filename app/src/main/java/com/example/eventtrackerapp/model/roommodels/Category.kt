package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    var id:String = "",
    val name:String = ""
)
