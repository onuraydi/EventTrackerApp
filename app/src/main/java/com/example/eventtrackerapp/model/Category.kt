package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    val name:String,
    val tagList:List<Tag>
){
    @PrimaryKey(autoGenerate = true) var id:Int = 0
}
