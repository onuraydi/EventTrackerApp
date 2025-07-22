package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventtrackerapp.model.roommodels.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("select * from search_history order by timeStamp desc")
     fun getAllHistory(): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //aynı kelime tekrar eklenmez
    suspend fun insertHistory(item: SearchHistory)

    @Query("delete from search_history where keyword = :keyword")
    suspend fun deleteItem(keyword:String)

    @Query("delete from search_history")
    suspend fun clearHistory()
}