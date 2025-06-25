package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    val name:String? = "",
){
    @PrimaryKey(autoGenerate = true) var id:Int = 0
}
//
//val Tags = listOf(
//    Tag("Work"),
//    Tag("Personal"),
//    Tag("Software"),
//    Tag("Important"),
//    Tag("Family"),
//    Tag("Friends"),
//    Tag("Health"),
//    Tag("Fitness"),
//    Tag("Travel"),
//    Tag("Shopping"),
//    Tag("Hobbies"),
//    Tag("Education"),
//    Tag("Finance"),
//    Tag("Home"),
//    Tag("Events")
//)