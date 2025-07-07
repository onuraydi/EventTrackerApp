package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventTagCrossRef
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    suspend fun getAll():List<Event>

    @Query("Select * FROM events WHERE id = :id")
    suspend fun getById(id:Int): Event

    @Insert
    suspend fun add(event: Event):Long

    @Update
    suspend fun update(event: Event):Int

    @Query("delete from events where id = :eventId")
    suspend fun delete(eventId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEventTags(crossRef: List<EventTagCrossRef>)

    @Update
    suspend fun updateEventWithTags(crossRef: List<EventTagCrossRef>)

    @Transaction
    @Query("SELECT * FROM events")
    fun getAllEventsWithTags(): Flow<List<EventWithTags>>

    @Query("select * from events where ownerId = :ownerId")
    fun getEventsByOwner(ownerId:String):Flow<List<Event>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventWithTagsByEventId(eventId:Int): EventWithTags

    // TODO ??
    @Transaction
    @Query("""
    SELECT DISTINCT E.*
    FROM events E
    INNER JOIN EventTagCrossRef ETC ON E.id = ETC.eventId
    WHERE ETC.tagId IN (:tagIds)
""")
    suspend fun getEventBySelectedTag(tagIds:List<Int>):List<EventWithTags>

    @Query("DELETE FROM EventTagCrossRef WHERE eventId = :eventId")
    suspend fun deleteTagsForEvent(eventId: Int)
}