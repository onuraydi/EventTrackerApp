package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.CategoryMapper
import com.example.eventtrackerapp.data.mappers.ProfileMapper
import com.example.eventtrackerapp.data.mappers.TagMapper
import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.firebasemodels.FirebaseProfile
import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.Profile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class ProfileRepository(
    private val firestore: FirebaseFirestore,
) {
    private val profilesCollection = firestore.collection("profiles")
    private val categoryCollection = firestore.collection("categories")
    private val tagCollection = firestore.collection("tags")

    fun getProfile(profileId: String): Flow<Profile?> = flow {
        try {
            val profileSnapshot = profilesCollection.document(profileId).get().await()
            val categorySnapshot = categoryCollection.get().await()
            val tagSnapshot = tagCollection.get().await()

            val firebaseCategories = categorySnapshot.documents.mapNotNull { it.toObject(FirebaseCategory::class.java) }
            val firebaseTags = tagSnapshot.documents.mapNotNull { it.toObject(FirebaseTag::class.java) }
            val firebaseProfile = profileSnapshot.toObject(FirebaseProfile::class.java)

            val categories = firebaseCategories.map { CategoryMapper.toEntity(it) }
            val tags = firebaseTags.map { TagMapper.toEntity(it) }
            val profile = firebaseProfile?.let { ProfileMapper.toEntity(firebaseProfile,tags,categories) }

            emit(profile)
        }catch (e:Exception){
            Log.e(TAG,"Profil firestore'dan gelemedi")
        }
    }

    fun getAllProfiles():Flow<List<Profile>> = flow {
        try {
            val profileSnapshot = profilesCollection.get().await()
            val categorySnapshot = categoryCollection.get().await()
            val firebaseCategories = categorySnapshot.documents.mapNotNull { it.toObject(
                FirebaseCategory::class.java) }
            val allCategories = firebaseCategories.map { CategoryMapper.toEntity(it) }

            val tagSnapShot = tagCollection.get().await()
            val firebaseTags = tagSnapShot.documents.mapNotNull { it.toObject(FirebaseTag::class.java) }
            val allTags = firebaseTags.map { TagMapper.toEntity(it) }

            val firebaseProfiles = profileSnapshot.documents.mapNotNull { it.toObject(FirebaseProfile::class.java) }
            val profileList = firebaseProfiles.map { ProfileMapper.toEntity(it,allTags,allCategories) }

            emit(profileList)
        }catch (e:Exception){
            Log.e(TAG,"Profiller getirelemedi")
        }
    }


    fun upsertProfile(profile:Profile){

        //Sonra Firestore'a kaydet. Kaydetmeden önce firebase modele çevir(Category ve Tag listelerinin id'lerini al fb modele geçirir -> mapper)
        val firebaseProfile = ProfileMapper.toFirebaseModel(profile)
        profilesCollection.document(firebaseProfile.id).set(firebaseProfile)
            .addOnSuccessListener { Log.d(TAG,"Profile successfully upserted to Firestore") }
            .addOnFailureListener { e-> Log.w(TAG,"Error upserting profile from Firestore",e) }

    }

    fun deleteProfile(profile: Profile){
        //Sonra Firestore'dan sil
        profilesCollection.document(profile.id).delete()
            .addOnSuccessListener { Log.d(TAG,"Profile successfull deleted from Firestore") }
            .addOnFailureListener { e-> Log.w(TAG,"Error deleting profile from Firestore") }
    }

    companion object{
        private const val TAG = "ProfileRepository"
    }
}