package com.example.eventtrackerapp.views

import android.R
import android.app.Notification
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventtrackerapp.Greeting
import com.example.eventtrackerapp.common.EventTrackerTopAppBar
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import kotlinx.coroutines.processNextEventInCurrentThread
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController){
    Scaffold(modifier = Modifier
        .fillMaxSize(),
        topBar = {
            EventTrackerTopAppBar(
                title = "Bildirimler",
                modifier = Modifier,
                showBackButton = true,
                onBackClick =
                {
                    navController.popBackStack()
                },
            )
        }
    )
    { innerPadding ->
        LazyColumn(Modifier
            .padding(innerPadding)
            .padding(10.dp)
            .fillMaxSize()) {
            item()
            {
                Notification("Bildirim deneme Bildirim deneme Bildirim deneme Bildirim deneme Bildirim deneme Bildirim deneme Bildirim deneme Bildirim deneme Bildirim deneme")
                Notification("Bildirim deneme Bildirim deneme Bildirim deneme")
                Notification("Bildirim deneme Bildirim deneme Bildirim deneme")
            }
        }
    }
}

@Composable
private fun Notification(text:String){
    var expanded by remember { mutableStateOf(false) }
    var cutIndex by remember { mutableStateOf(text.length) }
    var safeCutIndex by remember { mutableStateOf(text.length) }
    var readyToDraw by remember { mutableStateOf(false)}

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (expanded) text else text.substring(0,safeCutIndex).trimEnd() + "...",
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    onTextLayout = {
                        if (!readyToDraw && it.lineCount > 0){
                            cutIndex = it.getLineEnd(0)
                            safeCutIndex = text.substring(0,cutIndex).lastIndexOf(' ').takeIf { it != -1 } ?: cutIndex
                            readyToDraw = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {expanded = !expanded}) {
                    Icon(
                        imageVector = if(expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
            if (expanded && cutIndex < text.length)
            {

                Text(text = text.substring(safeCutIndex).trimStart())
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun NotificationPreview() {
//    EventTrackerAppTheme {
////        NotificationScreen()
//    }
//}