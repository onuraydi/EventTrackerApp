package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.EventMapper
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.data.source.local.ProfileDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventTagCrossRef
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.Tag
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.timerTask

class EventRepository (
    private val eventDao:EventDao,
    private val firestore: FirebaseFirestore
) {
    private val eventCollection = firestore.collection("events")

    init
    {
        listenForFireStoreEvents()
    }

    fun getAllEventsWithRelations(): Flow<List<EventWithTags>>
    {
        return eventDao.getAllEventsWithRelations()
            .onEach {
                // Dinleyici zaten init bloğunda kurulduğu için burada ek bir şey yapmaya gerek yok.
                // Flow'un ilk emit edildiğinde Firestore'dan veri çekmesi için onEach kullanılır.
                // Burada Room'dan gelen veriyi hemen döndürüp, arka planda senkronizasyonu sürdürüyoruz.
            }
    }

    fun getEventWithRelationsById(eventId: String):Flow<EventWithTags?>
    {
        return eventDao.getEventWithRelationsById(eventId)
    }

    // seçtiği taglara göre getirecek
    fun getEventsForUser(tagIds:List<String>):Flow<List<EventWithTags>>
    {
        return eventDao.getEventBySelectedTag(tagIds)
    }

    //kullanıcının eklediği etkinlikleri getirecek
    fun getEventsForOwner(profileId:String):Flow<List<Event>>{
        return eventDao.getEventsByOwner(profileId)
    }

    suspend fun insertEvent(event: Event, tags:List<Tag>)
    {
        eventDao.insert(event)
        eventDao.deleteEventTagCrossRefsForEvent(eventId = event.id)

        tags.forEach { tag ->
            eventDao.insertEventTagCrossRef(EventTagCrossRef(event.id,tag.id))
        }

        val tagIds = tags.map { it.id }
        val firebaseEvent = EventMapper.toFirebaseModel(event,tagIds)
        eventCollection.document(firebaseEvent.id).set(firebaseEvent)
            .addOnSuccessListener { Log.d(TAG, "Event successfully written to Firestore!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error writing event to Firestore", e) }
    }

    suspend fun updateEvent(event: Event, tags: List<Tag>)
    {
        eventDao.update(event)
        eventDao.deleteEventTagCrossRefsForEvent(event.id)
        tags.forEach { tag ->
            eventDao.insertEventTagCrossRef(EventTagCrossRef(event.id, tag.id))
        }

        val tagIds = tags.map { it.id }
        val firebaseEvent = EventMapper.toFirebaseModel(event,tagIds)
        eventCollection.document(firebaseEvent.id).set(firebaseEvent)
            .addOnSuccessListener { Log.d(TAG, "Event successfully updated in Firestore!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating event in Firestore", e) }
    }

    suspend fun deleteEvent(event: Event)
    {
        eventDao.deleteEvent(event)

        eventCollection.document(event.id).delete()
            .addOnSuccessListener { Log.d(TAG, "Event successfully deleted from Firestore!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error deleting event from Firestore", e) }

        // TODO burada event silindiğinde like ve comment ile ilgii kısımları da silmek gerekebilir.
    }

    private fun listenForFireStoreEvents()
    {
        eventCollection.addSnapshotListener { snapshot, e ->
            if (e != null)
            {
                Log.e(TAG, "Event listen failed",e)
                return@addSnapshotListener
            }
            if (snapshot != null)
            {
                val firebaseEvents = snapshot.documents.mapNotNull { it.toObject(FirebaseEvent::class.java) }
                GlobalScope.launch(Dispatchers.IO)
                {
                    val currentRoomEventIds  = eventDao.getAllEventsWithRelations().first().map { it.event.id }.toSet()
                    val firebaseEventIds = firebaseEvents.map { it.id }.toSet()

                    firebaseEvents.forEach { firebaseEvent ->
                        val roomEvent = EventMapper.toEntity(firebaseEvent)
                        eventDao.insert(roomEvent)

                        eventDao.deleteEventTagCrossRefsForEvent(firebaseEvent.id)
                        firebaseEvent.tagIds.forEach { tagId ->
                            eventDao.insertEventTagCrossRef(EventTagCrossRef(firebaseEvent.id,tagId))
                        }
                    }
                    val deletedEventIds = currentRoomEventIds.minus(firebaseEventIds)
                    deletedEventIds.forEach { eventId ->
                        eventDao.deleteEvent(Event(id = eventId))
                    }
                }
            }
        }
    }

    companion object
    {
        private const val TAG = "EventRepository"
    }
}