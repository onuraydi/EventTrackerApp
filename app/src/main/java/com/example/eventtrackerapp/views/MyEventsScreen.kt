@file:Suppress("DEPRECATION")

package com.example.eventtrackerapp.views

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    myEvents:List<Event>,
    deleteEvent:(id:Int)->Unit
){
    val showDialog = remember { mutableStateOf(false) }
    val selectedEventId = remember { mutableIntStateOf(0) }

    EventTrackerAppTheme {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {Text("My Events")},
                    navigationIcon = {
                        IconButton(
                            content = {
                                Icon(Icons.Default.ArrowBack,"Go Back")
                            },
                            onClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        ) { innerPadding->
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myEvents){
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 72.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 72.dp),
                                //satırdaki elemanları ortaladı
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if(it.image!=""){
                                    val imageFile = it.image?.let{File(it)}
                                    if(imageFile!=null && imageFile.exists()){
                                        AsyncImage(
                                            model = imageFile,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                                .size(60.dp),
                                            contentDescription = "Profile",
                                        )
                                    }else{
                                        Image(
                                            painter = painterResource(R.drawable.ic_launcher_background),
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                                .size(60.dp),
                                            contentDescription = "Profile",
                                        )
                                    }
                                }

                                Column(
                                    Modifier
                                        .fillMaxHeight()
                                        .weight(10f)
                                        .padding(start = 12.dp)
                                ) {
                                    Text(
                                        text = it.name ?: "",
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = it.detail ?: "",
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis

                                    )
                                }

                                Spacer(Modifier.weight(1f))

                                Row(
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        content = {
                                            Icon(Icons.Default.Info, "Detail")
                                        },
                                        onClick = {
                                            navController.navigate("detail/${it.id}")
                                        }
                                    )
                                    IconButton(
                                        content = {
                                            Icon(Icons.Default.Edit, "Edit")
                                        },
                                        onClick = {
                                            navController.navigate("edit_event_screen/${it.id}")
                                        }
                                    )
                                    IconButton(
                                        content = {
                                            Icon(Icons.Default.Delete, "Delete")
                                        },
                                        onClick = {
                                            selectedEventId.intValue = it.id
                                            showDialog.value = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if(showDialog.value){
                    ShowAlertDialog(
                        dialogTitle = "Etkinlik Silinecek",
                        dialogText = "Eğer onaylarsan eklediğin etkinliği silmiş olacaksın." +
                                " Sildiğin etkinliği bir daha geri alamazsın",
                        onConfirmation = {deleteEvent(selectedEventId.value)},
                        onDismissRequest = {showDialog.value = false}
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAlertDialog(
    onConfirmation:()->Unit,
    onDismissRequest: ()->Unit,
    dialogTitle:String,
    dialogText:String
){
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ){
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp
        ) {
            Column(Modifier.padding(16.dp)){
                Text(text = dialogTitle, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.padding(vertical = 12.dp))
                Text(text = dialogText)
                Spacer(Modifier.padding(vertical = 16.dp))
                Row(
                    Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                        }
                    ){
                        Text("İptal")
                    }

                    TextButton(
                        onClick = {
                            onConfirmation()
                            onDismissRequest()
                        }
                    ){
                        Text("Sil", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyScreen(){
    EventTrackerAppTheme {
        //MyEventsScreen()
    }
}