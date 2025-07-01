package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//uid, katılınan etkinlikler, beğenilen etkinlikler,tercihler(cat, tag, bildirimler, darkMode)
@Entity(tableName = "profiles")
data class Profile(
    val fullName:String? = "",
    val userName:String? = "",
    val gender:String? = "",
//    val selectedCategory: Category? = Category(),
//    val selectedTagList:List<Tag>? = arrayListOf(),
    val photo:Int? = 0,
//    val addedEvents:List<Event>? = arrayListOf(),
    ){
    @PrimaryKey var id:String = ""; //TODO???
}