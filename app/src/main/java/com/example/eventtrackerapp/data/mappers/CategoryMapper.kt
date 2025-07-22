package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.roommodels.Category

object CategoryMapper {
    fun toEntity(firebaseCategory: FirebaseCategory):Category
    {
        return Category(
            id = firebaseCategory.id,
            name = firebaseCategory.name
        )
    }

    fun toFirebaseModel(category: Category):FirebaseCategory
    {
        return FirebaseCategory(
            id = category.id,
            name = category.name
        )
    }

    fun toEntityList(firebaseCategories: List<FirebaseCategory>):List<Category>
    {
        return firebaseCategories.map { toEntity(it) }
    }

    fun toFirebaseModelList(categories: List<Category>):List<FirebaseCategory>
    {
        return categories.map { toFirebaseModel(it) }
    }
}