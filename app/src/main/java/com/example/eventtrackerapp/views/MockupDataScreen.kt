package com.example.eventtrackerapp.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventtrackerapp.model.firebasemodels.FirebaseCategory
import com.example.eventtrackerapp.model.firebasemodels.FirebaseEvent
import com.example.eventtrackerapp.model.firebasemodels.FirebaseTag
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.Tag
import java.util.UUID

@Composable
fun MockupDataScreen(
    onCategorySubmit:(Category)->Unit,
    onTagSubmit: (tag: Tag)->Unit
){
    var categoryId by remember{mutableStateOf("")}
    var categoryName by remember{ mutableStateOf("") }

    var tagName by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Kategori Ekle", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = categoryId,
            onValueChange = { categoryId = it },
            label = { Text("Kategori ID") },
            singleLine = true
        )
        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Kategori Adı") },
            singleLine = true
        )

        Button(onClick = {
            if(categoryName.isNotBlank()&&categoryId.isNotBlank()){
                onCategorySubmit(Category(id = categoryId, name = categoryName))
            }
        }) {
            Text("Kategori Ekle")
        }

        HorizontalDivider(thickness = 2.dp)

        Text("Etiket Ekle", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = tagName,
            onValueChange = { tagName = it },
            label = { Text("Etiket Adı") },
            singleLine = true
        )

        Button(onClick = {
            if(tagName.isNotBlank()){
                val generatedTagId = UUID.randomUUID().toString()
                onTagSubmit(Tag(id = generatedTagId, name = tagName, categoryId = categoryId))
                tagName = ""
            }
        }) {
            Text("Etiket Ekle")
        }

    }
}