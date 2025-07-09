package com.example.eventtrackerapp.views

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.eventtrackerapp.common.BottomNavBar
import com.example.eventtrackerapp.common.EventTrackerExtendedFloatingActionButton


@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel:AuthViewModel,
    profile:Profile
    )
{
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar =
        {
            BottomNavBar(navController = navController)
        }


    val profilePhoto = rememberSaveable { mutableStateOf(profile.photo) }

    Scaffold(Modifier
        .fillMaxSize(),
        bottomBar = { BottomNavBar(navController = navController)}
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
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

                Text(
                    text = "${profile.fullName}",
                    fontSize = 20.sp
                )

                Text(
                    text = "${profile.email}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )

            // MY Account

            EventTrackerExtendedFloatingActionButton(
                text = "My Account",
                onClick = {
                    navController.navigate("my_account")
                    {
                        launchSingleTop = true
                    }
                },
                leadingIcon = Icons.Filled.Person,
                leadingIconDescription = "Person"
            )

            // My Preferences

            EventTrackerExtendedFloatingActionButton(
                text = "My Preferences",
                onClick = {
                    navController.navigate("preferences")
                    {
                        launchSingleTop = true
                    }
                },
                leadingIcon = Icons.Filled.Build,
                leadingIconDescription = "Person"
            )

            // My Events

            EventTrackerExtendedFloatingActionButton(
                text = "My Events",
                onClick = {
                    navController.navigate("my_events")
                    {
                        launchSingleTop = true
                    }
                },
                leadingIcon = Icons.Filled.Star,
                leadingIconDescription = "Person"
            )

            // Log Out

            EventTrackerExtendedFloatingActionButton(
                text = "Log Out",
                textColor = Color.Red,
                onClick = {
                    authViewModel.logOut()
                    navController.navigate("login_screen")
                    {
                        popUpTo("profile")
                        {
                            inclusive = true
                        }
                    }
                },
                leadingIcon = Icons.AutoMirrored.Filled.ExitToApp,
                leadingIconDescription = "Person",
                tint = Color.Red
            )
        }
    }
}