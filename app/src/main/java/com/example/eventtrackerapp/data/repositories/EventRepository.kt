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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID


class EventRepository(
    private val firestore: FirebaseFirestore
) {
    private val eventCollection = firestore.collection("events")
    private val tagCollection = firestore.collection("tags")
    private val attendancesCollection = firestore.collection("attendances")

    fun getAllEventsWithRelations(): Flow<List<EventWithTags>> = flow {
        try {
            val eventSnapshot = eventCollection.get().await()
            val firebaseEvents = eventSnapshot.documents.mapNotNull {
                it.toObject(FirebaseEvent::class.java)
            }

            val events = firebaseEvents.map { firebaseEvent ->
                val event = EventMapper.toEntity(firebaseEvent)
                val tags = firebaseEvent.tagIds.map { tagId ->
                    Tag(id = tagId, name = "", categoryId = "")
                }
                EventWithTags(event = event, tags = tags)
            }

            emit(events)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching events from Firestore", e)
            emit(emptyList())
        }
    }

    fun getEventWithRelationsById(eventId: String): Flow<EventWithTags?> = flow {
        try {
            val doc = eventCollection.document(eventId).get().await()
            val firebaseEvent = doc.toObject(FirebaseEvent::class.java)

            firebaseEvent?.let {
                val event = EventMapper.toEntity(it)
                val tags = it.tagIds.map { tagId ->
                    Tag(id = tagId, name = "", categoryId = "")
                }
                emit(EventWithTags(event = event, tags = tags))
            } ?: emit(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching event by ID from Firestore", e)
            emit(null)
        }
    }

    fun getEventsForUser(tagIds: List<String>): Flow<List<EventWithTags>> = flow {
        try {
            val snapshot = eventCollection.get().await()
            val firebaseEvents = snapshot.documents.mapNotNull {
                it.toObject(FirebaseEvent::class.java)
            }

            val filteredEvents = firebaseEvents.filter { event ->
                event.tagIds.any { tagIds.contains(it) }
            }

            val eventsWithTags = filteredEvents.map { firebaseEvent ->
                val event = EventMapper.toEntity(firebaseEvent)
                val tags = firebaseEvent.tagIds.map { tagId ->
                    Tag(id = tagId, name = "", categoryId = "")
                }
                EventWithTags(event = event, tags = tags)
            }

            emit(eventsWithTags)
        } catch (e: Exception) {
            Log.e(TAG, "Error filtering events by tags", e)
            emit(emptyList())
        }
    }

    fun getEventsForOwner(profileId: String): Flow<List<Event>> = flow {
        try {
            val snapshot = eventCollection
                .whereEqualTo("ownerId", profileId)
                .get().await()

            val firebaseEvents = snapshot.documents.mapNotNull {
                it.toObject(FirebaseEvent::class.java)
            }

            val events = firebaseEvents.map { EventMapper.toEntity(it) }

            emit(events)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching owner events", e)
            emit(emptyList())
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

    fun getParticipationCountForEvent(eventId: String): Flow<Int> = flow {
        try {
            val snapshot = attendancesCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("isAttending", true)
                .get().await()
            emit(snapshot.size())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting participation count", e)
            emit(0)
        }
    }

    fun hasUserParticipated(eventId: String, profileId: String): Flow<Boolean> = flow {
        try {
            val docId = "$eventId-$profileId"
            val docSnapshot = attendancesCollection.document(docId).get().await()
            val attendance = docSnapshot.toObject(FirebaseAttendance::class.java)
            emit(attendance?.isAttending == true)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking attendance", e)
            emit(false)
        }
    }

    fun getEventWithParticipants(eventId: String): Flow<EventWithParticipants?> = flow {
        try {
            val eventDoc = eventCollection.document(eventId).get().await()
            val firebaseEvent = eventDoc.toObject(FirebaseEvent::class.java)

            val attendanceSnapshot = attendancesCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("isAttending", true)
                .get().await()

            val participants = attendanceSnapshot.documents.mapNotNull {
                it.toObject(FirebaseAttendance::class.java)
            }.map {
                Profile(id = it.profileId, userName = "", email = "")
            }

            firebaseEvent?.let {
                val event = EventMapper.toEntity(it)
                emit(EventWithParticipants(event, participants))
            } ?: emit(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching event with participants", e)
            emit(null)
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
