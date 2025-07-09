package com.example.eventtrackerapp.views

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Profile
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.BottomNavBar
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.io.File


@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel:AuthViewModel,
    profile:Profile
    )
{


    val profilePhoto = rememberSaveable { mutableStateOf(profile.photo) }

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

            if(profilePhoto.value!=""){
                val imageFile = profilePhoto.value?.let { File(it) }
                if(imageFile!=null && imageFile.exists()){
                    AsyncImage(
                        model = imageFile,
                        null,
                        error = painterResource(R.drawable.clock_icon),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                println(profilePhoto.value)
                            }
                    )
                }else{
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                println(profilePhoto.value)
                            }
                    )
                }
            }


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


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    EventTrackerAppTheme {
//        ProfileScreen();
    }
}