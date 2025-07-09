package com.example.eventtrackerapp.data.repositories

import com.example.eventtrackerapp.data.mappers.TagMapper
import com.example.eventtrackerapp.data.source.local.TagDao
import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.Tag
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
class TagRepository @Inject constructor(
    private val tagDao: TagDao,
    private val firebaseDatabase: FirebaseDatabase
){
    private val tagsRef = firebaseDatabase.getReference("tags")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        syncTagsFromFirebase()
    }

    fun getAllTagsFromLocal():Flow<List<Tag>>
    {
        return tagDao.getAll()
    }

    fun getTagByIdFromLocal(tagId:String):Flow<Tag>
    {
        return tagDao.getById(tagId)
    }

    fun getTagsForCategoryFromLocal(categoryId:String):Flow<List<Tag>> {
        return tagDao.getTagsByCategory(categoryId)
    }

    suspend fun getAllTagsOnce():List<Tag>{
        return tagDao.getAllTagsOnce()
    }

    suspend fun getTagForCategoryOnce(categoryId: String):List<Tag>{
        return tagDao.getTagForCategoryOnce(categoryId)
    }

    suspend fun saveTag(tag: Tag) {
        val finalTag = if (tag.id.isEmpty()) {
            val newRef = tagsRef.push()
            tag.copy(id = newRef.key ?: throw IllegalStateException("Firebase Tag ID could not be generated"))
        } else {
            tag
        }

        tagDao.insert(finalTag)
        val firebaseTag = TagMapper.toFirebaseModel(finalTag)
        tagsRef.child(firebaseTag.id).setValue(firebaseTag).await()
    }


    private fun syncTagsFromFirebase() {
        tagsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val firebaseTags = mutableListOf<FirebaseTag>()
                    for (childSnapshot in snapshot.children) {
                        val firebaseTag = childSnapshot.getValue(FirebaseTag::class.java)?.apply {
                            id = childSnapshot.key ?: ""
                        }
                        firebaseTag?.let { firebaseTags.add(it) }
                    }
                    tagDao.insertAll(TagMapper.toEntityList(firebaseTags))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // TODO Hata yönetimi
            }
        })
    }
}