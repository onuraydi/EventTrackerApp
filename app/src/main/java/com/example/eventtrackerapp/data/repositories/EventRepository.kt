package com.example.eventtrackerapp.data.repositories

import com.example.eventtrackerapp.data.mappers.EventMapper
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.data.source.local.ProfileDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao:EventDao,
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao,
    private val profileDao: ProfileDao,
    private val firebaseDatabase: FirebaseDatabase
) {
    private val eventsRef = firebaseDatabase.getReference("events")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        syncEventFromFirebase()
    }

    fun getAllEventFromLocal(): Flow<List<Event>>{
        return eventDao.getAll()
    }

    fun getEventByIdFromLocal(eventId:String):Flow<Event>
    {
        return eventDao.getById(eventId)
    }

    fun getEventsByCategoryIdFromLocal(categoryId:String):Flow<List<Event>>{
        return eventDao.getEventsByCategoryId(categoryId)
    }

    fun getEventsByTagIdFromLocal(tagId:String):Flow<List<Event>>{
        return eventDao.getEventsByTagId(tagId)
    }

    suspend fun getAllEventOnce():List<Event>
    {
        return eventDao.getAllEventOnce()
    }

    suspend fun getEventByOwnerIdOnce(ownerId:String):List<Event>
    {
        return eventDao.getEventsByOwnerIdOnce(ownerId)
    }

    suspend fun saveEvent(event: Event){
        val finalEvent = if(event.id.isEmpty()) {
            val newRef = eventsRef.push()
            event.copy(id = newRef.key ?: throw IllegalStateException("Firebase event Id ccould not be generated"))
        } else {
            event
        }

        eventDao.add(finalEvent)

        val firebaseEvent = EventMapper.toFirebaseModel(finalEvent)
        eventsRef.child(firebaseEvent.id).setValue(firebaseEvent).await()
    }

    private fun syncEventFromFirebase(){
        eventsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val firebaseEvents = mutableListOf<FirebaseEvent>()
                    for(childSnapShot in snapshot.children){
                        val firebaseEvent = childSnapShot.getValue(FirebaseEvent::class.java)?.apply {
                            id = childSnapShot.key ?: ""
                        }
                        firebaseEvent?.let { firebaseEvents.add(it) }
                    }
                    val allCategories = categoryDao.getAllCategoriesOnce()
                    val allTags = tagDao.getAllTagsOnce()
                    val allProfiles = profileDao.getAll()

                    val eventToSave = mutableListOf<Event>()

                    for (firebaseEvent in firebaseEvents) {
                        val eventEntity = EventMapper.toEntity(
                            firebaseEvent,
//                            allCategories,
//                            allTags,
//                            allProfiles,
                        )

                        eventToSave.add(eventEntity)
                    }
                    eventDao.insertAllEvents(eventToSave)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO HATA YÖNETİMİ GELECEK
            }
        })
    }
}