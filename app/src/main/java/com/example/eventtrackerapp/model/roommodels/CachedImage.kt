package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cachedImage")
data class CachedImage(
    @PrimaryKey val firebaseUrl:String,
    val localPath:String
)
