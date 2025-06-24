package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    val name:String? = ""
){
    @PrimaryKey(autoGenerate = true) var id:Int = 0
}
