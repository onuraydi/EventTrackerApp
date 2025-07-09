package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseProfile
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.Tag

object ProfileMapper {
    fun toEntity(firebaseProfile: FirebaseProfile,allCategories:List<Category>,allTags:List<Tag>,allEvents:List<Event>):Profile{

        val selectedCategories = firebaseProfile.selectedCategoryIds.keys.mapNotNull { categoryId ->
            allCategories.find { it.id == categoryId }
        }

        val selectedTags = firebaseProfile.selectedTagIds.keys.mapNotNull { tagId ->
            allTags.find { it.id == tagId }
        }

        val addedEventIds = emptyList<String>()

        return Profile(
            id = firebaseProfile.id,
            email = firebaseProfile.email,
            photo = firebaseProfile.profileImageUrl,
            gender = firebaseProfile.gender,
            userName = firebaseProfile.userName,
            fullName = firebaseProfile.fullName,
            selectedCategoryList = selectedCategories,
            selectedTagList = selectedTags,
            addedEventIds = addedEventIds
        )
    }

    fun toFirebaseModel(profile: Profile):FirebaseProfile{
        val selectedCategoryIdsMap = profile.selectedCategoryList.associate { it.id to true }
        val selectedTagIdsMap = profile.selectedTagList.associate { it.id to true }

        return FirebaseProfile(
            id = profile.id,
            fullName = profile.fullName,
            userName = profile.userName,
            email = profile.email,
            gender = profile.gender,
            profileImageUrl = profile.photo,
            selectedCategoryIds = selectedCategoryIdsMap,
            selectedTagIds = selectedTagIdsMap,

        )
    }

    fun toEntityList(firebaseProfile: List<FirebaseProfile>,allCategories:List<Category>,allTags:List<Tag>,allEvents:List<Event>):List<Profile>{
        return firebaseProfile.map { toEntity(it, allCategories, allTags, allEvents) }
    }
}
