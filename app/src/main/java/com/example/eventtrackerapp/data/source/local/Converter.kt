package com.example.eventtrackerapp.data.source.local

import androidx.room.TypeConverter
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    private val gson = Gson()

//    @TypeConverter
//    fun fromList(value:List<String>): String {
//        return value.joinToString(separator = ",")
//    }
//    @TypeConverter
//    fun toList(value: String): List<String> {
//        return value.split(",").map { it.trim() }
//    }
    @TypeConverter
    fun fromCategory(category: Category?): String? {
        return gson.toJson(category)
    }

    @TypeConverter
    fun toCategory(categoryString: String?): Category? {
        return if (categoryString.isNullOrEmpty()) null else gson.fromJson(categoryString, Category::class.java)
    }

    @TypeConverter
    fun fromTagList(list: List<Tag>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toTagList(json: String?): List<Tag>? {
        return if (json.isNullOrEmpty()) emptyList() else gson.fromJson(json, object : TypeToken<List<Tag>>() {}.type)
    }

    @TypeConverter
    fun fromEventList(list: List<Event>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toEventList(json: String?): List<Event>? {
        return if (json.isNullOrEmpty()) emptyList() else gson.fromJson(json, object : TypeToken<List<Event>>() {}.type)
    }}