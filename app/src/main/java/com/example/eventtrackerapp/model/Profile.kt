package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//uid, katılınan etkinlikler, beğenilen etkinlikler,tercihler(cat, tag, bildirimler, darkMode)
@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey var id:String = "",
    val email:String? = "",
    val fullName:String? = "",
    val userName:String? = "",
    val gender:String? = "",
    val selectedCategoryList: List<Category>? = arrayListOf(),
    val selectedTagList:List<Tag>? = arrayListOf(),
    val photo:String? = "",
    val addedEvents:List<Event>? = arrayListOf(),
    ){
}