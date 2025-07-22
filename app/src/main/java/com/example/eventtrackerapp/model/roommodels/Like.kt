package com.example.eventtrackerapp.model.roommodels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.eventtrackerapp.model.Event

@Entity(
    primaryKeys = ["profileId","eventId"],
    tableName = "likes",
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("profileId"),Index("eventId")]
)
data class Like(
    val profileId:String = "",
    val eventId: String = "",
) {
}