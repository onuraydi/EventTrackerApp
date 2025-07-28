package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventtrackerapp.model.roommodels.CachedImage

@Dao
interface CachedImageDao {

    @Query("SELECT * FROM cachedimage WHERE firebaseUrl = :url LIMIT 1")
    suspend fun getByUrl(url:String):CachedImage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image:CachedImage)

}
