package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"),Index("ownerId")]
    )
data class Event(
    @PrimaryKey
    var id:String = "",
    val ownerId:String = "",
    var name:String = "",
    var detail: String = "",
    var date:Long = 0,
    var duration:String = "",
    var location:String = "",
    var categoryId:String = "",
    var image:String = "",
    var likeCount:Int = 0,
){

}
