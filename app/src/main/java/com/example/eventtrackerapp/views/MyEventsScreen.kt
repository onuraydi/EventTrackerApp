package com.example.eventtrackerapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(navController: NavController){
    EventTrackerAppTheme {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {Text("My Events")},
                    navigationIcon = {
                        IconButton(
                            content = {
                                Icon(Icons.Default.ArrowBack,"Go Back")
                            },
                            onClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        ) { innerPadding->
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                /*TODO VERİLER SENKRON GELDİKTEN SONRA NAVİGASYONLAR YAPILACAK*/
                val eventList : List<Map<String,String>> = listOf(
                    mapOf("isim" to "EtkinlikasdasdAdsaD ASDADas1", "detay" to "Detay1"),
                    mapOf("isim" to "Etkinlik2", "detay" to "Detay2"),
                    mapOf("isim" to "Etkinlik3", "detay" to "Detay3"),
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(eventList){
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 72.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 72.dp),
                                //satırdaki elemanları ortaladı
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .size(60.dp),
                                    painter = painterResource(R.drawable.ic_launcher_foreground),
                                    contentDescription = "Profile",
                                )

                                Column(
                                    Modifier
                                        .fillMaxHeight()
                                        .weight(10f)
                                        .padding(start = 12.dp)
                                ) {
                                    Text(
                                        text = it["isim"] ?: "",
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = it["detay"] ?: "",
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis

                                    )
                                }

                                Spacer(Modifier.weight(1f))

                                Row(
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        content = {
                                            Icon(Icons.Default.Info, "Detail")
                                        },
                                        onClick = {

                                        }
                                    )
                                    IconButton(
                                        content = {
                                            Icon(Icons.Default.Edit, "Edit")
                                        },
                                        onClick = {}
                                    )
                                    IconButton(
                                        content = {
                                            Icon(Icons.Default.Delete, "Edit")
                                        },
                                        onClick = {}
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyScreen(){
    EventTrackerAppTheme {
        //MyEventsScreen()
    }
}