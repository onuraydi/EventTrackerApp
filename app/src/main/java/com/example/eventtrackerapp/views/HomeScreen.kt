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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.BottomNavBar
import com.example.eventtrackerapp.utils.CommentBottomSheet
import com.example.eventtrackerapp.viewmodel.EventViewModel

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
                    Text(text = "Ana Sayfa")
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {navController.navigate("addEvent")}) {
                Icon(Icons.Filled.Add, null)
            }
        },

        bottomBar = {BottomNavBar(navController = navController)}
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
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun EventRow(event:Event,navController: NavController)
{
    var eventViewModel: EventViewModel = viewModel()


    var showBottomSheet by remember { mutableStateOf(false ) }


    // TODO Bu kısımda bir sıkıntı var icon tutulmuyor çözümü ise kullanıcı ekledikten sonra ilişki
    //  ile kullanıcının like durumunu tutmak
    val isLikeState = rememberSaveable {mutableStateOf(false)}
    var likeCount = remember(event.likeCount) { mutableStateOf(event.likeCount) }


    val commentCount = rememberSaveable { mutableStateOf(0) }  // TODO buraya daha sonra event.comment count gelecek

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
            if(isLikeState.value == false)
            {
                Icon(Icons.Filled.FavoriteBorder,null, modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end=5.dp)
                    .clickable {
                        isLikeState.value = true;
                        likeCount.value++;
                        eventViewModel.incrementLike(eventId = event.id)
                    })
                Text(text = "${likeCount.value}",Modifier.align(Alignment.CenterVertically))
            }else
            {
                Icon(Icons.Filled.Favorite,null, modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end=5.dp)
                    .clickable {
                        isLikeState.value = false;
                        likeCount.value--;
                        eventViewModel.decrementLike(eventId = event.id)
                    })
                Text(text = "${likeCount.value}",Modifier.align(Alignment.CenterVertically))
            }

            Icon(painterResource(R.drawable.baseline_chat_bubble_outline_24),null, modifier = Modifier
                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end = 5.dp)
                .clickable { showBottomSheet = true })
            Text(text = "${commentCount.value}",Modifier.align(Alignment.CenterVertically))
            Icon(Icons.Filled.Share ,null, modifier = Modifier
                .padding(15.dp)
                .clickable {  })
        }


        event.name?.let {
            Text(text= it, modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .clickable {  navController.navigate("detail/${event.id}") },
                fontWeight = W500, fontSize = 20.sp
            )
        }

        CommentBottomSheet(
            showSheet = showBottomSheet,
            onDismiss = {showBottomSheet = false},
            comments = arrayListOf(),
            onSendComment = {},
            currentUserImage = painterResource(R.drawable.ic_launcher_foreground),
            currentUserName = "deneme"
            )
        }
    }


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    EventTrackerAppTheme {
       // HomeScreen(eventList = listOf());
    }
}