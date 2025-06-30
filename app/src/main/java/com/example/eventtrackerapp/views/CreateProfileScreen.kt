package com.example.eventtrackerapp.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.data.source.local.UserPreferences
import com.example.eventtrackerapp.model.Tag
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateProfileScreen(
    tagViewModel: TagViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    userPreferences: UserPreferences
) {
    /*LaunchedEffect(Unit) {
        tagViewModel.resetTag()
    }*/
    val categoryWithTags by categoryViewModel.categoryWithTags.collectAsStateWithLifecycle()
    val selectedTag by tagViewModel.selectedTag.collectAsStateWithLifecycle()
    val chosenTags by tagViewModel.chosenTags.collectAsStateWithLifecycle()

    val selectedCompleteTagList = remember{ mutableStateListOf<Tag?>(null) }
    val isShow = rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    EventTrackerAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Complete Your Profile", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    navigationIcon = {
                        Icon(Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.padding(start = 8.dp),
                            tint = Color.White)

                        println("asdasd")
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                val fullNameState = rememberSaveable { mutableStateOf("") }
                val userNameState = rememberSaveable { mutableStateOf("") }
                val gender = rememberSaveable { mutableStateOf("") }
                val isExpanded = rememberSaveable { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .padding(bottom = 80.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.padding(vertical = 15.dp))

                    Box(
                        Modifier
                            .size(80.dp, 80.dp)
                            .border(border = BorderStroke(2.dp, Color.Black), shape = CircleShape)
                            .clickable {
                            }
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                                .align(Alignment.Center)
                                .padding(start = 5.dp),
                            painter = painterResource(R.drawable.profile_photo_add_icon),
                            contentDescription = "PhotoAdd",
                            )
                    }

                    Spacer(Modifier.padding(vertical = 5.dp))

                    Text(
                        text = "Add Profile Photo",
                    )

                    Spacer(Modifier.padding(vertical = 7.dp))

                    EventTrackerAppOutlinedTextField(
                        "Full Name",
                        fullNameState
                    )

                    Spacer(Modifier.padding(vertical = 12.dp))

                    EventTrackerAppOutlinedTextField(
                        "Username",
                        userNameState
                    )
                    Spacer(Modifier.padding(vertical = 12.dp))

                    ExposedDropdownMenuBox(
                        expanded = isExpanded.value,
                        onExpandedChange = {
                            isExpanded.value = it
                        }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(),
                            value = gender.value,
                            onValueChange = {},
                            placeholder = { Text("Gender") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                            },
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded.value,
                            onDismissRequest = { isExpanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Male") },
                                onClick = {
                                    gender.value = "Male"
                                    isExpanded.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Female") },
                                onClick = {
                                    gender.value = "Female"
                                    isExpanded.value = false
                                }
                            )
                        }
                    }

                    Spacer(Modifier.padding(vertical = 18.dp))

                    Text(
                        "Please select category",
                        modifier = Modifier.fillMaxWidth(0.9f),
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.padding(vertical = 3.dp))

                    //TODO BU KATEGORİ ALANINDA SELECTED NASIL HALLOLUR
                    //TODO HATA: Kullanıcı kategoriyi seçtikten sonra etiketleri seçmeden başka kategori seçebiliyor
                    LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                        items(categoryWithTags) {
                            val selected = rememberSaveable { mutableStateOf(false) }

                            FilterChip(
                                modifier = Modifier.padding(end = 8.dp),
                                selected = selected.value,
                                label = { Text(it.category.name?:"") },
                                onClick = {
                                    selected.value = !selected.value
                                    isShow.value = selected.value

                                    if(selected.value){
                                        tagViewModel.updateSelectedCategoryTags(it) //selectedTags ı doldurur
                                    }else{
                                        // Kategori kaldırıldı → hem chosenTags hem selectedCompleteTagList içinden temizle
                                        tagViewModel.resetChosenTagForCategory(it.category.id)

                                        // Compose'daki selectedCompleteTagList içinden bu kategoriye ait tag’leri çıkar
                                        selectedCompleteTagList.removeAll{tag-> tag?.categoryId == it.category.id }
                                    }
                                },
                                trailingIcon = if (selected.value) {
                                    {
                                        Icon(
                                            Icons.Filled.Done,
                                            "Done",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                }

                            )
                        }
                    }

                    /*Spacer(Modifier.padding(vertical = 5.dp))
                    Box(
                        Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                            .defaultMinSize(minHeight = 80.dp)
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                    ) {
                        FlowRow(
                            Modifier.padding(5.dp),
                            maxItemsInEachRow = 4,
                        ) {
                            selectedTag.forEach {tag->
                                val isSelected = chosenTags.any { it.id == tag.id }

                                FilterChip(
                                    modifier = Modifier.padding(end = 3.dp),
                                    label = { Text(tag.name?:"", fontSize = 12.sp, maxLines = 1) },
                                    selected = isSelected,
                                    onClick = {
                                        if(isSelected){
                                            tagViewModel.toggleTag(tag)
                                            selectedCompleteTagList.remove(tag)
                                        }else{
                                            selectedCompleteTagList.add(tag)
                                        }
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.Clear, "Clear")
                                    }
                                )
                            }
                        }
                    }*/

                    Spacer(Modifier.padding(vertical = 12.dp))

                    if(isShow.value){
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(selectedTag) {tag->

                                val isSelected = chosenTags.any { it.id == tag.id }

                                FilterChip(
                                    modifier = Modifier.padding(end = 8.dp),
                                    selected = isSelected,
                                    label = { Text(tag.name ?: "") },
                                    onClick = {
                                        tagViewModel.toggleTag(tag)
                                        if(!selectedCompleteTagList.any{it?.id == tag.id}){
                                            selectedCompleteTagList.add(tag)
                                        }else if(isSelected){
                                            selectedCompleteTagList.removeAll{it?.id == tag.id}
                                        }
                                    },
                                    trailingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                Icons.Filled.Done,
                                                "Done",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else {
                                        null
                                    }

                                )
                            }
                        }
                    }

                    Spacer(Modifier.padding(vertical = 5.dp))

                    Box(
                        Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                            .defaultMinSize(minHeight = 80.dp)
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                    ) {
                        FlowRow(
                            Modifier.padding(5.dp),
                            maxItemsInEachRow = 4,
                        ) {
                            selectedCompleteTagList.filterNotNull().forEach {tag->
                                FilterChip(
                                    modifier = Modifier.padding(end = 3.dp),
                                    label = { Text(tag.name?:"", fontSize = 12.sp, maxLines = 1) },
                                    selected = true,
                                    onClick = {
                                        tagViewModel.removeChosenTag(tag)
                                        selectedCompleteTagList.remove(tag)
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.Clear, "Clear")
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.padding(vertical = 20.dp))

                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 15.dp)
                ) {
                    EventTrackerAppPrimaryButton("Complete") {
                        scope.launch {
                            userPreferences.setIsProfileCompleted(value = true)
                            // onclick 
                        }
                    }
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    EventTrackerAppTheme {
//        CreateProfileScreen()
    }
}