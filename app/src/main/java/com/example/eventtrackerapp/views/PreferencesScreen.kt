package com.example.eventtrackerapp.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreferencesScreen(){
    EventTrackerAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {Text("Preferences")},
                    navigationIcon = {
                        IconButton(
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Go Back")
                              },
                            onClick = {}
                        )
                    }
                )
            }
        )
        { innerPadding->

            val myCategories = remember{mutableStateListOf<String?>("Seçili1","Seçili2","Seçili3")}
            val isSelected = rememberSaveable{mutableStateOf(false)}
            val isEdit = rememberSaveable { mutableStateOf(false) }


            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) {
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CardRow("Notifications") {
                        Icon(Icons.Default.Notifications,
                            "Notification",
                            Modifier.size(32.dp)
                        )
                    }
                    CardRow("Dark Mode"){
                        Icon(painter = painterResource(R.drawable.dark_mode_icon),
                            "DarkMode",
                            Modifier.size(32.dp)
                        )
                    }

                    Spacer(Modifier.padding(vertical = 12.dp))
                    Row {
                        Text("My Categories", modifier = Modifier.weight(5f))
                        Text(
                            text = if(isEdit.value){
                                "Confirm"
                            }else{
                                "Edit"
                            },
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isEdit.value = !isEdit.value
                                }
                        )
                    }

                    if(isEdit.value){
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
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
                            items(categories) {
                                FilterChip(
                                    modifier = Modifier.padding(end = 8.dp),
                                    selected = isSelected.value,
                                    label = { Text(it) },
                                    onClick = {
                                        isSelected.value = !isSelected.value
                                        if (myCategories.contains(it)) {
                                            myCategories.add(it)
                                        } else {
                                            myCategories.remove(it)
                                        }
                                    },
                                    trailingIcon = if (isSelected.value) {
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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .wrapContentHeight()
                            .defaultMinSize(minHeight = 80.dp)
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                    ){
                        FlowRow(
                            Modifier.padding(5.dp),
                            maxItemsInEachRow = 4
                        ) {
                            myCategories.filterNotNull().forEach {
                                FilterChip(
                                    modifier = Modifier.padding(end = 5.dp),
                                    selected = isSelected.value,
                                    onClick = {
                                        isSelected.value = !isSelected.value
                                        myCategories.remove(it)
                                    },
                                    label = {Text(it)},
                                    trailingIcon = {
                                        Icon(Icons.Default.Clear,"Clear")
                                    },
                                    enabled = isEdit.value
                                )
                            }

                        }

                    }


                }
            }

        }
    }
}

@Composable
fun CardRow(
    preference:String,
    leadingIcon: @Composable (() ->Unit)? = null,
)
{
    val isChecked = rememberSaveable { mutableStateOf(false) }
    Card(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp,Color.LightGray)
    ) {
        Row(
            Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp)
                .heightIn(min = 72.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon?.let { it() }

            Text(text = preference, fontSize = 21.sp, modifier = Modifier.weight(1f))

            Switch(
                checked = isChecked.value,
                onCheckedChange = {
                    isChecked.value = it
                }
            )

        }

    }

}
@Preview(showBackground = true)
@Composable
fun ShowMyScreen(){
    EventTrackerAppTheme {
        PreferencesScreen()
    }
}