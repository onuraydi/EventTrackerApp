package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.common.BottomNavBar
import com.example.eventtrackerapp.common.CommentBottomSheet
import com.example.eventtrackerapp.common.EventTrackerTopAppBar
import com.example.eventtrackerapp.common.SelectableImageBox
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.EventWithTags
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

        topBar =
        {
            EventTrackerTopAppBar(
                title = "Home",
                modifier = Modifier,
                showBackButton = false,
                actions = {
                    IconButton(
                        onClick = {navController.navigate("notification")}
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        },

        floatingActionButton =
        {
            FloatingActionButton(
                onClick = { navController.navigate("addEvent") })
            {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Event"
                )
            }
        },

        bottomBar =
        {
            BottomNavBar(
                navController = navController
            )
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(5.dp, vertical = 15.dp),
        ) {

            items(eventList)
            {
                val commentList = commentViewModel.getComments(eventId = it.event.id)
                EventRow(it.event, navController,commentList.value,commentViewModel,profileId,likeViewModel)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun EventRow(event: Event, navController: NavController, commentList:List<CommentWithProfileAndEvent>, commentViewModel:CommentViewModel, profileId:String, likeViewModel: LikeViewModel)
{
    var eventViewModel: EventViewModel = viewModel()


    var showBottomSheet by remember { mutableStateOf(false ) }

    val likeCount = likeViewModel.getLikeCountForEvent(event.id)
    val isLiked = likeViewModel.isEventLikedByUser(event.id,profileId)

    val commentCount by commentViewModel.getCommentCount(event.id).observeAsState(initial = 0)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                BorderStroke(
                    width = 2.dp,
                    color = Color.Black  // TODO Belki bu kısımdaki renk de dark mode için standartlaştırılabilir.
                ),
                shape = RoundedCornerShape(20.dp),
            )
    )
    {

        SelectableImageBox(
            boxWidth = 220.dp,
            boxHeight = 220.dp,
            imagePath = event.image,
            modifier = Modifier.fillMaxWidth(),
            placeHolder = painterResource(R.drawable.ic_launcher_background),
            shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
            onClick = {
                navController.navigate("detail/${event.id}")
            }
        )


        Row() {
            if(isLiked.value == false)
            {
                Icon(Icons.Filled.FavoriteBorder,null, modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end=5.dp)
                    .clickable {
                        likeViewModel.toggleLike(eventId = event.id,profileId);
                    })
                Text(text = "${likeCount}",Modifier.align(Alignment.CenterVertically))
            }else
            {
                Icon(Icons.Filled.Favorite,null, modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end=5.dp)
                    .clickable {
                        likeViewModel.toggleLike(event.id,profileId)
                    })
                Text(text = "${likeCount}",Modifier.align(Alignment.CenterVertically))
            }

            Icon(
                painter = painterResource(R.drawable.baseline_chat_bubble_outline_24),
                contentDescription = "Comments",
                modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp, end = 5.dp)
                    .clickable
                    {
                        showBottomSheet = true
                    }
            )
            Text(
                text = commentCount.toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                modifier = Modifier
                    .padding(15.dp)
                    .clickable { }
            )
        }


        event.name.let {
            Text(text= it, modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .clickable {  navController.navigate("detail/${event.id}") },
                fontWeight = W500, fontSize = 20.sp
            )
        }

        CommentBottomSheet(
            showSheet = showBottomSheet,
            onDismiss = { showBottomSheet = false },
            comments = commentList,
            currentUserImage = painterResource(R.drawable.ic_launcher_foreground),
            commentViewModel = commentViewModel,
            profileId = profileId,
            eventId = event.id
        )
    }
}