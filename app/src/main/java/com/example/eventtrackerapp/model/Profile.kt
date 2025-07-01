package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//uid, katılınan etkinlikler, beğenilen etkinlikler,tercihler(cat, tag, bildirimler, darkMode)
@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey var id:String = "", //TODO???
    val fullName:String? = "",
    val userName:String? = "",
    val gender:String? = "",
    val selectedCategoryList: List<Category>? = arrayListOf(),
    val selectedTagList:List<Tag>? = arrayListOf(),
    val photo:Int? = 0,
    val addedEvents:List<Event>? = arrayListOf(),
    ){
}