package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "events")
data class Event(
    val name:String,
    val detail: String,
    val date:Long,
    val duration:String,
    val location:String,
    val category:Category,
    val tagLİst:List<Tag>,
    val image:Int,
    val likeCount:Int,
    val participants:List<Profile>,

    // Yorumlar eklenecek
){
    @PrimaryKey(autoGenerate = true) var id:Int = 0;
}
