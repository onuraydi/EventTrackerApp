package com.example.eventtrackerapp.views

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen()
{
    Scaffold(modifier = Modifier
        .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {Text("Katılımcılar")},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,null)
                    }
                }
            )
        }
        ){innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
            contentAlignment = Alignment.Center){


        LazyColumn(modifier = Modifier
            .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {

            items(5){
                user(R.drawable.ic_launcher_background,"Onur Aydı","deneme@gmail.com")
            }
        }
        }


    }
}

@Composable
fun user(image:Int,nameSurname:String, email:String)
{
    val userImage = remember { mutableStateOf(image) }
    val userNameSurname = remember { mutableStateOf(nameSurname) }
    val userMail = remember { mutableStateOf(email) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .heightIn(72.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Image(painterResource(userImage.value),null, modifier = Modifier
                .clip(CircleShape)
                .size(70.dp))


            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(10f)
                .padding(start = 12.dp)) {

                Text(
                    text = userNameSurname.value,
                    fontSize = 18.sp,
                    fontWeight = W500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = userMail.value,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun previewParticipans()
{
    EventTrackerAppTheme {
        ParticipantsScreen()
    }
}