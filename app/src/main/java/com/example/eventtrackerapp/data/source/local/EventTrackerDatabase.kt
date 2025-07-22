package com.example.eventtrackerapp.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.Comment
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.roommodels.EventTagCrossRef
import com.example.eventtrackerapp.model.roommodels.Like
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.ProfileEventCrossRef
import com.example.eventtrackerapp.model.roommodels.SearchHistory
import com.example.eventtrackerapp.model.roommodels.Tag

@Database(entities = [Category::class, Tag::class,Event::class, Profile::class, Comment::class, Like::class, EventTagCrossRef::class, ProfileEventCrossRef::class, SearchHistory::class],
    version = 12)
@TypeConverters(Converter::class)
abstract class EventTrackerDatabase : RoomDatabase(){

    abstract fun eventDao(): EventDao
    abstract fun categoryDao(): CategoryDao
    abstract fun profileDao(): ProfileDao
    abstract fun tagDao(): TagDao
    abstract fun profileEventDao():ProfileEventDao
    abstract fun commentDao():CommentDao
    abstract fun likeDao():LikeDao
    abstract fun exploreDao():ExploreDao
    abstract fun historyDao():HistoryDao
    //bu fonksiyon çağrıldığı yerde eğer database objesi oluşturulduysa aynı
    //obje üzerinden süreç işler.

    companion object {

        @Volatile
        private var INSTANCE: EventTrackerDatabase? = null

        fun getDatabase(context: Context): EventTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventTrackerDatabase::class.java,
                    "EventTrackerDb"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}