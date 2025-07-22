package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.Tag
import com.example.eventtrackerapp.model.firebasemodels.FirebaseProfile

object ProfileMapper {
    fun toEntity(firebaseProfile: FirebaseProfile, allTag:List<Tag>, allCategory:List<Category>): Profile
    {
        val selectedTags = allTag.filter { firebaseProfile.selectedTagIds.contains(it.id) }
        val selectedCategories = allCategory.filter{ firebaseProfile.selectedCategoryIds.contains(it.id) }

        return Profile(
            id = firebaseProfile.id,
            email = firebaseProfile.email,
            photo = firebaseProfile.profileImageUrl,
            gender = firebaseProfile.gender,
            userName = firebaseProfile.userName,
            fullName = firebaseProfile.fullName,
            selectedTagList = selectedTags,
            selectedCategoryList = selectedCategories,
            addedEventIds = firebaseProfile.addedEventIds
        )
    }

    fun toFirebaseModel(profile: Profile):FirebaseProfile
    {
        return FirebaseProfile(
            id = profile.id,
            fullName = profile.fullName,
            userName = profile.userName,
            email = profile.email,
            gender = profile.gender,
            profileImageUrl = profile.photo,
            selectedTagIds = profile.selectedTagList.map { it.id },
            selectedCategoryIds = profile.selectedCategoryList.map { it.id },
            addedEventIds = profile.addedEventIds
        )
    }
}
