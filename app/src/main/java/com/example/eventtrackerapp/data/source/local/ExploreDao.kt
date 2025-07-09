package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Query
import com.example.eventtrackerapp.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface ExploreDao {

    @Query("select * from events where name like '%' || :query || '%' or detail like '%' || :query || '%' ")
    fun searchEvents(query:String) :Flow<List<Event>>
}