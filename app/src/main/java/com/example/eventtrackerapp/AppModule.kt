package com.example.eventtrackerapp

import android.content.Context
import com.example.eventtrackerapp.data.repositories.CategoryRepository
import com.example.eventtrackerapp.data.repositories.CommentRepository
import com.example.eventtrackerapp.data.repositories.EventRepository
import com.example.eventtrackerapp.data.repositories.LikeRepository
import com.example.eventtrackerapp.data.repositories.ProfileRepository
import com.example.eventtrackerapp.data.repositories.StorageCacheRepository
import com.example.eventtrackerapp.data.source.local.CachedImageDao
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.CommentDao
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.data.source.local.EventTrackerDatabase
import com.example.eventtrackerapp.data.source.local.ExploreDao
import com.example.eventtrackerapp.data.source.local.HistoryDao
import com.example.eventtrackerapp.data.source.local.LikeDao
import com.example.eventtrackerapp.data.source.local.ProfileDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    //Firebase Bağımlılıkları
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    //Room Bağımlılıkları
    //TODO DATABASE DÜZELTİLECEK. SCOPE SORUNLU
    @Provides
    @Singleton
    fun provideEventDatabase(@ApplicationContext context: Context): EventTrackerDatabase {
        //EventTrackerDatabase.getDatabase() metodu, Context bağımlılığına sahiptir.
        // Hilt, @ApplicationContext annotation'ı sayesinde doğru Context'i otomatik olarak sağlar.
        return EventTrackerDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: EventTrackerDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(database: EventTrackerDatabase): TagDao {
        return database.tagDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: EventTrackerDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideLikeDao(database: EventTrackerDatabase): LikeDao {
        return database.likeDao()
    }

    @Provides
    @Singleton
    fun provideCommentDao(database: EventTrackerDatabase): CommentDao {
        return database.commentDao()
    }

    @Provides
    @Singleton
    fun provideExploreDao(database: EventTrackerDatabase): ExploreDao {
        return database.exploreDao()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: EventTrackerDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideProfileDao(database: EventTrackerDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    @Singleton
    fun provideCacheDao(database: EventTrackerDatabase): CachedImageDao {
        return database.cachedImageDao()
    }

    //Repository Bağımlılıkları
    // Her Repository için bir @Provides metodu tanımlıyoruz.
    // Bu metotların parametreleri (örn. categoryDao, tagDao, firestore),
    // Hilt'in zaten yukarıda tanımladığımız @Provides metotlarından sağlayacağı bağımlılıklardır.
    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao, tagDao: TagDao, firestore: FirebaseFirestore, @ApplicationContext context: Context): CategoryRepository {
        return CategoryRepository(categoryDao,tagDao,firestore,context)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(commentDao: CommentDao, firestore: FirebaseFirestore, @ApplicationContext context: Context): CommentRepository {
        return CommentRepository(commentDao,firestore,context)
    }

    @Provides
    @Singleton
    fun provideEventRepository(eventDao: EventDao, profileDao: ProfileDao, firestore: FirebaseFirestore, @ApplicationContext context: Context): EventRepository {
        return EventRepository(eventDao,profileDao,firestore,context)
    }

    @Provides
    @Singleton
    fun provideLikeRepository(likeDao: LikeDao, firestore: FirebaseFirestore, @ApplicationContext context: Context): LikeRepository {
        return LikeRepository(likeDao,firestore,context)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileDao: ProfileDao, categoryDao: CategoryDao, tagDao: TagDao, firestore: FirebaseFirestore, @ApplicationContext context: Context): ProfileRepository {
        return ProfileRepository(profileDao,categoryDao,tagDao,firestore,context)
    }

    @Provides
    @Singleton
    fun provideCacheRepository(cachedImageDao: CachedImageDao, @ApplicationContext context: Context): StorageCacheRepository {
        return StorageCacheRepository(cachedImageDao,context)
    }
}