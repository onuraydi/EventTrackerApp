package com.example.eventtrackerapp.data.repositories

import com.example.eventtrackerapp.data.mappers.ProfileMapper
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.data.source.local.ProfileDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseProfile
import com.example.eventtrackerapp.model.roommodels.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao,
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao,
    private val eventDao: EventDao,
    private val firebaseDatabase: FirebaseDatabase
) {

    // TODO Güncelleme işlemi de yazmak gerekebilir

    private val profilesRef = firebaseDatabase.getReference("profiles")
    private val userEventsRef = firebaseDatabase.getReference("userEvents")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        syncProfileFromFirebase()
        syncUserEventsFromFirebase()
    }


    fun getAllProfilesFromLocal(): Flow<List<Profile>>
    {
        return profileDao.getAll()
    }

    fun getProfileByIdFromLocal(profileId:String):Flow<Profile>
    {
        return profileDao.getById(profileId)
    }

    suspend fun getAllProfilesOnce():List<Profile>
    {
        return profileDao.getAllProfilesOnce()
    }

    suspend fun getProfileByIdOnce(profileId:String):Profile
    {
        return profileDao.getProfileByIdOnce(profileId)
    }

    suspend fun saveProfile(profile: Profile){
        val finalProfile = if (profile.id.isEmpty()){
            val newRef = profilesRef.push()
            profile.copy(id = newRef.key ?: throw IllegalStateException("Firebase Profile ID could not be generated"))
        }else{
            profile
        }

        profileDao.add(profile)

        val firebaseProfile = ProfileMapper.toFirebaseModel(profile)
        profilesRef.child(firebaseProfile.id).setValue(firebaseProfile).await()


        if (finalProfile.addedEventIds.isNotEmpty()) {
            val eventIdsMap = finalProfile.addedEventIds.associateWith { true }
            userEventsRef.child(finalProfile.id).setValue(eventIdsMap).await()
        }else{
            userEventsRef.child(finalProfile.id).removeValue().await()
        }
    }


    private fun syncProfileFromFirebase(){
        profilesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val firebaseProfiles = mutableListOf<FirebaseProfile>()
                    for (childSnapShot in snapshot.children) {
                        val firebaseProfile = childSnapShot.getValue(FirebaseProfile::class.java)?.apply {
                            id = childSnapShot.key ?: ""
                        }
                        firebaseProfile?.let { firebaseProfiles.add(it) }
                    }

                    val allCategories = categoryDao.getAllCategoriesOnce()
                    val allTags = tagDao.getAllTagsOnce()
                    val allEvents = eventDao.getAllEventOnce()

                    val profilesToSave = mutableListOf<Profile>()

                    for(firebaseProfile in firebaseProfiles){
                        val profile = ProfileMapper.toEntity(
                            firebaseProfile,
                            allCategories,
                            allTags,
                            allEvents
                        )

                        val userEventSnapshot = userEventsRef.child(profile.id).get().await()
                        val addedEventIds: List<String> = if (userEventSnapshot.exists() && userEventSnapshot.value is Map<*, *>) {
                            (userEventSnapshot.value as Map<*, *>)
                                .filterKeys { it is String }          // Anahtarın String olduğundan emin ol
                                .filterValues { it is Boolean }       // Değerin Boolean olduğundan emin ol
                                .mapKeys { it.key as String }         // String olarak döküm (cast) yap
                                .mapValues { it.value as Boolean }    // Boolean olarak döküm (cast) yap
                                .keys.toList()                        // Sadece anahtarlarını liste olarak al
                        } else {
                            emptyList() // Eğer düğüm yoksa veya beklenen Map formatında değilse boş liste döndür
                        }


                        val finalProfile = profile.copy(addedEventIds = addedEventIds)
                        profilesToSave.add(finalProfile)
                    }
                    profileDao.insertAllProfiles(profilesToSave)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO HATA YÖNETİMİ
            }
        })
    }

    private fun syncUserEventsFromFirebase(){
        userEventsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    for (profileIdSnapshot in snapshot.children) {
                        val profileId = profileIdSnapshot.key ?: continue
                        val addedEventIds: List<String> = if(profileIdSnapshot.exists() && profileIdSnapshot.value is Map<*, *>) {
                            (profileIdSnapshot.value as Map<*,*>)
                                .filterKeys { it is String }
                                .filterValues { it is Boolean }
                                .mapKeys { it.key as String }
                                .mapValues { it.value as Boolean }
                                .keys.toList()
                        }else{
                            emptyList()
                        }

                        val existingProfiles = profileDao.getProfileByIdOnce(profileId)
                        existingProfiles?.let {
                            val updatedProfile = it.copy(addedEventIds = addedEventIds)
                            profileDao.add(updatedProfile)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // TODO HAYA YÖNETİMİ
            }
        })
    }
}