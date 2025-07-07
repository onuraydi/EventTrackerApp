package com.example.eventtrackerapp.data.repositories

import com.example.eventtrackerapp.data.mappers.CategoryMapper
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.roommodels.Category
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
class CategoryRepository @Inject constructor(
    private val categoryDao:CategoryDao,
    private val firebaseDatabase: FirebaseDatabase
) {
    private val categoriesRef = firebaseDatabase.getReference("categories")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        // senkronizasyonu başlatıyoruz.
        syncCategoriesFromFirebase()
    }


    fun getAllCategoriesFromLocal(): Flow<List<Category>>{
        return categoryDao.getAll()
    }

    fun getCategoryByIdFromLocal(categoryId: String):Flow<Category> {
        return categoryDao.getById(categoryId)
    }

    // Tek seferlik room'dan alma metodu
    suspend fun getAllCategoriesOnce(): List<Category> {
        return categoryDao.getAllCategoriesOnce()
    }

    suspend fun saveCategory(category: Category)
    {
        val finalCategory = if(category.id.isEmpty()){
            val newRef = categoriesRef.push()
            category.copy(id = newRef.key ?: throw IllegalStateException("Firebase Category ID could not be generated"))
        }else{
            category
        }

        categoryDao.insert(finalCategory) // rooma kaydeder
        val firebaseCategory = CategoryMapper.toFirebaseModel(finalCategory)
        categoriesRef.child(firebaseCategory.id).setValue(firebaseCategory).await() // firebase' e kaydet
    }

    private fun syncCategoriesFromFirebase(){

        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val firebaseCategories = mutableListOf<FirebaseCategory>()
                    for(childSnapShot in snapshot.children) {
                        val firebaseCategory = childSnapShot.getValue(FirebaseCategory::class.java)?.apply {
                            id = childSnapShot.key ?: ""
                        }
                        firebaseCategory?.let { firebaseCategories.add(it) }
                    }
                    categoryDao.insertAllCategories(CategoryMapper.toEntityList(firebaseCategories))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO HATA yönetimi
            }
        })
    }

}

