package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen()
{
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(modifier = Modifier
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
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 70.dp),
                contentPadding = PaddingValues(5.dp, vertical = 15.dp),
            ) {
                item()
                {
                    EventRow();
                    EventRow();
                    EventRow();
                }
            }
        }
    }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventRow()
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
        Image(painterResource(R.drawable.ic_launcher_background), null, modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .align(Alignment.CenterHorizontally)
            .clickable {  }
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


        Text(text="text", modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
            .clickable {  })


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
        HomeScreen();
    }
}