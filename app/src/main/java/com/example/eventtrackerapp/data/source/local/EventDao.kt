package com.example.eventtrackerapp.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventTagCrossRef
import com.example.eventtrackerapp.model.roommodels.EventWithParticipants
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.ProfileEventCrossRef
import com.example.eventtrackerapp.model.roommodels.ProfileWithEvents
import com.google.firebase.database.core.utilities.ImmutableTree
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert
    suspend fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(event : List<Event>)

    @Update
    suspend fun update(event: Event)

    @Query("SELECT * FROM events")
    fun getAll():Flow<List<Event>>

    @Transaction
    @Query("SELECT * FROM events")
    fun getAllEventsWithRelations():Flow<List<EventWithTags>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventWithRelationsById(eventId: String): Flow<EventWithTags?>

    @Query("Select * FROM events WHERE id = :id")
    fun getById(id:String): Flow<Event>

    @Transaction
    @Query("""
    SELECT DISTINCT E.*
    FROM events E
    INNER JOIN EventTagCrossRef ETC ON E.id = ETC.eventId
    WHERE ETC.tagId IN (:tagIds)
""")
    fun getEventBySelectedTag(tagIds:List<String>):Flow<List<EventWithTags>>


    // etkinliğe katılım işlemleri
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfileEventCrossRef(crossRef: ProfileEventCrossRef)

    @Query("SELECT * FROM profile_event_cross_ref WHERE eventId = :eventId AND profileId = :profileId LIMIT 1")
    fun getProfileEventCrossRef(eventId: String, profileId:String):Flow<ProfileEventCrossRef?>

    @Query("SELECT COUNT(*) FROM profile_event_cross_ref WHERE eventId = :eventId AND isAttending = 1")
    fun getParticipationCountForEvent(eventId: String):Flow<Int>

    @Query("Select Count(*) FROM profile_event_cross_ref WHERE eventId = :eventId AND profileId = :profileId AND isAttending = 1")
    fun hasUserParticipated(eventId : String,profileId: String):Flow<Int>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    fun getEventWithParticipantsById(eventId:String):Flow<EventWithParticipants?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventTagCrossRef(crossRef: EventTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEventTagCrossRef(crossRef: List<EventTagCrossRef>)

    @Delete
    suspend fun deleteEventTagCrossRef(crossRef: EventTagCrossRef)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM EventTagCrossRef WHERE eventId = :eventId")
    suspend fun deleteEventTagCrossRefsForEvent(eventId: String)

    @Query("SELECT * FROM events")
    suspend fun getAllEventOnce():List<Event>

    @Query("SELECT * FROM events WHERE categoryId = :categoryId")
    fun getEventsByCategoryId(categoryId:String):Flow<List<Event>>

    @Query("SELECT * FROM events WHERE ownerId = :ownerId")
    suspend fun getEventsByOwnerIdOnce(ownerId: String):List<Event>



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


    @Query("DELETE FROM EventTagCrossRef WHERE eventId = :eventId")
    suspend fun deleteTagsForEvent(eventId: Int)
}