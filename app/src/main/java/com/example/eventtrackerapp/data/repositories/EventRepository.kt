package com.example.eventtrackerapp.data.repositories

import android.content.Context
import android.util.Log
import com.example.eventtrackerapp.common.NetworkUtils
import com.example.eventtrackerapp.data.mappers.AttendanceMapper
import com.example.eventtrackerapp.data.mappers.EventMapper
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.data.source.local.ProfileDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseAttendance
import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventTagCrossRef
import com.example.eventtrackerapp.model.roommodels.EventWithParticipants
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.model.roommodels.ProfileEventCrossRef
import com.example.eventtrackerapp.model.roommodels.Tag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
class EventRepository (
    private val eventDao:EventDao,
    private val profileDao: ProfileDao,
    private val firestore: FirebaseFirestore,
    private val context:Context
) {
    private val eventCollection = firestore.collection("events")
    private val attendancesCollection = firestore.collection("attendances")

    fun getAllEventsWithRelations(): Flow<List<EventWithTags>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val eventSnapshot = eventCollection.get().await()
                val firebaseEvents = eventSnapshot.documents.mapNotNull { it.toObject(FirebaseEvent::class.java) }
                val roomEvents = firebaseEvents.map { EventMapper.toEntity(it) }


                roomEvents.forEach {
                    eventDao.deleteEventTagCrossRefsForEvent(it.id)
                }
                eventDao.insertAll(roomEvents)

                val eventTagCrossRefs = firebaseEvents.flatMap { firebaseEvent ->
                    firebaseEvent.tagIds.map { tagId ->
                        EventTagCrossRef(eventId = firebaseEvent.id, tagId = tagId)
                    }
                }
                eventDao.insertAllEventTagCrossRef(eventTagCrossRefs)
                emit(eventDao.getAllEventsWithRelations().first())

            } catch (e: Exception) {
                Log.e("EventRepo", "Firestore'dan event verisi alınamadı, roomdan geliyor", e)
                emit(eventDao.getAllEventsWithRelations().first())
            }
        } else {
            emit(eventDao.getAllEventsWithRelations().first())
        }
    }


    fun getEventWithRelationsById(eventId: String): Flow<EventWithTags?> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val eventSnapshot = eventCollection.get().await()
                val firebaseEvents = eventSnapshot.documents.mapNotNull { it.toObject(FirebaseEvent::class.java) }
                val roomEvents = firebaseEvents.map { EventMapper.toEntity(it) }

                // Room'a event'leri ekle

                roomEvents.forEach {
                    eventDao.deleteEventTagCrossRefsForEvent(it.id)
                }

                eventDao.insertAll(roomEvents)

                // Event-Tag ilişkilerini oluştur
                val eventTagCrossRefs = firebaseEvents.flatMap { firebaseEvent ->
                    firebaseEvent.tagIds.map { tagId ->
                        EventTagCrossRef(eventId = firebaseEvent.id, tagId = tagId)
                    }
                }

                eventDao.insertAllEventTagCrossRef(eventTagCrossRefs)
                emitAll(eventDao.getEventWithRelationsById(eventId))

            } catch (e: Exception) {
                Log.e("EventRepo", "Firestore'dan event verisi alınamadı", e)
                emitAll(eventDao.getEventWithRelationsById(eventId))
            }
        }else
        {
            emitAll(eventDao.getEventWithRelationsById(eventId))
        }
    }

    fun getEventsForUser(tagIds: List<String>): Flow<List<EventWithTags>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val eventSnapshot = eventCollection.get().await()
                val firebaseEvents = eventSnapshot.documents.mapNotNull { it.toObject(FirebaseEvent::class.java) }
                val roomEvents = firebaseEvents.map { EventMapper.toEntity(it) }

                roomEvents.forEach {
                    eventDao.deleteEventTagCrossRefsForEvent(it.id)
                }
                eventDao.insertAll(roomEvents)

                val eventTagCrossRefs = firebaseEvents.flatMap { firebaseEvent ->
                    firebaseEvent.tagIds.map { tagId ->
                        EventTagCrossRef(eventId = firebaseEvent.id, tagId = tagId)
                    }
                }
                eventDao.insertAllEventTagCrossRef(eventTagCrossRefs)
                emitAll(eventDao.getEventBySelectedTag(tagIds))

            } catch (e: Exception) {
                Log.e("EventRepo", "Firestore'dan event verisi alınamadı", e)
                emitAll(eventDao.getEventBySelectedTag(tagIds))
            }
        }
        else
        {
            emitAll(eventDao.getEventBySelectedTag(tagIds))
        }
    }


    fun getEventsForOwner(profileId: String): Flow<List<Event>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val eventSnapshot = eventCollection.get().await()
                val firebaseEvents = eventSnapshot.documents.mapNotNull { it.toObject(FirebaseEvent::class.java) }
                val roomEvents = firebaseEvents.map { EventMapper.toEntity(it) }

                roomEvents.forEach {
                    eventDao.deleteEventTagCrossRefsForEvent(it.id)
                }
                eventDao.insertAll(roomEvents)

                val eventTagCrossRefs = firebaseEvents.flatMap { firebaseEvent ->
                    firebaseEvent.tagIds.map { tagId ->
                        EventTagCrossRef(eventId = firebaseEvent.id, tagId = tagId)
                    }
                }

                eventDao.insertAllEventTagCrossRef(eventTagCrossRefs)
                emitAll(eventDao.getEventsByOwner(profileId))

            } catch (e: Exception) {
                Log.e("EventRepo", "Firestore'dan veri alınamadı", e)
                // Firestore başarısızsa Room'dan devam
                emitAll(eventDao.getEventsByOwner(profileId))
            }
        }
        else
        {
            emitAll(eventDao.getEventsByOwner(profileId))
        }
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
            val firebaseAttendance = FirebaseAttendance(eventId = eventId, profileId = profileId, isAttending = newAttendingStatus)
            attendancesCollection.document(firestoreDocId).set(firebaseAttendance).await()
        }
        else
        {
            eventDao.deleteProfileEventCrossRef(eventId,profileId)
            attendancesCollection.document(firestoreDocId).delete().await()
        }
    }

    fun getParticipationCountForEvent(eventId:String):Flow<Int> = flow {

        if (NetworkUtils.isNetworkAvailable(context)){
            try {
                val attendanceSnapshot = attendancesCollection.get().await()
                val firebaseAttendances = attendanceSnapshot.documents.mapNotNull { it.toObject(FirebaseAttendance::class.java) }
                val roomAttendances = firebaseAttendances.map { AttendanceMapper.toEntity(it) }

                //Room'a eklemek için
                eventDao.insertAllProfileEventCrossRef(roomAttendances)
                emitAll(eventDao.getParticipationCountForEvent(eventId))

            }catch (e:Exception){
                Log.e(TAG,"Attendances sayısı firestore'dan gelemedi",e)
                emitAll(eventDao.getParticipationCountForEvent(eventId))
            }
        }else{
            emitAll(eventDao.getParticipationCountForEvent(eventId))
        }
    }

    fun hasUserParticipated(eventId:String,profileId: String):Flow<Boolean> = flow {
        if(NetworkUtils.isNetworkAvailable(context)){
            try {
                val snapshot = attendancesCollection.get().await()
                val firebaseAttendances = snapshot.documents.mapNotNull { it.toObject(FirebaseAttendance::class.java) }
                val roomAttendances = firebaseAttendances.map { AttendanceMapper.toEntity(it) }

                eventDao.insertAllProfileEventCrossRef(roomAttendances)
                emitAll(eventDao.hasUserParticipated(eventId,profileId).map { count->count>0 })

            }catch (e:Exception){
                Log.e(TAG,"Attendances katılımı firestore'dan gelemedi",e)
                emitAll(eventDao.hasUserParticipated(eventId,profileId).map { count->count>0 })
            }
        }else{
            emitAll(eventDao.hasUserParticipated(eventId,profileId).map { count->count>0 })
        }

    }

    fun getEventWithParticipants(eventId:String):Flow<EventWithParticipants?> = flow{
        if (NetworkUtils.isNetworkAvailable(context)){
            try {
                val snapshot = attendancesCollection.get().await()
                val firebaseAttendances = snapshot.documents.mapNotNull { it.toObject(FirebaseAttendance::class.java) }
                val roomAttendances = firebaseAttendances.map { AttendanceMapper.toEntity(it) }

                eventDao.insertAllProfileEventCrossRef(roomAttendances)
                emitAll(eventDao.getEventWithParticipantsById(eventId))

            }catch (e:Exception){
                Log.e(TAG,"Attendances firestore'dan gelemedi",e)
                emitAll(eventDao.getEventWithParticipantsById(eventId))
            }
        }else{
            emitAll(eventDao.getEventWithParticipantsById(eventId))
        }
    }

    companion object
    {
        private const val TAG = "EventRepository"
    }
}