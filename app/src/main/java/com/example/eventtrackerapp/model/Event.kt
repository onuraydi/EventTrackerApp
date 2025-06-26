package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "events")
data class Event(
    val name:String? = "",
    val detail: String? = "",
    val date:Long? = 0,
    val duration:String? = "",
    val location:String? = "",
    val categoryId:Int=0,
    val image:Int = 0,
    val likeCount:Int = 0,
    //val participants:List<Profile> = arrayListOf(),

    // Yorumlar eklenecek
){
    @PrimaryKey(autoGenerate = true) var id:Int = 0;
}
