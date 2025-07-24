package com.example.eventtrackerapp.data.repositories

import android.content.Context
import android.util.Log
import com.example.eventtrackerapp.common.NetworkUtils
import com.example.eventtrackerapp.data.mappers.EventMapper
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.data.source.local.ProfileDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseAttendance
import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventTagCrossRef
import com.example.eventtrackerapp.model.roommodels.EventWithParticipants
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.ProfileEventCrossRef
import com.example.eventtrackerapp.model.roommodels.Tag
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.timerTask

class EventRepository (
    private val eventDao:EventDao,
    private val profileDao: ProfileDao,
    private val firestore: FirebaseFirestore,
    private val context:Context
) {
    private val eventCollection = firestore.collection("events")
    private val attendancesCollection = "attendances"

//    init
//    {
//        listenForFireStoreEvents()
//    }

    fun getAllEventsWithRelations(): Flow<List<EventWithTags>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            syncEventsFromFirestoreOnce()
        }
        emitAll(eventDao.getAllEventsWithRelations())
    }

    fun getEventWithRelationsById(eventId: String):Flow<EventWithTags?> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            syncEventsFromFirestoreOnce()
        }
        emitAll(eventDao.getEventWithRelationsById(eventId))
    }

    // seçtiği taglara göre getirecek
    fun getEventsForUser(tagIds:List<String>):Flow<List<EventWithTags>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            syncEventsFromFirestoreOnce()
        }
        emitAll(eventDao.getEventBySelectedTag(tagIds))
    }

    //kullanıcının eklediği etkinlikleri getirecek
    fun getEventsForOwner(profileId:String):Flow<List<Event>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            syncEventsFromFirestoreOnce()
        }
        emitAll(eventDao.getEventsByOwner(profileId))
    }

    suspend fun insertEvent(event: Event, tags:List<Tag>)
    {
        event.id = UUID.randomUUID().toString()
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

    suspend fun toggleAttendance(eventId:String, profileId:String)
    {
        val firestoreDocId = "${eventId}-${profileId}"
        val currentCrossRef = eventDao.getProfileEventCrossRef(eventId,profileId).firstOrNull()

        val newAttendingStatus = !(currentCrossRef?.isAttending ?: false)

        val updatedCrossRef = currentCrossRef?.copy(isAttending = newAttendingStatus) ?: ProfileEventCrossRef(profileId = profileId, eventId = eventId, isAttending = newAttendingStatus)
        eventDao.insertOrUpdateProfileEventCrossRef(updatedCrossRef)

        if (newAttendingStatus)
        {
            val firebaseAttendance = FirebaseAttendance(eventId = eventId, profileId = profileId)
            firestore.collection(attendancesCollection).document(firestoreDocId).set(firebaseAttendance).await()
        }
        else
        {
            firestore.collection(attendancesCollection).document(firestoreDocId).delete().await()
        }
    }

    fun getParticipationCountForEvent(eventId:String):Flow<Int>
    {
        return eventDao.getParticipationCountForEvent(eventId)
    }

    fun hasUserParticipated(eventId:String,profileId: String):Flow<Boolean>
    {
        return eventDao.hasUserParticipated(eventId,profileId).map { count -> count > 0 }
    }

    fun getEventWithParticipants(eventId:String):Flow<EventWithParticipants?>
    {
        return  eventDao.getEventWithParticipantsById(eventId)
    }


    suspend fun syncEventsFromFirestoreOnce() {
        try {
            val snapshot = firestore.collection("events").get().await()
            val firebaseEvents = snapshot.toObjects(FirebaseEvent::class.java)

            val currentRoomEventIds = eventDao.getAllEventsWithRelations().first().map { it.event.id }.toSet()
            val firebaseEventIds = firebaseEvents.map { it.id }.toSet()

            firebaseEvents.forEach { firebaseEvent ->
                val event = EventMapper.toEntity(firebaseEvent)
                eventDao.insert(event)
                eventDao.deleteEventTagCrossRefsForEvent(event.id)
                firebaseEvent.tagIds.forEach { tagId ->
                    eventDao.insertEventTagCrossRef(EventTagCrossRef(event.id, tagId))
                }
            }

            val deletedEventIds = currentRoomEventIds - firebaseEventIds
            deletedEventIds.forEach { id ->
                eventDao.deleteEvent(Event(id = id))
            }
            Log.d(TAG, "Event sync completed.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync events from Firestore", e)
        }
    }



    fun listenForFirestoreAttendances()
    {
        firestore.collection(attendancesCollection).addSnapshotListener{snapshots, e ->
            if (e != null) {
                Log.e(TAG, "Attendance listen failed", e)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    snapshots.documentChanges.forEach{ dc ->
                        val firebaseAttendance = dc.document.toObject(FirebaseAttendance::class.java)
                        val eventId = firebaseAttendance.eventId
                        val profileId = firebaseAttendance.profileId

                        val existingCrossRef = eventDao.getProfileEventCrossRef(eventId, profileId).firstOrNull()

                        when (dc.type)
                        {
                            com.google.firebase.firestore.DocumentChange.Type.ADDED,
                            com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                                val updatedCrossRef = existingCrossRef?.copy(
                                    isAttending = true) ?: ProfileEventCrossRef(
                                        profileId = profileId,
                                        eventId = eventId,
                                        isAttending = true
                                    )
                                eventDao.insertOrUpdateProfileEventCrossRef(updatedCrossRef)
                            }
                            com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                                if (existingCrossRef != null)
                                {
                                    val updatedCrossRef = existingCrossRef.copy(
                                        isAttending = false
                                    )
                                    eventDao.insertOrUpdateProfileEventCrossRef(updatedCrossRef)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun listenForFireStoreEvents()
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