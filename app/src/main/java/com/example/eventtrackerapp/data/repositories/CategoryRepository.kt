package com.example.eventtrackerapp.data.repositories

import android.util.Log
import com.example.eventtrackerapp.data.mappers.CategoryMapper
import com.example.eventtrackerapp.data.mappers.TagMapper
import com.example.eventtrackerapp.data.source.local.CategoryDao
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import com.example.eventtrackerapp.model.roommodels.Tag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Dispatcher


class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao,
    private val firestore: FirebaseFirestore
) {
    private val categoryCollection = firestore.collection("categories")
    private val tagCollection = firestore.collection("tags")


//    init {
//        listenForFirestoreCategories()
//        listenForFirestoreTags()
//    }

    suspend fun initializeCategoriesAndTags()
    {
        Log.d(Companion.TAG, "Initializing categories and tags from Firestore...")
        try
        {
            val categorySnapshot = categoryCollection.get().await()
            val firebaseCategories = categorySnapshot.documents.mapNotNull { it.toObject(FirebaseCategory::class.java) }
            val roomCategories = firebaseCategories.map { CategoryMapper.toEntity(it) }
            categoryDao.insertAllCategory(roomCategories)
            Log.d(Companion.TAG,"Categories initialized: ${roomCategories.size}")

            val tagSnapshot = tagCollection.get().await()
            val firebaseTags = tagSnapshot.documents.mapNotNull { it.toObject(FirebaseTag::class.java) }
            val roomTags = firebaseTags.map { TagMapper.toEntity(it) }
            tagDao.insertAllTags(roomTags)
            Log.d(Companion.TAG,"Tag initialized: ${roomTags.size}")
        }
        catch (e:Exception)
        {
            Log.e(Companion.TAG,"Error initializing categories and tags",e)
        }
    }

    fun getCategoriesWithTags(): Flow<List<CategoryWithTag>>
    {
        return categoryDao.getCategoryWithTags()
    }

    fun getCategoryWithTagsByCategoryId(categoryId:String):Flow<CategoryWithTag?>
    {
        return categoryDao.getCategoryWithTagsById(categoryId)
    }

    suspend fun addCategory(id:String,name:String)
    {
        val roomCategory = Category(id = id, name = name)
        val firebaseCategory = FirebaseCategory(id = id, name = name)

        try
        {
            categoryDao.insertCategory(roomCategory)
            categoryCollection.document(id).set(firebaseCategory).await()
            Log.d(TAG,"Category '$name' added successfully to room and Firebase")
        } catch (e:Exception){
            Log.e(TAG,"Error adding category '$name'",e)

            //burada hatadan dolayı silme işlemi gerekebilir senkron olma durumunu bozmaması için
        }
    }

    suspend fun addTag(id:String, name:String, categoryId: String)
    {
        val roomTag = Tag(id = id, name= name, categoryId = categoryId)
        val firebaseTag = FirebaseTag(id = id, name = name, categoryId = categoryId)

        try {
            tagDao.insertTag(roomTag)
            tagCollection.document(id).set(firebaseTag).await()
            Log.d(TAG,"Tag '$name' for category '$categoryId' added successfully to room and firebase")
        } catch (e:Exception){
            Log.e(TAG,"Error adding tag '$name' to category '$categoryId'")
            // silme işlemi gerekebilir
        }
    }


    fun listenForFirestoreCategories()
    {
        categoryCollection.addSnapshotListener {snapshot,e ->
            if (e != null)
            {
                Log.e(TAG,"Category listen failed",e)
                return@addSnapshotListener
            }
            if (snapshot != null)
            {
                CoroutineScope(Dispatchers.IO).launch {
                    val firebaseCategories = snapshot.documents.mapNotNull { it.toObject(FirebaseCategory::class.java) }
                    val roomCategoriesToInsertOrUpdate = firebaseCategories.map { CategoryMapper.toEntity(it) }
                    categoryDao.insertAllCategory(roomCategoriesToInsertOrUpdate)

                    Log.d(TAG, "Categories synchronized. Total: ${firebaseCategories.size}")
                }
            }
        }
    }

    fun listenForFirestoreTags()
    {
        tagCollection.addSnapshotListener {snapshot, e ->
            if(e != null)
            {
                Log.e(TAG,"Tag listen failed",e)
                return@addSnapshotListener
            }
            if (snapshot != null)
            {
                CoroutineScope(Dispatchers.IO).launch {
                    val firebaseTags = snapshot.documents.mapNotNull { it.toObject(FirebaseTag::class.java) }
                    val roomTagsToInsertOrUpdate = firebaseTags.map { TagMapper.toEntity(it) }
                    tagDao.insertAllTags(roomTagsToInsertOrUpdate)

                    Log.d(TAG,"Tags synchronized. Total: ${firebaseTags.size}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "CategoryRepository"
    }
}

