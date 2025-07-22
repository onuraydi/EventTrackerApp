package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_history",
    indices = [Index(value = ["keyword"], unique = true)]
)
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val keyword:String,
    val timeStamp:Long = System.currentTimeMillis()
)
