package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "profiles")
data class Profile(
    val fullName:String? = "",
    val userName:String? = "",
    val gender:String? = "",
    val selectedCategory: Category? = Category(),
    val selectedTagList:List<Tag>? = arrayListOf(),
    val photo:Int? = 0,
    val addedEvents:List<Event>? = arrayListOf(),
    ){
    @PrimaryKey(autoGenerate = true) var id:Int = 0;
}