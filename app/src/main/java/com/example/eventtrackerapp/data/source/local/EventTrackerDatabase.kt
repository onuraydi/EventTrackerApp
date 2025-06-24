package com.example.eventtrackerapp.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.Tag

@Database(entities = [Category::class, Tag::class], version = 1)
abstract class EventTrackerDatabase : RoomDatabase(){

    abstract fun eventDao(): EventDao
    abstract fun categoryDao(): CategoryDao
    abstract fun profileDao(): ProfileDao
    abstract fun tagDao(): TagDao
    //bu fonksiyon çağrıldığı yerde eğer database objesi oluşturulduysa aynı
    //obje üzerinden süreç işler.

    companion object{

        @Volatile
        private var instance:EventTrackerDatabase ?= null

        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            instance ?: createDb(context).also{
                instance = it
            }
        }

        private fun createDb(context:Context) = Room.databaseBuilder(
            context.applicationContext,
            EventTrackerDatabase::class.java,
            "EventTrackerDatabase"
        ).build()
    }

}