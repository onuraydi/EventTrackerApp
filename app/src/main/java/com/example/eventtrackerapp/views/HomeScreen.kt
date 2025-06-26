package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    eventList: List<Event>,
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Notifications, contentDescription = null)
                    }
                },
                modifier = Modifier,
                title = {
                    Text(text = "Home")
                })
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(5.dp, vertical = 15.dp),
        ) {

            items(eventList)
            {
                EventRow(it, navController)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(onClick = {navController.navigate("addEvent")}, Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
            Icon(Icons.Filled.Add, null)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventRow(event:Event,navController: NavController)
{

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false);
    val scope = rememberCoroutineScope();
    var showBottomSheet by remember { mutableStateOf(false ) }


    var commentState = remember { mutableStateOf("")}

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(20.dp))
        )
    {
        Image(painterResource(event.image), null, modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .align(Alignment.CenterHorizontally)
            .clickable { navController.navigate("detail/${event.id}") }
            .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)),

            contentScale = ContentScale.Crop)


        Row() {
            Icon(Icons.Filled.FavoriteBorder,null, modifier = Modifier
                .padding(15.dp)
                .clickable {  })
            Icon(painterResource(R.drawable.baseline_chat_bubble_outline_24),null, modifier = Modifier
                .padding(15.dp)
                .clickable { showBottomSheet = true })
            Icon(Icons.Filled.Share ,null, modifier = Modifier
                .padding(15.dp)
                .clickable {  })
        }


        event.name?.let {
            Text(text= it, modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .clickable {  navController.navigate("detail/${event.id}") },)
        }


        if (showBottomSheet) {
            Column {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight(),
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 70.dp)
                        ) {
                            items(15) {
                                comment()
                            }
                        }


                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(10.dp)
                        ) {
                            Row() {
                                Image(
                                    painter = painterResource(R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .border(BorderStroke(2.dp, Color.Black), shape = CircleShape),

                                    contentScale = ContentScale.Fit,
                                )

                                Spacer(modifier = Modifier.width(20.dp))

                                EventTrackerAppOutlinedTextField("Etkinlik için Yorum Ekle...",commentState ,trailingIcon = {
                                    Icon(Icons.Filled.PlayArrow,null, modifier = Modifier
                                        .clickable {  })
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable

fun comment()
{
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .border(BorderStroke(2.dp, Color.Black), shape = CircleShape),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = "username",
                fontSize = 15.sp
            )
            Text(
                text = "yorum detayı",
                fontSize = 20.sp
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    EventTrackerAppTheme {
       // HomeScreen(eventList = listOf());
    }
}