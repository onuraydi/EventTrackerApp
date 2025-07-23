package com.example.eventtrackerapp.data.repositories

import android.util.Log
import androidx.compose.ui.window.Popup
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository(
    private val profileDao: ProfileDao,
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao,
    private val firestore: FirebaseFirestore
) {
    private val profilesCollection = firestore.collection("profiles")

    fun getProfile(profileId:String):Flow<Profile?>{
        return profileDao.getById(profileId)
    }

    fun getAllProfiles():Flow<List<Profile>>{
        return profileDao.getAll()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun listenForFirestoreProfileChanges(profileId:String){
        profilesCollection.document(profileId)
            .addSnapshotListener{snapshot, e->
                if(e!=null){
                    Log.w(TAG,"Listen failed for profile $profileId",e)
                    return@addSnapshotListener
                }

                if(snapshot!=null && snapshot.exists()){
                    val firebaseProfile = snapshot.toObject(FirebaseProfile::class.java)
                    GlobalScope.launch(Dispatchers.IO) {
                        firebaseProfile?.let{fbProfile->
                            Log.d("profil","Snapshot geliyo: ${snapshot.data}")
                            //firestore'dan gelen ID'lerle eşleştirmek için tüm kategori ve tag'leri çek
                            val allCategories = categoryDao.getAll().first() //flow olduğu için first
                            val allTags = tagDao.getAll()

                            val roomProfile = ProfileMapper.toEntity(fbProfile,allTags,allCategories)
                            profileDao.add(roomProfile)
                        }
                    }
                }else if(snapshot!=null && !snapshot.exists()){
                    GlobalScope.launch(Dispatchers.IO){
                        profileDao.delete(Profile(id = profileId))
                    }
                }
            }
    }

    fun listenForAllFirestoreProfiles(){
        profilesCollection.addSnapshotListener{snapshot, e->
            if(e!=null){
                Log.w(TAG,"Listen failed for all profiles",e)
                return@addSnapshotListener
            }

            if(snapshot!=null){
                val firebaseProfiles = snapshot.documents.mapNotNull { it.toObject(FirebaseProfile::class.java) }

                GlobalScope.launch(Dispatchers.IO){
                    val currentRoomProfiles = profileDao.getAll().first()
                    val currentRoomProfileIds = currentRoomProfiles.map { it.id }.toSet()
                    val firebaseProfileIds = firebaseProfiles.map { it.id }.toSet()

                    //Firestore'dan gelen tüm kategori ve tagleri bir kez çek
                    val allCategories = categoryDao.getAll().first()
                    val allTags = tagDao.getAll()

                    //Firestore'dan gelenleri Room'a ekle/güncelle
                    firebaseProfiles.forEach { fbProfile->
                        val roomProfile = ProfileMapper.toEntity(fbProfile,allTags,allCategories)
                        profileDao.add(roomProfile)
                    }

                    //Firestore'da silinenleri Room'dan sil
                    val deletedProfileIds = currentRoomProfileIds.minus(firebaseProfileIds)
                    deletedProfileIds.forEach{profileId->
                        profileDao.delete(Profile(id = profileId))
                    }
                }
            }
        }
    }

    suspend fun upsertProfile(profile:Profile){
        //Room'a kaydet
        //profile.id = UUID.randomUUID().toString()
        profileDao.add(profile)

        //Sonra Firestore'a kaydet. Kaydetmeden önce firebase modele çevir(Category ve Tag listelerinin id'lerini al fb modele geçirir -> mapper)
        val firebaseProfile = ProfileMapper.toFirebaseModel(profile)
        profilesCollection.document(firebaseProfile.id).set(firebaseProfile)
            .addOnSuccessListener { Log.d(TAG,"Profile successfully upserted to Firestore") }
            .addOnFailureListener { e-> Log.w(TAG,"Error upserting profile from Firestore",e) }

    }

    suspend fun deleteProfile(profile: Profile){
        //Room'dan sil
        profileDao.delete(profile)

        //Sonra Firestore'dan sil
        profilesCollection.document(profile.id).delete()
            .addOnSuccessListener { Log.d(TAG,"Profile successfull deleted from Firestore") }
            .addOnFailureListener { e-> Log.w(TAG,"Error deleting profile from Firestore") }
    }

    suspend fun updateAddedEventIds(profileId:String,newAddedEventIds:List<String>){
        //Room'u güncelle
        val profile = profileDao.getById(profileId).first()
        profile?.let {
            val updatedProfile = it.copy(addedEventIds = newAddedEventIds)
            profileDao.add(updatedProfile)
        }

        //Sonra Firestore'u güncelle
        profilesCollection.document(profileId).update("addedEventIds",newAddedEventIds)
            .addOnSuccessListener{Log.d(TAG,"Added event IDs updated in Firestore")}
            .addOnFailureListener { e-> Log.w(TAG,"Error updating added event IDs in Firestore",e) }
    }

    companion object{
        private const val TAG = "ProfileRepository"
    }
}