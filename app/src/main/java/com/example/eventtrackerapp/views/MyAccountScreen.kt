package com.example.eventtrackerapp.views

import android.widget.Space
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MyAccountScreen(){
    Scaffold(modifier = Modifier
        .fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Profilinizi Düzenleyin",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            })
        }
    ){ innnerPadding ->
        Box(modifier = Modifier
            .padding(innnerPadding)
            .fillMaxSize()){
            var fullNameState = rememberSaveable { mutableStateOf("") }
            var userNameState = rememberSaveable { mutableStateOf("") }
            var selected = rememberSaveable { mutableStateOf(false) }
            var gender = rememberSaveable { mutableStateOf("") }
            var isExpanded = rememberSaveable { mutableStateOf(false) }
            var selectedCategoryList = remember { mutableStateListOf<String?>() }
            var profilePhotoState = rememberSaveable { mutableStateOf(R.drawable.profile_photo_add_icon) }
            // Buraya kullanıcının yüklediği profil gelecek

            Column(modifier = Modifier
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState()),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Spacer(modifier = Modifier.padding(vertical = 15.dp))

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
                        painter = painterResource(profilePhotoState.value),
                        contentDescription = "PhotoAdd",
                    )
                }

                Spacer(Modifier.padding(vertical = 5.dp))

                Text(
                    text = "Update Profile Photo",
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {  }
                )

                Spacer(Modifier.padding(vertical = 7.dp))


                EventTrackerAppOutlinedTextField(txt="Full Name",fullNameState)

                Spacer(modifier = Modifier.padding(vertical = 12.dp))

                EventTrackerAppOutlinedTextField(txt = "Username",userNameState)

                Spacer(Modifier.padding(vertical = 12.dp))


                ExposedDropdownMenuBox(
                    expanded = isExpanded.value,
                    onExpandedChange = {isExpanded.value = it}
                ) {
                    OutlinedTextField(modifier = Modifier
                        .menuAnchor(),
                        value = gender.value,
                        onValueChange = {},
                        placeholder = { Text("Gender")},
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
                            text = { Text("Male")},
                            onClick = {
                                gender.value = "Male"
                                isExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Female")},
                            onClick = {
                                gender.value = "Female"
                                isExpanded.value = false
                            }
                        )
                    }
                }
                Spacer(Modifier.padding(vertical = 18.dp))

                Text("Please select category",
                    modifier = Modifier.fillMaxWidth(0.9f),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.padding(vertical = 3.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)) {
                    val categories = arrayListOf(
                        "Yazılım",
                        "Yapay Zeka",
                        "Back-End",
                        "Front-End",
                        "Teknoloji",
                        "Örnek",
                        "Örnek2",
                        "Örnek3",
                        "Örnek4"
                    )
                    items(categories){
                        FilterChip(
                            modifier = Modifier.padding(end = 8.dp),
                            selected = selected.value,
                            label = { Text(text = it)},
                            onClick = {
                                selected.value = !selected.value
                                if(!selectedCategoryList.contains(it)){
                                    selectedCategoryList.add(it)
                                }else{
                                    selectedCategoryList.remove(it)
                                }
                            },
                            trailingIcon = if(selected.value){
                                {
                                    Icon(
                                        Icons.Filled.Done,null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            }
                            else {
                                null
                            }
                        )
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
                ){
                        FlowRow(
                            modifier = Modifier.padding(5.dp),
                            maxItemsInEachRow = 4,
                        ) {
                            selectedCategoryList.filterNotNull().forEach{
                                FilterChip(
                                    modifier = Modifier.padding(end = 3.dp),
                                    label = { Text(text = it, fontSize = 12.sp, maxLines = 1)},
                                    selected = selected.value,
                                    onClick = {
                                        selected.value = !selected.value
                                        selectedCategoryList.remove(it)
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.Clear,"Clear")
                                    }
                                )
                            }
                        }
                }

                // TAglar
                Spacer(Modifier.padding(vertical = 18.dp))

                Text("Please select Tag",
                    modifier = Modifier.fillMaxWidth(0.9f),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.padding(vertical = 3.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)) {
                    val categories = arrayListOf(
                        "Yazılım",
                        "Yapay Zeka",
                        "Back-End",
                        "Front-End",
                        "Teknoloji",
                        "Örnek",
                        "Örnek2",
                        "Örnek3",
                        "Örnek4"
                    )
                    items(categories){
                        FilterChip(
                            modifier = Modifier.padding(end = 8.dp),
                            selected = selected.value,
                            label = { Text(text = it)},
                            onClick = {
                                selected.value = !selected.value
                                if(!selectedCategoryList.contains(it)){
                                    selectedCategoryList.add(it)
                                }else{
                                    selectedCategoryList.remove(it)
                                }
                            },
                            trailingIcon = if(selected.value){
                                {
                                    Icon(
                                        Icons.Filled.Done,null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            }
                            else {
                                null
                            }
                        )
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
                ){
                    FlowRow(
                        modifier = Modifier.padding(5.dp),
                        maxItemsInEachRow = 4,
                    ) {
                        selectedCategoryList.filterNotNull().forEach{
                            FilterChip(
                                modifier = Modifier.padding(end = 3.dp),
                                label = { Text(text = it, fontSize = 12.sp, maxLines = 1)},
                                selected = selected.value,
                                onClick = {
                                    selected.value = !selected.value
                                    selectedCategoryList.remove(it)
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.Clear,"Clear")
                                }
                            )
                        }
                    }
                }

            }
            Spacer(Modifier.padding(vertical = 20.dp))

            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 15.dp))
            {
                EventTrackerAppPrimaryButton("Complete") { }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun MyAccountScreenPrev() {
    EventTrackerAppTheme {
        MyAccountScreen()
    }
}