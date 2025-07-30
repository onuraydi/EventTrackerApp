package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.CategoryMapper
import com.example.eventtrackerapp.data.mappers.EventMapper
import com.example.eventtrackerapp.data.mappers.TagMapper
import com.example.eventtrackerapp.model.firebasemodels.FirebaseAttendance
import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventWithParticipants
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.Tag
import com.example.eventtrackerapp.views.EditEventScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID


class EventRepository(
    private val firestore: FirebaseFirestore
) {
    private val eventCollection = firestore.collection("events")
    private val tagCollection = firestore.collection("tags")
    private val attendancesCollection = firestore.collection("attendances")

    fun getAllEventsWithRelations(): Flow<List<EventWithTags>> = callbackFlow {
        val listener = eventCollection.addSnapshotListener {snapshot,error ->
            if (error != null)
            {
                Log.e(TAG,"etkinlik dinleme başarısız",error)
                close(error)
                return@addSnapshotListener
            }

            val firebaseEvents = snapshot?.documents?.mapNotNull {
                it.toObject(FirebaseEvent::class.java)
            } ?: emptyList()

            val events = firebaseEvents.map { firebaseEvent ->
                val event = EventMapper.toEntity(firebaseEvent)
                val tags = firebaseEvent.tagIds.map { id -> Tag(id,"","") }  // HATA GELEBİLİR
                EventWithTags(event,tags)
            }

            trySend(events).onFailure {
                Log.e(TAG,"event listesi gönderilemedi")
            }
        }
        awaitClose {listener.remove()}
    }

    fun getEventWithRelationsById(eventId: String): Flow<EventWithTags?> = callbackFlow {
        var listener: ListenerRegistration? = null
        
        // Boş event ID kontrolü
        if (eventId.isBlank()) {
            trySend(null)
        } else {
            listener = eventCollection.document(eventId).addSnapshotListener{snapshot, error ->
                if (error != null)
                {
                    Log.e(TAG,"Etlinlik dinleme başarısız",error)
                    close(error)
                    return@addSnapshotListener
                }

                val firebaseEvent = snapshot?.toObject(FirebaseEvent::class.java)
                firebaseEvent?.let {
                    val event = EventMapper.toEntity(it)
                    val tags = it.tagIds.map { id -> Tag(id,"","")}
                    trySend(EventWithTags(event,tags))
                } ?: trySend(null)
            }
        }
        
        awaitClose {
            listener?.remove()
        }
    }

    fun getEventsForUser(tagIds: List<String>): Flow<List<EventWithTags>> = callbackFlow {
        val listener = eventCollection.addSnapshotListener { snapshot, error ->
            if (error != null)
            {
                Log.e(TAG,"Event dinleme başarısız",error)
                close(error)
                return@addSnapshotListener
            }

            val firebaseEvent = snapshot?.documents?.mapNotNull {
                it.toObject(FirebaseEvent::class.java)
            } ?: emptyList()

            val filteredEvents = firebaseEvent.filter {
                it.tagIds.any {tagId -> tagId.contains(tagId)}
            }

            val mapped = filteredEvents.map {
                val event = EventMapper.toEntity(it)
                val tags = it.tagIds.map { tagId -> Tag(tagId,"","") }
                EventWithTags(event,tags)
            }

            trySend(mapped)
        }
        awaitClose {
            listener.remove()
        }
    }

    fun getEventsForOwner(profileId: String): Flow<List<Event>> = callbackFlow {
        var listener: ListenerRegistration? = null
        
        // Boş profile ID kontrolü
        if (profileId.isBlank()) {
            trySend(emptyList())
        } else {
            listener = eventCollection
                .whereEqualTo("ownerId",profileId)
                .addSnapshotListener { snapshot, error ->
                if (error != null)
                {
                    Log.e(TAG,"Event dinleme başarısız",error)
                    close(error)
                    return@addSnapshotListener
                }

                val firebaseEvent = snapshot?.documents?.mapNotNull {
                    it.toObject(FirebaseEvent::class.java)
                } ?: emptyList()

                trySend(firebaseEvent.map { EventMapper.toEntity(it) })
            }
        }

        awaitClose{
            listener?.remove()
        }
    }

    suspend fun insertEvent(event: Event, tags: List<Tag>) {
        val id = UUID.randomUUID().toString()
        event.id = id
        val tagIds = tags.map { it.id }
        val firebaseEvent = EventMapper.toFirebaseModel(event, tagIds)

        try {
            eventCollection.document(id).set(firebaseEvent).await()
            Log.d(TAG, "Event added to Firestore.")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding event to Firestore", e)
        }
    }

    suspend fun updateEvent(event: Event, tags: List<Tag>) {
        val tagIds = tags.map { it.id }
        val firebaseEvent = EventMapper.toFirebaseModel(event, tagIds)

        try {
            eventCollection.document(event.id).set(firebaseEvent).await()
            Log.d(TAG, "Event updated in Firestore.")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating event", e)
        }
    }

    suspend fun deleteEvent(event: Event) {
        try {
            eventCollection.document(event.id).delete().await()
            Log.d(TAG, "Event deleted from Firestore.")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting event", e)
        }
    }

    suspend fun toggleAttendance(eventId: String, profileId: String) {
        // Boş ID kontrolü
        if (eventId.isBlank() || profileId.isBlank()) {
            Log.w(TAG, "toggleAttendance: Boş eventId veya profileId")
            return
        }
        
        val docId = "${eventId}-$profileId"
        val docSnapshot = attendancesCollection.document(docId).get().await()
        val current = docSnapshot.toObject(FirebaseAttendance::class.java)
        val newStatus = !(current?.isAttending ?: false)

        if (newStatus) {
            val attendance = FirebaseAttendance(eventId, profileId, true)
            attendancesCollection.document(docId).set(attendance).await()
        } else {
            attendancesCollection.document(docId).delete().await()
        }
    }

    fun getParticipationCountForEvent(eventId: String): Flow<Int> = callbackFlow {
        var listener: ListenerRegistration? = null
        
        // Boş event ID kontrolü
        if (eventId.isBlank()) {
            trySend(0)
        } else {
            listener = attendancesCollection
                .whereEqualTo("eventId",eventId)
                .whereEqualTo("isAttending",true)
                .addSnapshotListener {snapshot, error ->
                    if (error != null)
                    {
                        Log.e(TAG,"Katılımcı dinleme başarısız",error)
                        close(error)
                        return@addSnapshotListener
                    }

                    trySend(snapshot?.size() ?: 0)
                }
        }
        
        awaitClose{
            listener?.remove()
        }
    }

    fun hasUserParticipated(eventId: String, profileId: String): Flow<Boolean> = callbackFlow {
        var listener: ListenerRegistration? = null
        
        // Boş event ID kontrolü
        if (eventId.isBlank() || profileId.isBlank()) {
            trySend(false)
        } else {
            val docId = "$eventId-$profileId"
            listener = attendancesCollection.document(docId)
                .addSnapshotListener {snapshot,error ->
                    if (error != null)
                    {
                        Log.e(TAG,"Katılımcı dinleme başarısız",error)
                        close(error)
                        return@addSnapshotListener
                    }

                    val attended = snapshot?.toObject(FirebaseAttendance::class.java)?.isAttending == true
                    trySend(attended)
                }
        }
        
        awaitClose {
            listener?.remove()
        }
    }

    fun getEventWithParticipants(eventId: String): Flow<EventWithParticipants?> = callbackFlow {
        var attendanceListener: ListenerRegistration? = null
        var eventListener: ListenerRegistration? = null

        // Boş event ID kontrolü
        if (eventId.isBlank()) {
            trySend(null)
        } else {
            eventListener = eventCollection.document(eventId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "event dinleme başarısız", error)
                        trySend(null)
                        return@addSnapshotListener
                    }

                    val firebaseEvent = snapshot?.toObject(FirebaseEvent::class.java)
                    if (firebaseEvent == null) {
                        trySend(null)
                        return@addSnapshotListener
                    }

                    // Eski listener varsa temizle
                    attendanceListener?.remove()

                    attendanceListener = attendancesCollection
                        .whereEqualTo("eventId", eventId)
                        .whereEqualTo("isAttending", true)
                        .addSnapshotListener { attendanceSnapshot, attendanceError ->
                            if (attendanceError != null) {
                                Log.e(TAG, "Katılımcı dinleme başarısız", attendanceError)
                                return@addSnapshotListener
                            }

                            val firebaseParticipants = attendanceSnapshot?.documents?.mapNotNull {
                                it.toObject(FirebaseAttendance::class.java)
                            } ?: emptyList()

                            val participants = firebaseParticipants.map {
                                Profile(id = it.profileId, userName = "", email = "")
                            }

                            val event = EventMapper.toEntity(firebaseEvent)
                            trySend(EventWithParticipants(event, participants))
                        }
                }
        }

        awaitClose {
            Log.d(TAG, "Tüm listenerlar kapatılıyor")
            eventListener?.remove()
            attendanceListener?.remove()
        }
    }

    fun searchEvents(query: String): Flow<List<EventWithTags>> = flow {
        val results = mutableListOf<EventWithTags>()
        try {
            val snapshot = eventCollection.get().await()

            val filtered = snapshot.documents.mapNotNull { it.toObject(FirebaseEvent::class.java) }
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.detail.contains(query, ignoreCase = true)
                }

            for (event in filtered) {
                val tagSnapshot = tagCollection.whereIn("id", event.tagIds).get().await()
                val firebaseTags = tagSnapshot.documents.mapNotNull { it.toObject(FirebaseTag::class.java) }

                if (firebaseTags != null) {
                    val eventWithTags = EventWithTags(
                        event = EventMapper.toEntity(event),
                        tags = firebaseTags.map { TagMapper.toEntity(it) }
                    )
                    results.add(eventWithTags)
                }
            }

            emit(results)
        } catch (e: Exception) {
            Log.e("FirestoreSearch", "Error searching events", e)
            emit(emptyList())
        }
    }



    companion object {
        private const val TAG = "EventRepository"
    }
}
