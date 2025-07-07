package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    val ownerId:String? = "",
    var name:String? = "",
    var detail: String? = "",
    var date:Long? = 0,
    var duration:String? = "",
    var location:String? = "",
    var categoryId:Int=0,
    var image:Int = 0,
    var likeCount:Int = 0,
    //val participants:List<Profile> = arrayListOf(),

    //TODO Yorumlar eklenecek
){

}
