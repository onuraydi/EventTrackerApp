package com.example.eventtrackerapp.data.repositories

import android.content.Context
import android.util.Log
import androidx.compose.ui.window.Popup
import com.example.eventtrackerapp.common.NetworkUtils
import com.example.eventtrackerapp.data.mappers.LikeMapper
import com.example.eventtrackerapp.data.mappers.ProfileMapper
import com.example.eventtrackerapp.data.repositories.LikeRepository.Companion
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.EventDao
import com.example.eventtrackerapp.data.source.local.ProfileDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseLike
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
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
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    private val profilesCollection = firestore.collection("profiles")

    fun getProfile(profileId: String): Flow<Profile?> {
        return profileDao.getById(profileId)
            .onStart {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    try {
                        // Gerekli referans verilerini al
                        val allCategories = categoryDao.getAll().first()
                        val allTags = tagDao.getAll()

                        // Firebase'den tüm profilleri çek (tek profil varsa query ile tek alabilirsin)
                        val snapshot = profilesCollection.get().await()
                        val firebaseProfiles = snapshot.documents.mapNotNull {
                            it.toObject(FirebaseProfile::class.java)
                        }

                        val roomProfiles = firebaseProfiles.map {
                            ProfileMapper.toEntity(it, allTags, allCategories)
                        }

                        profileDao.insertAllProfiles(roomProfiles)

                        Log.d("ProfileRepo", "Firebase'den profil alındı ve Room'a yazıldı.")
                    } catch (e: Exception) {
                        Log.e("ProfileRepo", "Firebase'den veri alınamadı. Room verisi kullanılacak.", e)
                    }
                } else {
                    Log.w("ProfileRepo", "İnternet yok, Room verisi gösterilecek.")
                }
            }
    }

    fun getAllProfiles():Flow<List<Profile>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)){
            try {
                val allCategories = categoryDao.getAll().first() //flow olduğu için first
                val allTags = tagDao.getAll()

                val profileSnapshot = profilesCollection.get().await()
                val firebaseProfile = profileSnapshot.documents.mapNotNull { it.toObject(FirebaseProfile::class.java) }
                val roomProfile = firebaseProfile.map { ProfileMapper.toEntity(it, allTags, allCategories) }

                profileDao.insertAllProfiles(roomProfile)
                profileDao.getAll()
                emit(profileDao.getAll().first())
            }
            catch (e:Exception){
                Log.e(TAG,"Etkinlik firestore'dan çekilemedi, roomdan geliyor",e)
                emit(profileDao.getAll().first())
            }
        }
        else{
            emit(profileDao.getAll().first())
        }
    }


    suspend fun upsertProfile(profile:Profile){
        //Room'a kaydet

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