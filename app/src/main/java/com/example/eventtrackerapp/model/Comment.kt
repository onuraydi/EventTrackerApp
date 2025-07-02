package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("eventId"), Index("profileId")]
)
data class Comment(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val profileId:String = "",
    val eventId:Int = 0,
    val comment:String = ""
) {
}