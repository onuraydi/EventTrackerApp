package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.CommentWithProfileAndEvent
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.CommentBottomSheet
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.CommentViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import kotlinx.coroutines.flow.Flow


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    event: Event,
    navController: NavController,
    category: Category,
    commentList: Flow<List<CommentWithProfileAndEvent>>,
    commentViewModel: CommentViewModel,
    profileId:String
)
{
    var showBottomSheet by remember { mutableStateOf(false) }
    val isLikeState = rememberSaveable {mutableStateOf(false)}
    var likeCount = remember(event.likeCount) { mutableStateOf(event.likeCount) }


    val commentCount = rememberSaveable { mutableStateOf(0) }  // TODO buraya daha sonra event.comment count gelecek
    var eventViewModel: EventViewModel = viewModel()

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text("Etkinlik Detay Sayfası", fontSize = 25.sp)
                },
                navigationIcon = {
                    Icon(Icons.Default.ArrowBack,null, modifier = Modifier
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
                /* resim yüklendiğinde sayfayı yüklemek için if kullanmadğımda hata veriyor*/
                if (event != null && event.image != null && event.image != 0) {
                    Image(
                        painter = painterResource(id = event.image),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.padding(top = 18.dp))

                event.name?.let { Text(text= it, fontSize = 30.sp, fontWeight = FontWeight.W500) }
                Spacer(Modifier.padding(top = 3.dp))
                category?.let {
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
                        // TODO buraya id ya da liste ile bir yönlendirme yapılmalı
                        navController.navigate("participants_screen")
                })
                Spacer(Modifier.padding(top = 5.dp))
                Row(modifier = Modifier
                    .clickable { navController.navigate("participants_screen") }) {
                    /*TODO: Buraya katılımcıların fotoğrafları gelecek ilk 4 tanesinin*/
                    Image(
                        painterResource(R.drawable.ic_launcher_foreground), contentDescription = null,
                        Modifier.border(BorderStroke(2.dp, Color.Black), shape = CircleShape)
                            .size(60.dp))
                    Spacer(Modifier.padding(start = 10.dp))
                    Image(
                        painterResource(R.drawable.ic_launcher_foreground), contentDescription = null,
                        Modifier.border(BorderStroke(2.dp, Color.Black), shape = CircleShape)
                            .size(60.dp))
                    Spacer(Modifier.padding(start = 10.dp))
                    Image(
                        painterResource(R.drawable.ic_launcher_foreground), contentDescription = null,
                        Modifier.border(BorderStroke(2.dp, Color.Black), shape = CircleShape)
                            .size(60.dp))
                    Spacer(Modifier.padding(start = 10.dp))

                    /*TODO: Buraya katılımıcıların sayısı gelecek*/
                    Text("+12 Kişi daha" ,fontWeight = FontWeight.W500, fontSize = 20.sp, textDecoration = TextDecoration.Underline,modifier =  Modifier
                        .align(Alignment.CenterVertically))
                }
                Spacer(Modifier.padding(top = 20.dp))
                Row(Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                    ExtendedFloatingActionButton(onClick = { },
                        icon = { Icon(Icons.Default.Add,null)},
                        text = { Text("Katıl")},
                        modifier = Modifier.weight(1f)
                    )

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
                    .border(BorderStroke(2.dp, color = Color.Black), shape = RoundedCornerShape(8.dp)),

                    ) {
                    Row(Modifier.weight(1f),horizontalArrangement = Arrangement.Center){
                        if (isLikeState.value == false) {
                            Icon(Icons.Filled.FavoriteBorder, null, modifier = Modifier
                                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp, end = 5.dp)
                                .clickable {
                                    isLikeState.value = true;
                                    likeCount.value++;
                                    eventViewModel.incrementLike(eventId = event.id)
                                })
                            Text(
                                text = "${likeCount.value}",
                                Modifier.align(Alignment.CenterVertically)
                            )
                        } else {
                            Icon(Icons.Filled.Favorite, null, modifier = Modifier
                                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp, end = 5.dp)
                                .clickable {
                                    isLikeState.value = false;
                                    likeCount.value--;
                                    eventViewModel.decrementLike(eventId = event.id)
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
                        Text(text = "${commentCount.value}",Modifier.align(Alignment.CenterVertically))

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


@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    EventTrackerAppTheme {
        //DetailScreen();
    }
}