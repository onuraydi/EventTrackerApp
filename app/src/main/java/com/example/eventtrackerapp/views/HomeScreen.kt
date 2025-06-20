package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import android.graphics.pdf.content.PdfPageGotoLinkContent.Destination
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.ActivityNavigator
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.rememberNavController
import com.example.eventtrackerapp.MainActivity
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import kotlinx.coroutines.launch

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
        Box(modifier = Modifier
            .fillMaxSize())
        {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                contentPadding = PaddingValues(5.dp, vertical = 15.dp),
            ) {
                item()
                {
                    EventRow();
                    EventRow();
                }
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
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Box(modifier = Modifier.fillMaxHeight()) {

                    // Yorum listesi (scroll edilebilir)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 70.dp) // Altta yer bırakıyoruz sabit alan için
                    ) {
                        items(15) { // örnek olarak 10 yorum
                            comment()
                        }
                    }

                    // Sabit alt alan
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter) // <-- en altta sabit
                            .fillMaxWidth()
                            .background(Color.White) // opsiyonel: arka plan ver
                            .padding(10.dp)
                    ) {
                        Text("Yorum Ekle", modifier = Modifier.align(Alignment.CenterStart))
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

        Spacer(modifier = Modifier.width(10.dp)) // resim ile yazı arasında boşluk

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