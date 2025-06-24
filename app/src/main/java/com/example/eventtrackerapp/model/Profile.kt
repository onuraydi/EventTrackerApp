package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "profiles")
data class Profile(
    val fullName:String,
    val userName:String,
    val gender:String,
    val selectedCategory: Category,
    val selectedTagList:List<Tag>,
    val photo:Int,
    val addedEvents:List<Event>
    ){
    @PrimaryKey(autoGenerate = true) var id:Int = 0;
}