package com.example.eventtrackerapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.utils.BottomNavBar


@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel:AuthViewModel,
    profile: Profile
    )
{



    Scaffold(Modifier
        .fillMaxSize(),
        bottomBar = { BottomNavBar(navController = navController)}
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(bottom = 70.dp)
            .fillMaxSize()
            .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
                Image(painterResource(R.drawable.ic_launcher_background),null,Modifier
                    .clip(CircleShape)
                )

                Spacer(Modifier.padding(10.dp))

                Text("${profile.fullName}", fontSize = 20.sp)
                Text("${profile.email}", fontSize = 14.sp, fontWeight = FontWeight.Light)

            // Hesabım

            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate("my_account"){
                        launchSingleTop = true
                    }
                },
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                    .background(MaterialTheme.colorScheme.background))
                {
                Row(Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person,null)
                    Spacer(Modifier.padding(8.dp))
                    Text("Hesabım",)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.KeyboardArrowRight,null)
                }
            }

            // Tercihler

            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate("preferences"){
                        launchSingleTop = true
                    }
            },
                Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                .background(MaterialTheme.colorScheme.background))
            {
                Row(Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Build,null)
                    Spacer(Modifier.padding(8.dp))
                    Text("Tercihler",)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.KeyboardArrowRight,null)
                }
            }

            // Eklediğim Etkinlikler

            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate("my_events"){
                        launchSingleTop = true
                    }
            },
                Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                .background(MaterialTheme.colorScheme.background))
            {
                Row(Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star,null)
                    Spacer(Modifier.padding(8.dp))
                    Text("Eklediğim Etkinlikler",)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.KeyboardArrowRight,null)
                }
            }

            // Çıkış yap

            ExtendedFloatingActionButton(
                onClick = {
                    authViewModel.logOut()
                    navController.navigate("login_screen"){
                        popUpTo("profile"){
                            inclusive = true
                        }
                    }
            },
                Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                .background(MaterialTheme.colorScheme.background))
            {
                Row(Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ExitToApp,null, tint = Color.Red)
                    Spacer(Modifier.padding(8.dp))
                    Text("Çıkış Yap", color = Color.Red)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.KeyboardArrowRight,null, tint = Color.Red)
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ProfilePreview() {
//    EventTrackerAppTheme {
////        ProfileScreen();
//    }
//}