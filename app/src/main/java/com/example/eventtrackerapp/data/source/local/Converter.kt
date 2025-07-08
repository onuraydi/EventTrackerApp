package com.example.eventtrackerapp.data.source.local

import androidx.room.TypeConverter
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    private val gson = Gson()

    @TypeConverter
    fun fromList(value:List<String>): String {
        return value.joinToString(separator = ",")
    }
    @TypeConverter
    fun toList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }
    @TypeConverter
    fun fromCategoryList(categoryList: List<Category>?): String? {
        return gson.toJson(categoryList)
    }

    @TypeConverter
    fun toCategoryList(categoryString: String?): List<Category>? {
        return if (categoryString.isNullOrEmpty()) emptyList() else gson.fromJson(categoryString, object : TypeToken<List<Category>>() {}.type)
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