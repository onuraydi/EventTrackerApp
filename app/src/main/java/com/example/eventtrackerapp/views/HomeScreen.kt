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
import com.example.eventtrackerapp.model.CommentWithProfileAndEvent
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.EventWithTags
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.BottomNavBar
import com.example.eventtrackerapp.utils.CommentBottomSheet
import com.example.eventtrackerapp.viewmodel.CommentViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.LikeViewModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    eventList: List<EventWithTags>,
    navController: NavController,
    commentViewModel: CommentViewModel,
    likeViewModel: LikeViewModel,
    profileId:String
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
                    IconButton(onClick = {navController.navigate("notification")}) {
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
                var commentList = commentViewModel.getComments(eventId = it.event.id)
                EventRow(it.event, navController,commentList,commentViewModel,profileId,likeViewModel)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun EventRow(event:Event,navController: NavController, commentList:Flow<List<CommentWithProfileAndEvent>>,commentViewModel:CommentViewModel,profileId:String,likeViewModel: LikeViewModel)
{
    var eventViewModel: EventViewModel = viewModel()


    var showBottomSheet by remember { mutableStateOf(false ) }

    val likeCount by likeViewModel.getLikeCount(event.id).collectAsState(initial = 0)
    val isLiked by likeViewModel.isLikedByUser(event.id, profileId).collectAsState(initial = false)

    val commentCount by commentViewModel.getCommentCount(event.id).collectAsState(initial = 0)


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
            if(isLiked == false)
            {
                Icon(Icons.Filled.FavoriteBorder,null, modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end=5.dp)
                    .clickable {
                        likeViewModel.likeEvent(eventId = event.id,profileId);
                    })
                Text(text = "${likeCount}",Modifier.align(Alignment.CenterVertically))
            }else
            {
                Icon(Icons.Filled.Favorite,null, modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end=5.dp)
                    .clickable {
                        likeViewModel.unlikeEvent(event.id,profileId)
                    })
                Text(text = "${likeCount}",Modifier.align(Alignment.CenterVertically))
            }

            Icon(painterResource(R.drawable.baseline_chat_bubble_outline_24),null, modifier = Modifier
                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end = 5.dp)
                .clickable { showBottomSheet = true })
            Text(text = "${commentCount}",Modifier.align(Alignment.CenterVertically))
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
            comments = commentList,
            currentUserImage = painterResource(R.drawable.ic_launcher_foreground),
            commentViewModel = commentViewModel,
            profileId = profileId,
            eventId = event.id
            )
        }
    }


//@Preview(showBackground = true)
//@Composable
//fun HomePreview() {
//    EventTrackerAppTheme {
//       // HomeScreen(eventList = listOf());
//    }
//}