package com.example.eventtrackerapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Tag(
    val name:String? = "",
    val categoryId:Int? = 0
){
    @PrimaryKey(autoGenerate = true) var id:Int = 0

}
