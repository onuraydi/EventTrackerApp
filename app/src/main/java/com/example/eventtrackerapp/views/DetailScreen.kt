package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.common.CommentBottomSheet
import com.example.eventtrackerapp.common.SelectableImageBox
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.CommentWithProfileAndEvent
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.viewmodel.CommentViewModel
import com.example.eventtrackerapp.viewmodel.LikeViewModel
import com.example.eventtrackerapp.viewmodel.ParticipantsViewModel
import kotlinx.coroutines.flow.Flow
import java.io.File


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    event: Event,
    navController: NavController,
    category: Category,
    commentList: List<CommentWithProfileAndEvent>,
    commentViewModel: CommentViewModel,
    likeViewModel:LikeViewModel,
    profileId:String,
    participantsViewModel: ParticipantsViewModel,
)
{
    var showBottomSheet by remember { mutableStateOf(false) }

    val likeCount = likeViewModel.getLikeCountForEvent(event.id).collectAsState(0)
    val isLiked = likeViewModel.isEventLikedByUser(event.id,profileId).collectAsState(0)

    val commentCount by commentViewModel.getCommentCount(event.id).collectAsState(0)

    val state by participantsViewModel.hasUserParticipated(event.id,profileId).collectAsState(false)

    val participantsCount by participantsViewModel.getParticipationCount(event.id).collectAsState(0)

    val participants by participantsViewModel.getParticipantsForEvent(event.id).collectAsState(
        emptyList()
    )

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
                title = {
                    Text("Etkinlik Detay Sayfası", fontSize = 25.sp)
                },
                navigationIcon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,null, modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { navController.popBackStack() })
                }
            )
        }
    ){ innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())) {
            Column() {

                if (event != null && event.image != null && event.image != "") {
                    SelectableImageBox(
                        boxWidth = 200.dp,
                        boxHeight = 200.dp,
                        imagePath = event.image,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        placeHolder = painterResource(R.drawable.ic_launcher_background)
                    )
                }

                Spacer(Modifier.padding(top = 18.dp))

                event.name.let { Text(text= it, fontSize = 30.sp, fontWeight = FontWeight.W500) }
                Spacer(Modifier.padding(top = 3.dp))
                category.let {
                    Text("Kategori:" + it.name)
                }
                Spacer(Modifier.padding(top = 10.dp))
                Row(Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.DateRange,null)
                    Spacer(Modifier.padding(start = 5.dp))
                    Text(text = event.date.toString())

                    Spacer(Modifier.weight(1f))

                    Icon(Icons.Default.LocationOn,null)
                    event.location?.let { Text(text = it) }
                }

                Spacer(Modifier.padding(top = 20.dp))
                Text("Etkinlik Açıklaması", fontSize = 30.sp, fontWeight = FontWeight.W500)
                Spacer(Modifier.padding(top = 5.dp))
                Text(text = event.detail.toString(), textAlign = TextAlign.Justify)

                Spacer(Modifier.padding(top = 20.dp))
                Text("Katılımcılar", fontSize = 30.sp, fontWeight = FontWeight.W500, modifier = Modifier
                    .clickable {
                        navController.navigate("participants_screen/${event.id}")
                })
                Spacer(Modifier.padding(top = 5.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                    .clickable { navController.navigate("participants_screen/${event.id}") }) {
                    items(participants.take(3)){participant->
                        //.take(3) ilk 3 elemanı alır. Eğer 3'ten fazla olursa aşağısı çalışır
                        //itemsIndexed de olabilir.
                        SelectableImageBox(
                            boxWidth = 60.dp,
                            boxHeight = 60.dp,
                            imagePath = participant?.photo,
                            modifier = Modifier,
                            placeHolder = painterResource(R.drawable.ic_launcher_foreground),
                            shape = CircleShape,
                            borderStroke = BorderStroke(2.dp,MaterialTheme.colorScheme.primaryContainer)
                        )
                        Spacer(Modifier.padding(start = 10.dp))
                    }
                    if(participantsCount > 3){
                        item {
                            Text("+${participantsCount - 3} Kişi daha" ,fontWeight = FontWeight.W500, fontSize = 20.sp, textDecoration = TextDecoration.Underline,modifier =  Modifier)
                        }
                    }
                }
                Spacer(Modifier.padding(top = 20.dp))
                Row(Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                    // TODO etkinliğe katılıp katılmadığının kontorlü yapılarak butonun görünümü vb. değişecek
                    if (!state)
                    {
                        ExtendedFloatingActionButton(onClick = {participantsViewModel.toggleAttendance(profileId = profileId, eventId = event.id) },
                            icon = { Icon(Icons.Default.Add,null)},
                            text = { Text("Katıl")},
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else{
                        ExtendedFloatingActionButton(onClick = {participantsViewModel.toggleAttendance(event.id,profileId)},
                            icon = {Icon(Icons.Default.Clear,null)},
                            text = { Text("Vazgeç")},
                            modifier = Modifier.weight(1f)
                        )
                    }


                    // TODO Buraya şimdilik bir atama yapılmayacak zaman kalırsa uygulanır
                    ExtendedFloatingActionButton(onClick = { },
                        icon = { Icon(Icons.Default.Place,null)},
                        text = { Text("Haritada Gör")},
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.padding(top = 20.dp))
                Row(Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(2.dp, color = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(8.dp)),

                    ) {
                    Row(Modifier.weight(1f),horizontalArrangement = Arrangement.Center){
                        if (isLiked.value == false) {
                            Icon(Icons.Filled.FavoriteBorder, null, modifier = Modifier
                                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp, end = 5.dp)
                                .clickable {
                                    likeViewModel.toggleLike(event.id,profileId)
                                })
                            Text(
                                text = "${likeCount.value}",
                                Modifier.align(Alignment.CenterVertically)
                            )
                        } else {
                            Icon(Icons.Filled.Favorite, null, modifier = Modifier
                                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp, end = 5.dp)
                                .clickable {
                                    likeViewModel.toggleLike(event.id,profileId)
                                })
                            Text(
                                text = "${likeCount.value}",
                                Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }

                    Row(Modifier.weight(1f),horizontalArrangement = Arrangement.Center) {
                        Icon(painterResource(R.drawable.baseline_chat_bubble_outline_24),null, modifier = Modifier
                            .padding(start = 15.dp, top = 15.dp, bottom = 15.dp,end = 5.dp)
                            .clickable { showBottomSheet = true })
                        Text(text = "${commentCount}",Modifier.align(Alignment.CenterVertically))

                    }
                    Row(Modifier.weight(1f),horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Filled.Share ,null, modifier = Modifier
                            .padding(15.dp)
                            .clickable {  })
                    }
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
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DetailScreenPreview() {
//    EventTrackerAppTheme {
//        //DetailScreen();
//    }
//}