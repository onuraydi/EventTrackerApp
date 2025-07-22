package com.example.eventtrackerapp.data.source.local

import android.content.Context
import androidx.compose.animation.expandVertically
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.driver.SupportSQLiteConnection
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.Comment
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.EventTagCrossRef
import com.example.eventtrackerapp.model.Like
import com.example.eventtrackerapp.model.Profile
import com.example.eventtrackerapp.model.ProfileEventCrossRef
import com.example.eventtrackerapp.model.SearchHistory
import com.example.eventtrackerapp.model.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Category::class, Tag::class,Event::class,Profile::class,Comment::class,Like::class,EventTagCrossRef::class,ProfileEventCrossRef::class,SearchHistory::class],
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