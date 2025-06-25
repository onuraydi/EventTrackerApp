package com.example.eventtrackerapp.data.source.local

import androidx.room.TypeConverter

class TypeConverter {
    @TypeConverter
    fun fromList(value:List<String>): String {
        return value.joinToString(separator = ",")
    }

    fun toList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }
}