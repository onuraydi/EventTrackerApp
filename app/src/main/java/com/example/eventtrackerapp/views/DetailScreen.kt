package com.example.eventtrackerapp.views

import android.view.ViewGroup.MarginLayoutParams
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen()
{
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false);
    var commentState = remember { mutableStateOf("")}


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
                    Icon(Icons.Default.ArrowBack,null, modifier = Modifier.padding(start = 8.dp))
                }
            )
        }
    ){ innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .padding(8.dp)
            .padding(bottom = 100.dp)
            .verticalScroll(rememberScrollState())) {
            Column() {
                Image(painterResource(R.drawable.ic_launcher_background),null, contentScale = ContentScale.Crop, modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp))

                Spacer(Modifier.padding(top = 18.dp))

                Text(text="Etkinlik Başlığı", fontSize = 30.sp, fontWeight = FontWeight.W500)
                Spacer(Modifier.padding(top = 3.dp))
                Text("Kategori:" + " Etkinlik Kategorisi")
                Spacer(Modifier.padding(top = 10.dp))
                Row(Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.DateRange,null)
                    Spacer(Modifier.padding(start = 5.dp))
                    Text("04.04.2025")

                    Spacer(Modifier.weight(1f))

                    Icon(Icons.Default.LocationOn,null)
                    Text("İstanbul/Türkiye")
                }

                Spacer(Modifier.padding(top = 20.dp))
                Text("Etkinlik Açıklaması", fontSize = 30.sp, fontWeight = FontWeight.W500)
                Spacer(Modifier.padding(top = 5.dp))
                Text("Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                        "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                        " when an unknown printer took a galley of type and scrambled it to make a type" +
                        " specimen book. It has survived not only five centuries, but also the leap into" +
                        " electronic typesetting, remaining essentially unchanged. It was popularised in " +
                        "the 1960s with the release of Letraset sheets containing Lorem Ipsum passages," +
                        " and more recently with desktop publishing software like Aldus PageMaker " +
                        "including versions of Lorem Ipsum", textAlign = TextAlign.Justify)

                Spacer(Modifier.padding(top = 20.dp))
                Text("Katılımcılar", fontSize = 30.sp, fontWeight = FontWeight.W500, modifier = Modifier.clickable {  })
                Spacer(Modifier.padding(top = 5.dp))
                Row() {
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
                    Text("+12 Kişi daha" ,fontWeight = FontWeight.W500, fontSize = 20.sp, textDecoration = TextDecoration.Underline,modifier =  Modifier
                        .clickable {  }
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

                    ExtendedFloatingActionButton(onClick = { },
                        icon = { Icon(Icons.Default.Place,null)},
                        text = { Text("Haritada Gör")},
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.padding(top = 20.dp))

                Row(Modifier.border(BorderStroke(2.dp, color = Color.Black), shape = RoundedCornerShape(8.dp))) {
                    IconButton(onClick = {},Modifier.weight(1f)) {
                        Icon(Icons.Default.FavoriteBorder,null)
                    }
                    IconButton(onClick = {showBottomSheet = true},Modifier.weight(1f)) {
                        Icon(painterResource(R.drawable.baseline_chat_bubble_outline_24),null)
                    }
                    IconButton(onClick = {},Modifier.weight(1f)) {
                        Icon(Icons.Default.Share,null)
                    }
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
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CommentDetail()
//{
//    var showBottomSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false);
//
//
//    Column(Modifier
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(onClick = {}) {
//
//        }
//    }
//}


@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    EventTrackerAppTheme {
        DetailScreen();
    }
}