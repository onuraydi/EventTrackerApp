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
import com.example.eventtrackerapp.model.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Category::class, Tag::class,Event::class,Profile::class,Comment::class,Like::class,EventTagCrossRef::class,ProfileEventCrossRef::class],
    version = 8)
@TypeConverters(Converter::class)
abstract class EventTrackerDatabase : RoomDatabase(){

    abstract fun eventDao(): EventDao
    abstract fun categoryDao(): CategoryDao
    abstract fun profileDao(): ProfileDao
    abstract fun tagDao(): TagDao
    abstract fun profileEventDao():ProfileEventDao
    abstract fun commentDao():CommentDao
    abstract fun likeDao():LikeDao
    abstract fun participationDao():ParticipationDao
    abstract fun exploreDao():ExploreDao
    //bu fonksiyon çağrıldığı yerde eğer database objesi oluşturulduysa aynı
    //obje üzerinden süreç işler.

    companion object {

        @Volatile
        private var INSTANCE: EventTrackerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EventTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventTrackerDatabase::class.java,
                    "EventTrackerDb"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) { // <-- Burada parametre düzeltildi
                            super.onCreate(db)
                            // Room instance'ı bu noktada oluşturulmuş olacak
                            INSTANCE?.let { database ->
                                // CoroutineScope ile launch etmek için scope'u kullan
                                scope.launch {
                                    prepopulate(database.categoryDao(), database.tagDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun prepopulate(categoryDao: CategoryDao, tagDao: TagDao) {
            val sportsId = categoryDao.insert(Category(name = "Spor")).toInt()
            val techId = categoryDao.insert(Category(name = "Teknoloji")).toInt()
            val artId = categoryDao.insert(Category(name = "Sanat")).toInt()

            tagDao.insert(Tag(name = "Futbol", categoryId = sportsId))
            tagDao.insert(Tag(name = "Basketbol", categoryId = sportsId))
            tagDao.insert(Tag(name = "Tenis", categoryId = sportsId))
            tagDao.insert(Tag(name = "Voleybol", categoryId = sportsId))

            tagDao.insert(Tag(name = "Yazılım", categoryId = techId))
            tagDao.insert(Tag(name = "Donanım", categoryId = techId))
            tagDao.insert(Tag(name = "Yapay Zeka", categoryId = techId))

            tagDao.insert(Tag(name = "Resim", categoryId = artId))
            tagDao.insert(Tag(name = "Müzik", categoryId = artId))
            tagDao.insert(Tag(name = "Sinema", categoryId = artId))
            tagDao.insert(Tag(name = "Tiyatro", categoryId = artId))
            tagDao.insert(Tag(name = "Edebiyat", categoryId = artId))
        }
    }
}